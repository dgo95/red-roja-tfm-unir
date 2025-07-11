package es.juventudcomunista.redroja.cjcrest.keycloak.dto.service;


import es.juventudcomunista.redroja.cjccommonutils.enums.Rol;
import es.juventudcomunista.redroja.cjcrest.keycloak.dto.GroupRep;
import es.juventudcomunista.redroja.cjcrest.keycloak.dto.KeycloakTokenResponse;
import es.juventudcomunista.redroja.cjcrest.keycloak.dto.UserRep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakAdminService {

    private final RestTemplate rest;

    @Value("${keycloak.server-url}")
    private String serverUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.client-id}")
    private String clientId;
    @Value("${keycloak.client-secret}")
    private String clientSecret;

    /* ---------- token cache ---------- */
    private String cachedToken;
    private Instant tokenExpiry = Instant.EPOCH;

    private String getAdminToken() {

        if (cachedToken != null && Instant.now().isBefore(tokenExpiry.minusSeconds(30))) {
            return cachedToken;                 // sigue siendo válido
        }

        String tokenUrl = "%s/realms/%s/protocol/openid-connect/token".formatted(serverUrl, realm);

        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "grant_type=client_credentials"
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret;

        var res = rest.postForEntity(tokenUrl, new HttpEntity<>(body, h), KeycloakTokenResponse.class);

        cachedToken = Objects.requireNonNull(res.getBody()).getAccessToken();
        tokenExpiry = Instant.now().plusSeconds(res.getBody().getExpiresIn());

        return cachedToken;
    }

    /* --------------------------------------------------------------------- */
    /* ---------------------------  BÚSQUEDAS  ----------------------------- */
    /* --------------------------------------------------------------------- */

    public UserRep findUserByMilitanteId(String militanteId) {

        String url = "%s/admin/realms/%s/users?q=militanteId:%s&exact=true"
                .formatted(serverUrl, realm, UriUtils.encode(militanteId, StandardCharsets.UTF_8));

        List<UserRep> list = exchange(url,
                new ParameterizedTypeReference<>() {
                });

        if (list.isEmpty()) {
            throw new IllegalStateException("No existe usuario con militanteId=" + militanteId);
        }
        return list.getFirst();
    }

    public GroupRep findGroupByPath(String fullPath) {

        String url = UriComponentsBuilder
                .fromHttpUrl(serverUrl)
                .path("/admin/realms/{realm}/group-by-path")
                .path(fullPath)                           // se codifica automáticamente
                .buildAndExpand(realm)
                .toUriString();

        return exchange(url);
    }

    public List<GroupRep> children(String parentId) {

        String url = "%s/admin/realms/%s/groups/%s/children"
                .formatted(serverUrl, realm, parentId);

        return exchange(url,
                new ParameterizedTypeReference<>() {
                });
    }

    /* --------------------------------------------------------------------- */
    /* ---------------------------  MUTACIONES ----------------------------- */
    /* --------------------------------------------------------------------- */

    /**
     * Elimina completamente un usuario de Keycloak.
     *
     * @param userId ID del usuario a eliminar.
     */
    public void deleteUser(String userId) {
        String url = String.format("%s/admin/realms/%s/users/%s", serverUrl, realm, userId);
        rest.exchange(url, HttpMethod.DELETE, new HttpEntity<>(authHeaders()), Void.class);
        log.info("[KC] Usuario {} eliminado", userId);
    }

    /**
     * Elimina a un usuario de un grupo concreto en Keycloak.
     *
     * @param userId  ID del usuario.
     * @param groupId ID del grupo del que se va a desasignar.
     */
    public void removeUserFromGroup(String userId, String groupId) {
        String url = String.format("%s/admin/realms/%s/users/%s/groups/%s", serverUrl, realm, userId, groupId);
        rest.exchange(url, HttpMethod.DELETE, new HttpEntity<>(authHeaders()), Void.class);
        log.info("[KC] Usuario {} eliminado del grupo {}", userId, groupId);
    }

    /**
     * Mueve un usuario de un grupo a otro: primero lo quita del grupo origen y luego lo añade al grupo destino.
     *
     * @param userId        ID del usuario.
     * @param sourceGroupId ID del grupo de origen.
     * @param targetGroupId ID del grupo de destino.
     */
    public void moveUserBetweenGroups(String userId, String sourceGroupId, String targetGroupId) {
        removeUserFromGroup(userId, sourceGroupId);
        addUserToGroup(userId, targetGroupId);
        log.info("[KC] Usuario {} movido del grupo {} al grupo {}", userId, sourceGroupId, targetGroupId);
    }


    public void removeAllMembers(String groupId) {

        // GET miembros actuales
        String url = "%s/admin/realms/%s/groups/%s/members?briefRepresentation=true"
                .formatted(serverUrl, realm, groupId);

        List<UserRep> users = exchange(url,
                new ParameterizedTypeReference<>() {
                });

        users.forEach(u ->
                rest.exchange("%s/admin/realms/%s/users/%s/groups/%s"
                                .formatted(serverUrl, realm, u.getId(), groupId),
                        HttpMethod.DELETE,
                        new HttpEntity<>(authHeaders()),
                        Void.class));
    }

    public void addUserToGroup(String userId, String groupId) {
        rest.exchange("%s/admin/realms/%s/users/%s/groups/%s"
                        .formatted(serverUrl, realm, userId, groupId),
                HttpMethod.PUT,
                new HttpEntity<>(authHeaders()),
                Void.class);
    }

    /**
     * Vacía el subgrupo; si queda sin miembros lo elimina.
     */
    private void clearAndMaybeDelete(String groupId) {

        removeAllMembers(groupId);

        // ¿sigue vacío?
        String url = "%s/admin/realms/%s/groups/%s/members?max=1"
                .formatted(serverUrl, realm, groupId);

        List<?> still = exchange(url,
                new ParameterizedTypeReference<List<?>>() {
                });

        if (still.isEmpty()) {                           // eliminar
            rest.exchange("%s/admin/realms/%s/groups/%s"
                            .formatted(serverUrl, realm, groupId),
                    HttpMethod.DELETE,
                    new HttpEntity<>(authHeaders()),
                    Void.class);
            log.info("[KC] Subgrupo {} eliminado (vacío)", groupId);
        }
    }


    /**
     * Devuelve el id de un subgrupo “rol” (lo crea si no existe)
     */
    public String ensureRoleSubGroup(GroupRep parent, String subGroupName) {

        return children(parent.getId()).stream()
                .filter(g -> g.getName().equals(subGroupName))
                .map(GroupRep::getId)
                .findFirst()
                .orElseGet(() -> createSubGroup(parent.getId(), subGroupName));
    }

    private String createSubGroup(String parentId, String name) {

        var body = Map.of("name", name);
        ResponseEntity<GroupRep> res = rest.exchange(
                "%s/admin/realms/%s/groups/%s/children".formatted(serverUrl, realm, parentId),
                HttpMethod.POST,
                new HttpEntity<>(body, authHeaders()),
                GroupRep.class);

        return Objects.requireNonNull(res.getBody()).getId();
    }

    /* --------------------------------------------------------------------- */
    /* --------------------------  API PÚBLICA  ---------------------------- */
    /* --------------------------------------------------------------------- */

    /**
     * Sincroniza en Keycloak las responsabilidades de un colectivo.
     *
     * @param fullPath         ruta KC del colectivo (c.getFullPath()).
     * @param mapaRolMilitante map< Rol , militanteId >
     */
    public void syncRoles(String fullPath, Map<Rol, String> mapaRolMilitante) {

        GroupRep colectivo = findGroupByPath(fullPath);

        for (Rol rol : Rol.values()) {

            if (rol.equals(Rol.ADMIN) || rol.equals(Rol.MIEMBRO)) {
                continue;
            }

            String subName = "@" + rol.getSubgrpsKey();
            String subId = children(colectivo.getId()).stream()
                    .filter(g -> g.getName().equals(subName))
                    .map(GroupRep::getId)
                    .findFirst()
                    .orElse(null);

            String militanteId = mapaRolMilitante.get(rol);    // puede ser null

            if (militanteId == null || militanteId.isBlank()) {
                if (subId != null) {                     // no asignado ⇒ vaciar/eliminar
                    clearAndMaybeDelete(subId);
                }
                continue;
            }

            // — hay responsable —
            UserRep user = findUserByMilitanteId(militanteId);
            if (subId == null) {
                subId = createSubGroup(colectivo.getId(), subName);
            }
            removeAllMembers(subId);
            addUserToGroup(user.getId(), subId);

            log.info("[KC] Rol {} → {} en {}", rol, user.getUsername(), fullPath);
        }
    }



    public void executeActionsEmail(String userId,
                                    List<String> actions,
                                    Integer lifespanSeconds,
                                    String clientId,
                                    String redirectUri) {

        UriComponentsBuilder b = UriComponentsBuilder
                .fromHttpUrl("%s/admin/realms/%s/users/%s/execute-actions-email"
                        .formatted(serverUrl, realm, userId))
                .queryParam("lifespan", lifespanSeconds);

        if (redirectUri != null) {
            b.queryParam("redirect_uri", redirectUri)
                    .queryParam("client_id", clientId);          // doc dice: obligatorio si se manda redirect_uri
        }

        rest.exchange(b.toUriString(), HttpMethod.PUT,
                new HttpEntity<>(actions, authHeaders()),
                Void.class);
    }


    /* --------------------------------------------------------------------- */

    private HttpHeaders authHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(getAdminToken());
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    private <T> T exchange(String url) {
        return rest.exchange(url, HttpMethod.GET, new HttpEntity<>(authHeaders()), (Class<T>) GroupRep.class).getBody();
    }

    private <T> T exchange(String url, ParameterizedTypeReference<T> type) {
        return rest.exchange(url, HttpMethod.GET, new HttpEntity<>(authHeaders()), type).getBody();
    }

    public String createUser(UserRep rep) {

        String url = "%s/admin/realms/%s/users".formatted(serverUrl, realm);
        ResponseEntity<Void> res = rest.exchange(
                url, HttpMethod.POST,
                new HttpEntity<>(rep, authHeaders()),
                Void.class);

        return Objects.requireNonNull(res.getHeaders().getLocation())
                .getPath()
                .replaceAll(".*/", "");
    }

    /* ---------- enviar e-mail verificación ---------- */
    public void sendVerifyEmail(String userId, String redirectUri) {

        String url = UriComponentsBuilder
                .fromHttpUrl("%s/admin/realms/%s/users/%s/send-verify-email"
                        .formatted(serverUrl, realm, userId))
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .toUriString();

        rest.exchange(url, HttpMethod.PUT,
                new HttpEntity<>(authHeaders()), Void.class);
    }
}


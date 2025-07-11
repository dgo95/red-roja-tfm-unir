package es.juventudcomunista.redroja.cjccommonutils.service;

import es.juventudcomunista.redroja.cjccommonutils.enums.Rol;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorizationService {

    private static final Pattern SCOPE_PATTERN = Pattern.compile("([A-Z])(\\d+)");

    public boolean hasRoleForCommittee(Authentication auth, Rol rol, long committeeId) {
        return scopeOf(auth, rol)
                .filter(scope -> scope.committeeIds().contains(committeeId))
                .isPresent();
    }

    public boolean hasRoleForCollective(Authentication auth, Rol rol, long collectiveId) {
        return scopeOf(auth, rol)
                .filter(scope -> scope.collectiveIds().contains(collectiveId))
                .isPresent();
    }

    public boolean isMemberOfCommittee(Authentication auth, long committeeId) {
        Jwt jwt = asJwt(auth);
        var groups = jwt.getClaimAsStringList("groups");
        if (groups == null) return false;

        return groups.stream()
                .map(this::committeeIdFromPath)
                .filter(OptionalLong::isPresent)
                .mapToLong(OptionalLong::getAsLong)
                .anyMatch(id -> id == committeeId);
    }

    public boolean isMemberOfAll(Authentication auth, List<String> ids){
        Jwt jwt = asJwt(auth);

        @SuppressWarnings("unchecked")
        Map<String, Object> subgrups = jwt.getClaim("subgrups");
        if (subgrups == null) return false;

        Object rawMb = subgrups.get("MB");       // Nos quedamos solo con la clave MB
        if (rawMb == null) return false;

        String mbScope = rawMb.toString();
        if (mbScope.isBlank()) return false;

        // Dividimos por ":" y comprobamos que contenga todos los ids solicitados
        Set<String> mbTokens = Arrays.stream(mbScope.split(":"))
                .filter(token -> !token.isBlank())
                .collect(Collectors.toSet());

        return mbTokens.containsAll(ids);
    }

    public boolean hasAnyRoleForCommittee(Authentication auth,
                                          long committeeId,
                                          Rol... roles) {
        return Arrays.stream(roles)
                .anyMatch(rol -> hasRoleForCommittee(auth, rol, committeeId));
    }

    public boolean hasAnyRoleForCollective(Authentication auth,
                                           long collectiveId,
                                           Rol... roles) {
        return Arrays.stream(roles)
                .anyMatch(rol -> hasRoleForCollective(auth, rol, collectiveId));
    }

    /* ---------- Implementación interna ---------- */

    private Optional<RoleScope> scopeOf(Authentication auth, Rol rol) {
        Jwt jwt = asJwt(auth);
        @SuppressWarnings("unchecked")
        Map<String, Object> subgrps = jwt.getClaim("subgrups");
        if (subgrps == null) return Optional.empty();

        String rawScope = (String) subgrps.get(rol.getSubgrpsKey());
        if (rawScope == null || rawScope.isBlank()) return Optional.empty();

        Set<Long> committees  = new HashSet<>();
        Set<Long> collectives = new HashSet<>();

        for (String token : rawScope.split(":")) {
            Matcher m = SCOPE_PATTERN.matcher(token);
            if (!m.matches()) continue;

            long id = Long.parseLong(m.group(2));
            switch (m.group(1)) {
                case "C" -> committees.add(id);
                case "B" -> collectives.add(id);
                default  -> log.warn("Ámbito desconocido: {}", token);
            }
        }
        return Optional.of(new RoleScope(committees, collectives));
    }

    private OptionalLong committeeIdFromPath(String path) {
        int lastUnderscore = path.lastIndexOf('_');
        if (lastUnderscore < 0) return OptionalLong.empty();
        try {
            return OptionalLong.of(Long.parseLong(path.substring(lastUnderscore + 1)));
        } catch (NumberFormatException e) {
            log.debug("ID no numérico en {}", path);
            return OptionalLong.empty();
        }
    }

    private Jwt asJwt(Authentication auth) {
        return ((JwtAuthenticationToken) auth).getToken();
    }

    private record RoleScope(Set<Long> committeeIds, Set<Long> collectiveIds) { }
}


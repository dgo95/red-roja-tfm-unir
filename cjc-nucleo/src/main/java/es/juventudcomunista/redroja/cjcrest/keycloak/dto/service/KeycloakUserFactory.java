package es.juventudcomunista.redroja.cjcrest.keycloak.service;

import es.juventudcomunista.redroja.cjcrest.entity.Militante;
import es.juventudcomunista.redroja.cjcrest.keycloak.dto.UserRep;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class KeycloakUserFactory {

    public UserRep build(@NonNull Militante m) {

        var rep = new UserRep();
        rep.setUsername(m.getNumeroCarnet());
        rep.setEmail(m.getEmail());
        rep.setFirstName(m.getNombre());
        rep.setLastName(Stream.of(m.getApellido(), m.getApellido2())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" ")));
        rep.setEnabled(true);
        rep.setEmailVerified(false);             // todavía no

        // Acciones obligatorias
        rep.setRequiredActions(List.of("VERIFY_EMAIL", "UPDATE_PASSWORD"));

        // Atributos personalizados
        rep.setAttributes(Map.of(
                "militanteId", List.of(m.getMilitanteId()),
                "sexo",        List.of(m.getSexo().name())
        ));

        return rep;
    }
}

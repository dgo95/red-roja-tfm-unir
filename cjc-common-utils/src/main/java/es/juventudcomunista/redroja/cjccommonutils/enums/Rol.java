package es.juventudcomunista.redroja.cjccommonutils.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Rol {

    ADMIN           ("ROLE_ADMIN", "ADMIN"),
    AGITPROP        ("ROLE_AGITPROP"       , "AG"),
    DIR_POLITICA    ("ROLE_DIR_POLITICA"   , "DP"),
    ESTUDIANTIL     ("ROLE_ESTUDIANTIL"    , "ES"),
    FINANZAS        ("ROLE_FINANZAS"       , "FI"),
    FORMACION       ("ROLE_FORMACION"      , "FO"),
    GARANTIAS_CTRL  ("ROLE_GARANTIAS_CTRL" , "GC"),
    MASAS           ("ROLE_MASAS"          , "MA"),
    MIEMBRO         ("ROLE_MIEMBRO"        , "MB"),
    MUJER           ("ROLE_MUJER"          , "MU"),
    OBRERO          ("ROLE_OBRERO"         , "OB"),
    ORGANIZACION    ("ROLE_ORGANIZACION"   , "OR"),
    VECINAL         ("ROLE_VECINAL"        , "VE");

    /** Nombre “oficial” que Keycloak podría enviar en <i>realm_access.roles</i> */
    private final String keycloakName;
    /** Clave del mapa <i>subgrups</i> dentro del JWT */
    private final String subgrpsKey;
}


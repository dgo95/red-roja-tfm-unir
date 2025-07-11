CREATE TABLE `tipo_contrato` (
                                 `id` int NOT NULL AUTO_INCREMENT,
                                 `nombre` varchar(100) NOT NULL,
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `comunidades_autonomas` (
                                         `id` int NOT NULL AUTO_INCREMENT,
                                         `Nombre` varchar(100) NOT NULL,
                                         PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `acta` (
                        `id` int NOT NULL AUTO_INCREMENT,
                        `fecha_creacion` datetime(6) DEFAULT NULL,
                        `fecha_modificacion` datetime(6) DEFAULT NULL,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `provincias` (
                              `id` int NOT NULL AUTO_INCREMENT,
                              `cod_postal` int NOT NULL,
                              `Nombre` varchar(30) NOT NULL,
                              `comunidad_autonoma` int NOT NULL,
                              PRIMARY KEY (`id`),
                              KEY `comunidad_autonoma` (`comunidad_autonoma`),
                              CONSTRAINT `provincias_ibfk_1` FOREIGN KEY (`comunidad_autonoma`) REFERENCES `comunidades_autonomas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `municipios` (
                              `id` int NOT NULL AUTO_INCREMENT,
                              `provincia` int DEFAULT NULL,
                              `nombre` varchar(100) NOT NULL,
                              PRIMARY KEY (`id`),
                              KEY `provincia` (`provincia`),
                              CONSTRAINT `municipios_ibfk_1` FOREIGN KEY (`provincia`) REFERENCES `provincias` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8117 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `direccion` (
                             `id` int NOT NULL AUTO_INCREMENT,
                             `codigo_postal` varchar(10) DEFAULT NULL,
                             `municipio_id` int DEFAULT NULL,
                             `direccion` varchar(255) NOT NULL,
                             PRIMARY KEY (`id`),
                             KEY `municipio_id` (`municipio_id`),
                             CONSTRAINT `direccion_ibfk_1` FOREIGN KEY (`municipio_id`) REFERENCES `municipios` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=96 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `actividad_economica` (
                                       `id` int NOT NULL AUTO_INCREMENT,
                                       `nombre` varchar(150) NOT NULL,
                                       `grupo` varchar(150) NOT NULL,
                                       PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `inventario` (
                              `id` int NOT NULL AUTO_INCREMENT,
                              `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                              `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `comite` (
                          `id` int NOT NULL AUTO_INCREMENT,
                          `organizacion` enum('PCTE','CJC') DEFAULT NULL,
                          `nombre` varchar(255) NOT NULL,
                          `email` varchar(255) NOT NULL,
                          `depende_de` int NOT NULL,
                          `nivel` varchar(255) DEFAULT NULL,
                          `inventario_id` int DEFAULT NULL,
                          `sede` varchar(255) DEFAULT NULL,
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `email` (`email`),
                          KEY `depende_de` (`depende_de`),
                          KEY `inventario_id` (`inventario_id`),
                          CONSTRAINT `comite_ibfk_1` FOREIGN KEY (`depende_de`) REFERENCES `comite` (`id`),
                          CONSTRAINT `comite_ibfk_2` FOREIGN KEY (`inventario_id`) REFERENCES `inventario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `comite_base` (
                               `id` int NOT NULL AUTO_INCREMENT,
                               `organizacion` enum('PCTE','CJC','Mixta') DEFAULT NULL,
                               `nombre` varchar(255) NOT NULL,
                               `email` varchar(255) NOT NULL,
                               `depende_de` int NOT NULL,
                               `sede` varchar(255) DEFAULT NULL,
                               `nivel` varchar(255) DEFAULT NULL,
                               `inventario_id` int DEFAULT NULL,
                               PRIMARY KEY (`id`),
                               UNIQUE KEY `email` (`email`),
                               KEY `depende_de` (`depende_de`),
                               KEY `inventario_id` (`inventario_id`),
                               CONSTRAINT `comite_base_ibfk_1` FOREIGN KEY (`depende_de`) REFERENCES `comite` (`id`),
                               CONSTRAINT `comite_base_ibfk_2` FOREIGN KEY (`inventario_id`) REFERENCES `inventario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `reunion` (
                           `id` INT NOT NULL AUTO_INCREMENT,
                           `apta_premilitantes` BIT(1) NOT NULL,
                           `direccion` VARCHAR(255) DEFAULT NULL,
                           `duracion` DECIMAL(21,0) DEFAULT NULL,
                           `fecha_fin` DATETIME(6) DEFAULT NULL,
                           `fecha_inicio` DATETIME(6) DEFAULT NULL,
                           `terminada` BIT(1) NOT NULL,
                           `acta_id` INT DEFAULT NULL,
                           `comite_base_id` INT DEFAULT NULL,
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `UK_st9uxomucgkdynrxs8rg3y9yj` (`acta_id`),
                           CONSTRAINT `FK7eilsfbkuedqxggtkf17bb483` FOREIGN KEY (`acta_id`) REFERENCES `acta` (`id`),
                           CONSTRAINT `FK_reunion_comite_base` FOREIGN KEY (`comite_base_id`) REFERENCES `comite_base` (`id`)
)ENGINE=InnoDB
AUTO_INCREMENT=2
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `militante` (
                             `id` int NOT NULL AUTO_INCREMENT,
                             `apellido` varchar(25) NOT NULL,
                             `apellido2` varchar(25) DEFAULT NULL,
                             `direccion_id` int DEFAULT NULL,
                             `email` varchar(255) NOT NULL,
                             `estudiante` tinyint(1) DEFAULT '0',
                             `fecha_nacimiento` date DEFAULT NULL,
                             `nombre` varchar(25) NOT NULL,
                             `numero_carnet` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                             `sexo` enum('MASCULINO','FEMENINO') NOT NULL,
                             `telefono` varchar(100) NOT NULL,
                             `trabajador` tinyint(1) DEFAULT '0',
                             `sindicado` bit(1) DEFAULT NULL,
                             `premilitante` bit(1) DEFAULT NULL,
                             `militante_id` varchar(255) NOT NULL,
                             `organizacion` enum('PCTE','CJC','Mixta') DEFAULT NULL,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `email` (`email`),
                             UNIQUE KEY `telefono` (`telefono`),
                             UNIQUE KEY `numero_carnet` (`numero_carnet`),
                             KEY `direccion_id` (`direccion_id`),
                             CONSTRAINT `militante_ibfk_1` FOREIGN KEY (`direccion_id`) REFERENCES `direccion` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=91 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `contactos` (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `email` varchar(255) DEFAULT NULL,
                             `estado_actual` varchar(255) DEFAULT NULL,
                             `estudiante` bit(1) DEFAULT NULL,
                             `fecha_nacimiento` date DEFAULT NULL,
                             `nombre` varchar(255) DEFAULT NULL,
                             `proxima_tarea` varchar(255) DEFAULT NULL,
                             `situacion_origen` varchar(255) DEFAULT NULL,
                             `telefono` varchar(255) DEFAULT NULL,
                             `trabajador` bit(1) DEFAULT NULL,
                             `militante_id` int NOT NULL,
                             `municipio_id` int NOT NULL,
                             PRIMARY KEY (`id`),
                             KEY `FK2qqv6p8f3r2k2th7jsj3c9wgv` (`militante_id`),
                             KEY `FK4rbrol18boai8hvuo2jsteqlm` (`municipio_id`),
                             CONSTRAINT `FK2qqv6p8f3r2k2th7jsj3c9wgv` FOREIGN KEY (`militante_id`) REFERENCES `militante` (`id`),
                             CONSTRAINT `FK4rbrol18boai8hvuo2jsteqlm` FOREIGN KEY (`municipio_id`) REFERENCES `municipios` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `empresa` (
                           `id` int NOT NULL AUTO_INCREMENT,
                           `nombre` varchar(100) NOT NULL,
                           `num_trabajadores` int DEFAULT NULL,
                           `direccion_id` int DEFAULT NULL,
                           PRIMARY KEY (`id`),
                           KEY `direccion_id` (`direccion_id`),
                           CONSTRAINT `empresa_ibfk_1` FOREIGN KEY (`direccion_id`) REFERENCES `direccion` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `centro_trabajo` (
                                  `id` int NOT NULL AUTO_INCREMENT,
                                  `nombre` varchar(100) NOT NULL,
                                  `num_trabajadores` int DEFAULT NULL,
                                  `direccion_id` int DEFAULT NULL,
                                  `empresa_id` int DEFAULT NULL,
                                  PRIMARY KEY (`id`),
                                  KEY `direccion_id` (`direccion_id`),
                                  KEY `empresa_id` (`empresa_id`),
                                  CONSTRAINT `centro_trabajo_ibfk_1` FOREIGN KEY (`direccion_id`) REFERENCES `direccion` (`id`) ON DELETE SET NULL,
                                  CONSTRAINT `centro_trabajo_ibfk_2` FOREIGN KEY (`empresa_id`) REFERENCES `empresa` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `email_enviado` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `body` varchar(255) NOT NULL,
                                 `comentarios` varchar(255) DEFAULT NULL,
                                 `estado` enum('ENVIADO','FALLIDO','PENDIENTE') NOT NULL,
                                 `fecha_envio` datetime(6) DEFAULT NULL,
                                 `from_email` varchar(255) NOT NULL,
                                 `mensaje_error` varchar(255) DEFAULT NULL,
                                 `numero_reintentos` int NOT NULL,
                                 `subject` varchar(255) NOT NULL,
                                 `to_email` varchar(255) NOT NULL,
                                 `ultima_modificacion` datetime(6) DEFAULT NULL,
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `estudios` (
                            `id` int NOT NULL AUTO_INCREMENT,
                            `nombre_estudio` varchar(100) NOT NULL,
                            `anho_finalizacion` int DEFAULT NULL,
                            `sindicato_estudiantil` tinyint(1) DEFAULT NULL,
                            `centro_estudios` varchar(200) DEFAULT NULL,
                            `militante_id` int NOT NULL,
                            `nivel_educativo` int DEFAULT NULL,
                            `sub_subdivision_nivel_educativo` int DEFAULT NULL,
                            `subdivision_nivel_educativo` int DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            KEY `militante_id` (`militante_id`),
                            CONSTRAINT `estudios_ibfk_1` FOREIGN KEY (`militante_id`) REFERENCES `militante` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `sindicato` (
                             `id` int NOT NULL,
                             `nombre` varchar(100) NOT NULL,
                             `telefono` varchar(20) DEFAULT NULL,
                             `email` varchar(100) DEFAULT NULL,
                             `web` varchar(200) DEFAULT NULL,
                             `numero_afiliados` int DEFAULT NULL,
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `federacion` (
                              `id` int NOT NULL AUTO_INCREMENT,
                              `sindicato_id` int DEFAULT NULL,
                              `nombre` varchar(100) NOT NULL,
                              PRIMARY KEY (`id`),
                              KEY `sindicato_id` (`sindicato_id`),
                              CONSTRAINT `federacion_ibfk_1` FOREIGN KEY (`sindicato_id`) REFERENCES `sindicato` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `ficha_movilidad` (
                                   `id` int NOT NULL AUTO_INCREMENT,
                                   `fecha_fin` date DEFAULT NULL,
                                   `fecha_inicio` date NOT NULL,
                                   `objeto_traslado` varchar(255) NOT NULL,
                                   `militante_id` int NOT NULL,
                                   `municipio_id` int DEFAULT NULL,
                                   `frentes_trabajo` varchar(255) DEFAULT NULL,
                                   `habitos_mejorar` varchar(255) DEFAULT NULL,
                                   `otras_observaciones` varchar(255) DEFAULT NULL,
                                   `otras_responsabilidades` varchar(255) DEFAULT NULL,
                                   `puntos_positivos` varchar(255) DEFAULT NULL,
                                   `responsabilidad_destacada` varchar(255) DEFAULT NULL,
                                   `sindicato_estudiantil` varchar(255) DEFAULT NULL,
                                   `comite_responsable_id` int DEFAULT NULL,
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `UK_nacjqpu0bbv8c0k06qooefc5n` (`militante_id`),
                                   KEY `FK63ax2vekl2866d9akmskxmoxm` (`municipio_id`),
                                   KEY `FKjfy1rgihusgme8f31b3f0htcy` (`comite_responsable_id`),
                                   CONSTRAINT `FK63ax2vekl2866d9akmskxmoxm` FOREIGN KEY (`municipio_id`) REFERENCES `municipios` (`id`),
                                   CONSTRAINT `FK9yx9rq0tws6xd6i6853yn5lhe` FOREIGN KEY (`militante_id`) REFERENCES `militante` (`id`),
                                   CONSTRAINT `FKjfy1rgihusgme8f31b3f0htcy` FOREIGN KEY (`comite_responsable_id`) REFERENCES `comite` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `habilidad` (
                             `id` int NOT NULL AUTO_INCREMENT,
                             `nombre` varchar(255) NOT NULL,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `UK_t94vhy06adfcmhvjdgr1jtdd0` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `idiomas` (
                           `id` int NOT NULL AUTO_INCREMENT,
                           `alpha2` varchar(2) NOT NULL,
                           `langEN` varchar(48) NOT NULL,
                           `langDE` varchar(48) NOT NULL,
                           `langFR` varchar(48) NOT NULL,
                           `langES` varchar(48) NOT NULL,
                           `langIT` varchar(48) NOT NULL,
                           `langGL` varchar(48) NOT NULL,
                           `langCA` varchar(48) NOT NULL,
                           `langEU` varchar(48) NOT NULL,
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `alpha2` (`alpha2`),
                           UNIQUE KEY `alpha2_2` (`alpha2`)
) ENGINE=InnoDB AUTO_INCREMENT=184 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Idiomas (ISO 639)';
CREATE TABLE `invitado` (
                            `id` int NOT NULL AUTO_INCREMENT,
                            `email` varchar(255) DEFAULT NULL,
                            `es_militante` bit(1) NOT NULL,
                            `nombre` varchar(255) DEFAULT NULL,
                            `numero_carnet` varchar(255) DEFAULT NULL,
                            `reunion_id` int DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            KEY `FKciepi28yxhfw1b120dwxfqf0f` (`reunion_id`),
                            CONSTRAINT `FKciepi28yxhfw1b120dwxfqf0f` FOREIGN KEY (`reunion_id`) REFERENCES `reunion` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `material_inventario` (
                                       `id` int NOT NULL AUTO_INCREMENT,
                                       `inventario_id` int NOT NULL,
                                       `nombre` varchar(255) NOT NULL,
                                       `description` text,
                                       `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                       `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                       PRIMARY KEY (`id`),
                                       KEY `FKq5qu9y8jxj7mian2bv0velu13` (`inventario_id`),
                                       CONSTRAINT `FKq5qu9y8jxj7mian2bv0velu13` FOREIGN KEY (`inventario_id`) REFERENCES `inventario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `asignacion_material` (
                                       `id` int NOT NULL AUTO_INCREMENT,
                                       `material_inventario_id` int NOT NULL,
                                       `militante_id` int DEFAULT NULL,
                                       `comite_base_id` int DEFAULT NULL,
                                       `comite_id` int DEFAULT NULL,
                                       `cantidad` int NOT NULL,
                                       PRIMARY KEY (`id`),
                                       KEY `militante_id` (`militante_id`),
                                       KEY `comite_base_id` (`comite_base_id`),
                                       KEY `comite_id` (`comite_id`),
                                       KEY `asignacion_material_ibfk_1` (`material_inventario_id`),
                                       CONSTRAINT `asignacion_material_ibfk_1` FOREIGN KEY (`material_inventario_id`) REFERENCES `material_inventario` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                       CONSTRAINT `asignacion_material_ibfk_2` FOREIGN KEY (`militante_id`) REFERENCES `militante` (`id`),
                                       CONSTRAINT `asignacion_material_ibfk_3` FOREIGN KEY (`comite_base_id`) REFERENCES `comite_base` (`id`),
                                       CONSTRAINT `asignacion_material_ibfk_4` FOREIGN KEY (`comite_id`) REFERENCES `comite` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `inventario_historial` (
                                        `id` int NOT NULL AUTO_INCREMENT,
                                        `material_inventario_id` int DEFAULT NULL,
                                        `change_type` enum('ADDED','REMOVED','USED','LOST','SOLD') NOT NULL,
                                        `cantidad` int NOT NULL,
                                        `change_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                        `description` text,
                                        PRIMARY KEY (`id`),
                                        KEY `FK3wqmd550evhhofyjy7enp3794` (`material_inventario_id`),
                                        CONSTRAINT `FK3wqmd550evhhofyjy7enp3794` FOREIGN KEY (`material_inventario_id`) REFERENCES `material_inventario` (`id`) ON DELETE SET NULL ON UPDATE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `militante_comite` (
                                    `id` int NOT NULL AUTO_INCREMENT,
                                    `militante_id` int DEFAULT NULL,
                                    `comite_id` int DEFAULT NULL,
                                    PRIMARY KEY (`id`),
                                    KEY `militante_id` (`militante_id`),
                                    KEY `comite_id` (`comite_id`),
                                    CONSTRAINT `militante_comite_ibfk_1` FOREIGN KEY (`militante_id`) REFERENCES `militante` (`id`),
                                    CONSTRAINT `militante_comite_ibfk_2` FOREIGN KEY (`comite_id`) REFERENCES `comite` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `militante_comite_base` (
                                         `militante_id` int NOT NULL,
                                         `comite_base_id` int NOT NULL,
                                         PRIMARY KEY (`militante_id`,`comite_base_id`),
                                         KEY `FKhvuoy8q48litsqrdp6c75tjbc` (`comite_base_id`),
                                         CONSTRAINT `FK74ry1gqlrqeolk7vxdf3swlnd` FOREIGN KEY (`militante_id`) REFERENCES `militante` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                         CONSTRAINT `FKhvuoy8q48litsqrdp6c75tjbc` FOREIGN KEY (`comite_base_id`) REFERENCES `comite_base` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `militante_habilidad` (
                                       `id` bigint NOT NULL AUTO_INCREMENT,
                                       `descripcion` varchar(255) NOT NULL,
                                       `habilidad_id` int NOT NULL,
                                       `militante_id` int NOT NULL,
                                       PRIMARY KEY (`id`),
                                       KEY `FKwmfuue7bfcicgo6miucgly6u` (`militante_id`),
                                       KEY `FK6y3nuh0aj0ndqvljncoefarem` (`habilidad_id`),
                                       CONSTRAINT `FK6y3nuh0aj0ndqvljncoefarem` FOREIGN KEY (`habilidad_id`) REFERENCES `habilidad` (`id`),
                                       CONSTRAINT `FKwmfuue7bfcicgo6miucgly6u` FOREIGN KEY (`militante_id`) REFERENCES `militante` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `militante_idioma` (
                                    `id` bigint NOT NULL AUTO_INCREMENT,
                                    `nivel` enum('NATIVO','A1','A2','B1','B2','C1','C2') NOT NULL,
                                    `idioma_id` int NOT NULL,
                                    `militante_id` int NOT NULL,
                                    PRIMARY KEY (`id`),
                                    KEY `FKbjtoj0bnnxc3xknj5j1eue3so` (`idioma_id`),
                                    KEY `FK6iy5xdyupox7outw4sy90fhvt` (`militante_id`),
                                    CONSTRAINT `FK6iy5xdyupox7outw4sy90fhvt` FOREIGN KEY (`militante_id`) REFERENCES `militante` (`id`),
                                    CONSTRAINT `FKbjtoj0bnnxc3xknj5j1eue3so` FOREIGN KEY (`idioma_id`) REFERENCES `idiomas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `militante_rol_comite` (
                                        `id` int NOT NULL AUTO_INCREMENT,
                                        `rol` enum('ADMIN', 'AGITPROP', 'DIR_POLITICA', 'ESTUDIANTIL', 'FINANZAS', 'FORMACION', 'GARANTIAS_CTRL', 'MASAS', 'MIEMBRO', 'MUJER', 'OBRERO', 'ORGANIZACION', 'VECINAL') NOT NULL,
                                        `comite_id` int DEFAULT NULL,
                                        `comite_base_id` int DEFAULT NULL,
                                        `militante_id` int NOT NULL,
                                        PRIMARY KEY (`id`),
                                        KEY `FKhgd6kl9hc7slv7pxhsxyeiqav` (`comite_id`),
                                        KEY `FK1xwkr961e9n86a1jwgley4kng` (`comite_base_id`),
                                        KEY `FKblg3kpxfayx6hjuiar01b1o11` (`militante_id`),
                                        CONSTRAINT `FK1xwkr961e9n86a1jwgley4kng` FOREIGN KEY (`comite_base_id`) REFERENCES `comite_base` (`id`),
                                        CONSTRAINT `FKblg3kpxfayx6hjuiar01b1o11` FOREIGN KEY (`militante_id`) REFERENCES `militante` (`id`),
                                        CONSTRAINT `FKhgd6kl9hc7slv7pxhsxyeiqav` FOREIGN KEY (`comite_id`) REFERENCES `comite` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=115 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `modalidad_trabajo` (
                                     `id` int NOT NULL AUTO_INCREMENT,
                                     `nombre` varchar(150) NOT NULL,
                                     PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `nivel_educativo` (
                                   `id` int NOT NULL AUTO_INCREMENT,
                                   `nombre` varchar(255) NOT NULL,
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `otros_datos` (
                               `id` int NOT NULL AUTO_INCREMENT,
                               `artes_marciales` varchar(255) DEFAULT NULL,
                               `conocimiento_wordpress` varchar(255) DEFAULT NULL,
                               `conocimientos_diseno_grafico` varchar(255) DEFAULT NULL,
                               `conocimientos_musicales` varchar(255) DEFAULT NULL,
                               `conocimientos_seo` varchar(255) DEFAULT NULL,
                               `desarrollo_web` varchar(255) DEFAULT NULL,
                               `equipo_fotografia_video` varchar(255) DEFAULT NULL,
                               `estudios_musicales` varchar(255) DEFAULT NULL,
                               `experiencias_tocando_publico` varchar(255) DEFAULT NULL,
                               `habilidad_instrumento_canto` varchar(255) DEFAULT NULL,
                               `herramientas_manejo_rss_community_manager` varchar(255) DEFAULT NULL,
                               `otros_conocimientos_tecnicos` varchar(255) DEFAULT NULL,
                               `programas_edicion_video` varchar(255) DEFAULT NULL,
                               `programas_maquetacion` varchar(255) DEFAULT NULL,
                               `militante_id` int NOT NULL,
                               PRIMARY KEY (`id`),
                               UNIQUE KEY `UK_rjtpo8bv7io4wvta4fkno36bd` (`militante_id`),
                               CONSTRAINT `FKr9t2eh2h2lksiug2m3skplx6g` FOREIGN KEY (`militante_id`) REFERENCES `militante` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `punto` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `descripcion` varchar(255) DEFAULT NULL,
                         `orden` int DEFAULT NULL,
                         `titulo` varchar(255) DEFAULT NULL,
                         `reunion_id` int DEFAULT NULL,
                         PRIMARY KEY (`id`),
                         KEY `FK_punto_reunion` (`reunion_id`),
                         CONSTRAINT `FK_punto_reunion` FOREIGN KEY (`reunion_id`) REFERENCES `reunion` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `roles` (
                         `id` int NOT NULL AUTO_INCREMENT,
                         `nombre` varchar(255) DEFAULT NULL,
                         `clave` varchar(255) DEFAULT NULL,
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `sindicacion` (
                               `id` int NOT NULL AUTO_INCREMENT,
                               `cargo` varchar(255) DEFAULT NULL,
                               `fecha_sindicacion` date DEFAULT NULL,
                               `federacion_otros` varchar(255) DEFAULT NULL,
                               `participa_area_juventud` bit(1) DEFAULT NULL,
                               `sindicato_otros` varchar(255) DEFAULT NULL,
                               `federacion_id` int DEFAULT NULL,
                               `militante_id` int NOT NULL,
                               `sindicato_id` int DEFAULT NULL,
                               PRIMARY KEY (`id`),
                               KEY `FK5kka93e37h96qjjegqrgdpobu` (`federacion_id`),
                               KEY `FKbl96w256odwnjqn8np2iqjghx` (`militante_id`),
                               KEY `FKhy66tgiwvgryfdjak38cka6hr` (`sindicato_id`),
                               CONSTRAINT `FK5kka93e37h96qjjegqrgdpobu` FOREIGN KEY (`federacion_id`) REFERENCES `federacion` (`id`),
                               CONSTRAINT `FKbl96w256odwnjqn8np2iqjghx` FOREIGN KEY (`militante_id`) REFERENCES `militante` (`id`),
                               CONSTRAINT `FKhy66tgiwvgryfdjak38cka6hr` FOREIGN KEY (`sindicato_id`) REFERENCES `sindicato` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `subdivision_nivel_educativo` (
                                               `id` int NOT NULL AUTO_INCREMENT,
                                               `nombre` varchar(255) NOT NULL,
                                               `id_nivel_educativo` int DEFAULT NULL,
                                               PRIMARY KEY (`id`),
                                               KEY `id_nivel_educativo` (`id_nivel_educativo`),
                                               CONSTRAINT `subdivision_nivel_educativo_ibfk_1` FOREIGN KEY (`id_nivel_educativo`) REFERENCES `nivel_educativo` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `subpunto` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `detalle` varchar(255) DEFAULT NULL,
                            `orden` int DEFAULT NULL,
                            `titulo` varchar(255) DEFAULT NULL,
                            `punto_id` bigint DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            KEY `FK4aisaiehmqu06pc4c5f7t1wcj` (`punto_id`),
                            CONSTRAINT `FK4aisaiehmqu06pc4c5f7t1wcj` FOREIGN KEY (`punto_id`) REFERENCES `punto` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `subsubdivision_nivel_educativo` (
                                                  `id` int NOT NULL AUTO_INCREMENT,
                                                  `nombre` varchar(255) NOT NULL,
                                                  `id_subdivision_nivel_educativo` int DEFAULT NULL,
                                                  PRIMARY KEY (`id`),
                                                  KEY `id_subdivision_nivel_educativo` (`id_subdivision_nivel_educativo`),
                                                  CONSTRAINT `subsubdivision_nivel_educativo_ibfk_1` FOREIGN KEY (`id_subdivision_nivel_educativo`) REFERENCES `subdivision_nivel_educativo` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `contrato` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `centro_trabajo_id` int DEFAULT NULL,
                            `tipo_contrato_id` int DEFAULT NULL,
                            `modalidad_trabajo_id` int DEFAULT NULL,
                            `actividad_economica_id` int DEFAULT NULL,
                            `existe_organo_representacion_trabajadores` enum('SI','NO','DESCONOCIDO') DEFAULT NULL,
                            `participa_organo_representacion` tinyint(1) NOT NULL DEFAULT '0',
                            `fecha_inicio` date NOT NULL,
                            `militante_id` int NOT NULL,
                            `empresa` varchar(255) DEFAULT NULL,
                            `nombre_centro_trabajo` varchar(255) DEFAULT NULL,
                            `numero_trabajadores_centro_trabajo` int DEFAULT NULL,
                            `numero_trabajadores_empresa` int DEFAULT NULL,
                            `direccion_trabajo` varchar(510) DEFAULT NULL,
                            `convenio` varchar(255) DEFAULT NULL,
                            `profesion` varchar(255) DEFAULT NULL,
                            `sector_laboral` varchar(255) DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            KEY `tipo_contrato_id` (`tipo_contrato_id`),
                            KEY `actividad_economica_id` (`actividad_economica_id`),
                            KEY `centro_trabajo_id` (`centro_trabajo_id`),
                            KEY `modalidad_trabajo_id` (`modalidad_trabajo_id`),
                            KEY `militante_id` (`militante_id`),
                            KEY `FK6yvewodkj528hpliukgwf4pj3` (`direccion_trabajo`),
                            CONSTRAINT `contrato_ibfk_1` FOREIGN KEY (`tipo_contrato_id`) REFERENCES `tipo_contrato` (`id`) ON DELETE SET NULL,
                            CONSTRAINT `contrato_ibfk_2` FOREIGN KEY (`actividad_economica_id`) REFERENCES `actividad_economica` (`id`) ON DELETE SET NULL,
                            CONSTRAINT `contrato_ibfk_3` FOREIGN KEY (`centro_trabajo_id`) REFERENCES `centro_trabajo` (`id`) ON DELETE SET NULL,
                            CONSTRAINT `contrato_ibfk_4` FOREIGN KEY (`modalidad_trabajo_id`) REFERENCES `modalidad_trabajo` (`id`) ON DELETE SET NULL,
                            CONSTRAINT `contrato_ibfk_5` FOREIGN KEY (`militante_id`) REFERENCES `militante` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
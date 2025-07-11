CREATE OR REPLACE VIEW `censo_estudiantil` AS
SELECT
    m.id AS id,
    m.militante_id AS militante_id,
    m.numero_carnet AS numero_carnet,
    m.nombre AS nombre,
    m.apellido AS apellido,
    m.apellido2 AS apellido2,
    ne.nombre AS nivel_educativo,
    sne.nombre AS subdivision_nivel_educativo,
    sne2.nombre AS subsubdivision_nivel_educativo,
    e.nombre_estudio AS nombre_estudio,
    e.centro_estudios AS centro_estudios,
    e.anho_finalizacion AS anho_finalizacion,
    m.premilitante AS premilitante,
    CASE
        WHEN e.sindicato_estudiantil = 1 THEN 'Sindicado'
        WHEN e.sindicato_estudiantil = 0 THEN 'No sindicado'
        ELSE 'No especificado'
        END AS sindicato_estudiantil
FROM
    militante m
        JOIN estudios e ON m.id = e.militante_id
        JOIN nivel_educativo ne ON ne.id = e.nivel_educativo
        JOIN subdivision_nivel_educativo sne ON sne.id = e.subdivision_nivel_educativo
        LEFT JOIN subsubdivision_nivel_educativo sne2 ON sne2.id = e.sub_subdivision_nivel_educativo;
CREATE OR REPLACE VIEW `censo_general` AS
SELECT
    m.id AS id,
    m.militante_id AS militante_id,
    m.numero_carnet AS numero_carnet,
    m.nombre AS nombre,
    m.apellido AS apellido,
    m.apellido2 AS apellido2,
    m.sexo AS sexo,
    m.fecha_nacimiento AS fecha_nacimiento,
    m.premilitante AS premilitante,
    CASE
        WHEN m.estudiante = 1 AND e.subdivision_nivel_educativo = 4 THEN 'UNIV'
        WHEN m.estudiante = 1 THEN 'INST'
        ELSE ''
        END AS estudiante,
    COALESCE(e.centro_estudios, '') AS centro_estudios,
    CASE
        WHEN e.sindicato_estudiantil = 1 THEN 'Sí'
        ELSE 'No'
        END AS sindicato_estudiantil,
    CASE
        WHEN m.trabajador = 1 THEN 'TRABAJADOR'
        ELSE 'PARADO'
        END AS trabajador,
    COALESCE(ae.nombre, '') AS sector_laboral,
    COALESCE(sin.nombre, '') AS sindicato_laboral,
    m.email AS correo_electronico,
    m.telefono AS telefono,
    COALESCE(idiomas_concat.idiomas, '') AS idiomas
FROM
    militante m
        LEFT JOIN estudios e ON e.militante_id = m.id
        LEFT JOIN contrato c ON c.militante_id = m.id
        LEFT JOIN actividad_economica ae ON c.actividad_economica_id = ae.id
        LEFT JOIN sindicacion s ON s.militante_id = m.id
        LEFT JOIN sindicato sin ON sin.id = s.sindicato_id
        LEFT JOIN (
        SELECT
            mi.militante_id,
            GROUP_CONCAT(CONCAT(i.langES, ' (', mi.nivel, ')') ORDER BY i.langES ASC SEPARATOR ', ') AS idiomas
        FROM
            militante_idioma mi
                LEFT JOIN idiomas i ON mi.idioma_id = i.id
        GROUP BY mi.militante_id
    ) idiomas_concat ON idiomas_concat.militante_id = m.id;
CREATE OR REPLACE VIEW `censo_laboral_sindical` AS
SELECT
    m.id AS id,
    m.militante_id AS militante_id,
    cb.nombre AS colectivo,
    m.numero_carnet AS numero_carnet,
    m.nombre AS nombre,
    m.apellido AS apellido,
    m.apellido2 AS apellido2,
    ae.nombre AS actividad_economica,
    c.profesion AS profesion,
    c.empresa AS nombre_empresa,
    c.numero_trabajadores_empresa AS numero_trabajadores_empresa,
    c.nombre_centro_trabajo AS nombre_centro_trabajo,
    c.numero_trabajadores_centro_trabajo AS numero_trabajadores_centro_trabajo,
    c.existe_organo_representacion_trabajadores AS existe_organo_representacion_trabajadores,
    CASE
        WHEN c.participa_organo_representacion = 1 THEN 'Sí'
        WHEN c.participa_organo_representacion = 0 THEN 'No'
        ELSE 'No especificado'
        END AS participa_organo_representacion,
    tc.nombre AS tipo_contrato,
    TIMESTAMPDIFF(MONTH, c.fecha_inicio, CURDATE()) AS antiguedad,
    COALESCE(s2.nombre, s3.nombre, s.sindicato_otros) AS sindicato,
    COALESCE(f.nombre, s.federacion_otros) AS federacion,
    s.cargo AS cargo,
    m.premilitante AS premilitante,
    CASE
        WHEN s.participa_area_juventud = 1 THEN 'Sí'
        WHEN s.participa_area_juventud = 0 THEN 'No'
        ELSE 'No especificado'
        END AS participa_area_juventud
FROM
    militante m
        JOIN militante_comite_base mcb ON mcb.militante_id = m.id
        JOIN comite_base cb ON cb.id = mcb.comite_base_id
        LEFT JOIN contrato c ON c.militante_id = m.id
        LEFT JOIN actividad_economica ae ON ae.id = c.actividad_economica_id
        LEFT JOIN tipo_contrato tc ON tc.id = c.tipo_contrato_id
        LEFT JOIN sindicacion s ON s.militante_id = m.id
        LEFT JOIN federacion f ON f.id = s.federacion_id
        LEFT JOIN sindicato s2 ON s2.id = f.sindicato_id
        LEFT JOIN sindicato s3 ON s3.id = s.sindicato_id;
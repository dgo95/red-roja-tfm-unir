CREATE TABLE documentos (
    id              BIGSERIAL PRIMARY KEY,
    uuid            VARCHAR(36)  NOT NULL UNIQUE,
    nombre_original VARCHAR(255) NOT NULL,
    nombre_fisico   VARCHAR(255) NOT NULL UNIQUE,
    extension       VARCHAR(20),
    mime_type       VARCHAR(120),
    tamano_bytes    BIGINT,
    checksum_sha256 VARCHAR(64),
    iv_b64          VARCHAR(64),
    dek_wrapped_b64 VARCHAR(64),
    nivel           VARCHAR(20),
    confidencialidad VARCHAR(20),
    tipo            VARCHAR(30),
    propietario     VARCHAR(50),
    fecha_subida    TIMESTAMP WITH TIME ZONE,
    ruta_relativa   VARCHAR(255),
    creado_por      VARCHAR(100),
    creado_en       TIMESTAMP WITH TIME ZONE,
    modificado_por  VARCHAR(100),
    modificado_en   TIMESTAMP WITH TIME ZONE,
    version         INTEGER
);

CREATE TABLE documento_categorias (
    documento_id BIGINT NOT NULL,
    categoria    VARCHAR(30) NOT NULL,
    PRIMARY KEY (documento_id, categoria),
    CONSTRAINT fk_doc_cat_documento
        FOREIGN KEY (documento_id)
        REFERENCES documentos(id)
        ON DELETE CASCADE
);
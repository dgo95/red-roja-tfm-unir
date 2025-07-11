-- V1__create_table_email_enviado.sql

CREATE TABLE IF NOT EXISTS email_enviado (
                                             id BIGSERIAL PRIMARY KEY,
                                             from_email VARCHAR(255) NOT NULL,
                                             to_email VARCHAR(255) NOT NULL,
                                             subject TEXT NOT NULL,
                                             body TEXT NOT NULL,
                                             estado VARCHAR(50) NOT NULL,
                                             numero_reintentos INT NOT NULL,
                                             comentarios TEXT,
                                             mensaje_error TEXT,
                                             fecha_envio TIMESTAMP,
                                             ultima_modificacion TIMESTAMP
);
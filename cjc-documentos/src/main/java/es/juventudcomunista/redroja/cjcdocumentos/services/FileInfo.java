package es.juventudcomunista.redroja.cjcdocumentos.services;

/**
 * DTO inmutable devuelto por la operación de guardado.
 */
public record FileInfo(String sha256, long size) {}


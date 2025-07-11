package es.juventudcomunista.redroja.cjcdocumentos.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public class ByteArrayMultipartFile implements MultipartFile {

	private final byte[] imgContent;
    private final String contentType;
    private final String originalFilename;

    public ByteArrayMultipartFile(byte[] imgContent, String contentType, String originalFilename) {
        this.imgContent = imgContent;
        this.contentType = contentType;
        this.originalFilename = originalFilename;
    }

    @Override
    public String getName() {
        // Retorna el nombre del parámetro (form field) en el formulario.
        return originalFilename;
    }

    @Override
    public String getOriginalFilename() {
        // Retorna el nombre del archivo original en el cliente.
        return originalFilename;
    }

    @Override
    public String getContentType() {
        // Retorna el tipo de contenido.
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        // Retorna si el archivo está vacío o no.
        return imgContent == null || imgContent.length == 0;
    }

    @Override
    public long getSize() {
        // Retorna el tamaño del archivo.
        return imgContent.length;
    }

    @Override
    public byte[] getBytes() {
        // Retorna el contenido del archivo como un array de bytes.
        return imgContent;
    }

    @Override
    public InputStream getInputStream() {
        // Retorna un InputStream.
        return new ByteArrayInputStream(imgContent);
    }

    @Override
    public void transferTo(File dest) {
        // Transfiere el contenido del archivo a un destino.
        throw new UnsupportedOperationException("Transfer to file not supported.");
    }

}

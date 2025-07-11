package es.juventudcomunista.redroja.cjcdocumentos.services;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import es.juventudcomunista.redroja.cjccommonutils.enums.Categoria;
import es.juventudcomunista.redroja.cjccommonutils.enums.Confidencialidad;
import es.juventudcomunista.redroja.cjccommonutils.enums.NivelDocumento;
import es.juventudcomunista.redroja.cjccommonutils.enums.TipoDocumento;
import es.juventudcomunista.redroja.cjcdocumentos.dto.ActualizarDocumentoDTO;
import es.juventudcomunista.redroja.cjcdocumentos.dto.DocumentoRecibidoDTO;
import es.juventudcomunista.redroja.cjcdocumentos.dto.FicheroDescarga;
import es.juventudcomunista.redroja.cjcdocumentos.dto.NuevoDocumentoDTO;
import es.juventudcomunista.redroja.cjcdocumentos.entity.Documento;
import es.juventudcomunista.redroja.cjcdocumentos.execption.DocumentoNoEncontradoException;
import es.juventudcomunista.redroja.cjcdocumentos.execption.FotoPerfilException;
import es.juventudcomunista.redroja.cjcdocumentos.mapper.DocumentoMapper;
import es.juventudcomunista.redroja.cjcdocumentos.repository.DocumentoRepository;
import es.juventudcomunista.redroja.cjcdocumentos.storage.CryptoFileInfo;
import es.juventudcomunista.redroja.cjcdocumentos.storage.DecryptedFile;
import es.juventudcomunista.redroja.cjcdocumentos.watermark.WatermarkInfo;
import es.juventudcomunista.redroja.cjcdocumentos.watermark.WatermarkProcessor;
import es.juventudcomunista.redroja.cjcdocumentos.watermark.WatermarkService;
import jakarta.servlet.ServletOutputStream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.xwpf.usermodel.*;
import es.juventudcomunista.redroja.cjccommonutils.service.RestClient;
import es.juventudcomunista.redroja.cjccommonutils.web.ApiResponse;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import es.juventudcomunista.redroja.cjcdocumentos.util.ByteArrayMultipartFile;

import lombok.extern.slf4j.Slf4j;

import static es.juventudcomunista.redroja.cjcdocumentos.repository.DocumentoSpecifications.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class AlmacenamientoService {

    @Value("${ruta.almacenamiento.imagenes}")
    private String rutaAlmacenamiento;

    private final DocumentoRepository documentoRepository;
    private final DocumentoMapper mapper;
    private final FileStorageService  storage;
    private final WatermarkService watermarkService;

    @Value("${custom.foto.perfil.chico}")
    private String defectoChico;
    
    @Value("${custom.foto.perfil.chica}")
    private String defectoChica;

    @Value("${app.fotos-perfil.dir}")
    private Path rootFotosPerfil;

    @Value("${app.storage.docs-dir}")
    private Path docsRoot;



    private final RestClient restClient;

    
    private static final long MAXIMO_PESO_IMAGEN = 200 * 1024; // 200 kB

    public String guardarImagen(Long id, MultipartFile imagen) throws IOException {
    	MultipartFile imagenJPEG = asegurarFormatoJPEG(imagen);
        MultipartFile imagenComprimida = comprimirSiEsNecesario(imagenJPEG);
        String nombreArchivo = id + ".jpg";
        Path destino = rootFotosPerfil.resolve(nombreArchivo);
        try (InputStream in = imagen.getInputStream()) {
            Files.copy(in, destino, StandardCopyOption.REPLACE_EXISTING);
        }

        return destino.toString();
    }

    private MultipartFile comprimirSiEsNecesario(MultipartFile imagenJPEG) throws IOException {
        // Leer la imagen original para verificar sus dimensiones
        BufferedImage imagenOriginal = ImageIO.read(imagenJPEG.getInputStream());
        int anchoOriginal = imagenOriginal.getWidth();
        int altoOriginal = imagenOriginal.getHeight();

        // Comprobamos que la imagen tiene las dimensiones deseadas
        if (anchoOriginal != 1024 || altoOriginal != 1024) {
            throw new IOException("Las dimensiones de la imagen no son las esperadas. Se esperaba una imagen de 1024x1024.");
        }

        // Verificar si la imagen necesita compresión
        long tamanoImagen = imagenJPEG.getSize();
        if (tamanoImagen <= MAXIMO_PESO_IMAGEN) {
            return imagenJPEG;
        }

        // Proceso de compresión
        ByteArrayOutputStream baosComprimido = new ByteArrayOutputStream();
        Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByFormatName("jpeg");

        if (!imageWriters.hasNext()) {
            throw new IOException("No hay escritores disponibles para el formato JPEG");
        }

        ImageWriter imageWriter = imageWriters.next();
        ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(baosComprimido);
        imageWriter.setOutput(imageOutputStream);

        ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

        // Configurar la calidad de la compresión
        float calidad = 1.0f; // Comenzar con la máxima calidad
        while (tamanoImagen > MAXIMO_PESO_IMAGEN && calidad > 0.1f) { // Se establece un límite inferior de calidad para evitar una compresión excesiva
            baosComprimido.reset(); // Limpiar el baosComprimido para la siguiente iteración
            imageWriteParam.setCompressionQuality(calidad);

            // Intentar escribir con la nueva configuración de calidad
            try {
                imageWriter.write(null, new IIOImage(imagenOriginal, null, null), imageWriteParam);
                tamanoImagen = baosComprimido.size();
                calidad -= 0.05f; // Reducir la calidad en un 5%
            } finally {
                // Siempre liberar recursos en un bloque finally para evitar fugas de memoria
                imageOutputStream.flush();
            }
        }

        imageOutputStream.close();
        imageWriter.dispose();

        // Verificar si se logró la compresión
        if (tamanoImagen > MAXIMO_PESO_IMAGEN) {
            throw new IOException("No fue posible comprimir la imagen al tamaño deseado sin perder la calidad mínima requerida");
        }

        // Retornar la nueva imagen comprimida como MultipartFile
        return new ByteArrayMultipartFile(baosComprimido.toByteArray(), "image/jpeg", imagenJPEG.getOriginalFilename());
    }

    private MultipartFile asegurarFormatoJPEG(MultipartFile imagen) throws IOException {
        String tipo = imagen.getContentType();
        if ("image/jpeg".equals(tipo) || "image/jpg".equals(tipo)) {
            return imagen;
        }

        // Leer la imagen original
        BufferedImage imagenOriginal = ImageIO.read(imagen.getInputStream());

        // Crear una imagen compatible, sin transparencia (BufferedImage.TYPE_INT_RGB)
        BufferedImage imagenSinTransparencia = new BufferedImage(
            imagenOriginal.getWidth(), imagenOriginal.getHeight(), BufferedImage.TYPE_INT_RGB);

        // Dibujar la imagen original en la nueva imagen (sin transparencia)
        Graphics2D g2d = imagenSinTransparencia.createGraphics();
        g2d.drawImage(imagenOriginal, 0, 0, Color.WHITE, null);
        g2d.dispose(); // Es importante liberar el recurso

        // Convertir la imagen a byte array en formato JPEG
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(imagenSinTransparencia, "jpg", baos);
        byte[] bytesImagenJPEG = baos.toByteArray();

        // Retornar el MultipartFile en formato JPEG
        // Asegúrate de que ByteArrayMultipartFile puede aceptar estos parámetros en su constructor.
        return new ByteArrayMultipartFile(bytesImagenJPEG, "image/jpeg", imagen.getOriginalFilename());
    }

    public Resource obtenerFotoPerfil(String id, Integer s) {
        log.debug("Iniciando obtención de foto de perfil para ID: {}, tipo: {}", id, s);

        try {
            Resource resource = cargarComoRecurso(id + ".jpg");
            log.debug("Recurso [{}] cargado. Validando si es legible y existe...", resource.getFilename());

            if (esRecursoValido(resource)) {
                log.info("La foto de perfil para ID: {} se ha encontrado correctamente.", id);
                return resource;
            }

            log.warn("No se encontró la foto de perfil para ID: {}, se proporcionará una por defecto.", id);
            return cargarFotoPorDefecto(s);

        } catch (MalformedURLException e) {
            String mensajeError = String.format("URI del archivo malformada al cargar la foto de perfil para ID: %s", id);
            log.error(mensajeError, e);
            throw new FotoPerfilException(mensajeError, e);
        }
    }

    private Resource cargarComoRecurso(String nombreArchivo) throws MalformedURLException {
        Path file = rootFotosPerfil.resolve(nombreArchivo).normalize();
        log.debug("Intentando cargar archivo desde la ruta: {}", file.toAbsolutePath());
        return new UrlResource(file.toUri());
    }

    private boolean esRecursoValido(Resource resource) {
        try {
            boolean valido = resource.exists() && resource.isReadable();
            if (valido) {
                log.debug("El recurso [{}] es válido y legible.", resource.getFilename());
            } else {
                log.debug("El recurso [{}] no es válido o no es legible.", resource.getFilename());
            }
            return valido;
        } catch (Exception e) {
            log.error("Error al verificar el recurso [{}].", resource.getFilename(), e);
            return false;
        }
    }

    private Resource cargarFotoPorDefecto(Integer s) {
        // 0 = chica, 1 = chico (ajústalo si tu lógica es distinta)
        String nombre = (s != null && s == 1) ? defectoChico : defectoChica;
        String archivo = nombre + ".jpg";

        // 1. Intentar en el sistema de ficheros
        try {
            Resource recursoEnDisco = cargarComoRecurso(archivo);
            if (esRecursoValido(recursoEnDisco)) {
                return recursoEnDisco;
            }
        } catch (Exception e) {
            log.debug("La foto por defecto [{}] no está en disco, se intentará en el classpath.", archivo);
        }

        // 2. Fallback al classpath
        Resource recursoClasspath = new ClassPathResource("fotosPerfil/" + archivo);
        if (esRecursoValido(recursoClasspath)) {
            return recursoClasspath;
        }

        // 3. Si nada funciona, lanzar excepción controlada
        String msg = String.format("No se pudo encontrar la foto por defecto [%s] ni en disco ni en el classpath.", archivo);
        throw new FotoPerfilException(msg,null);
    }

    public void generarFichaMovilidadDocx(String militanteId, ServletOutputStream outputStream) {
        generarFichaMovilidadDocx(militanteId, (OutputStream) outputStream);
    }

    public void generarFichaMovilidadDocx(String militanteId, OutputStream  outputStream) {
        // 1. Obtener los datos de la ficha de movilidad
        ApiResponse apiResponse = restClient.obtenerDatosFichaMovilidad(militanteId).getBody();
        if (apiResponse == null || !apiResponse.getSuccess()) {
            log.error("No se pudieron obtener los datos de la ficha de movilidad para el militanteId: {}", militanteId);
            throw new RestClientException("No se pudieron obtener los datos de la ficha de movilidad.");
        }

        // Convertir los datos a un LinkedHashMap<String, String>
        LinkedHashMap<String, String> fichaMovilidadData = (LinkedHashMap<String, String>) apiResponse.getData();
        log.info("Datos de la ficha de movilidad obtenidos correctamente para el militanteId: {}", militanteId);

        // 2. Cargar la plantilla DOCX desde resources
        ClassPathResource docxTemplate = new ClassPathResource("plantillas/ficha_movilidad_interna_v1.docx");
        try (InputStream is = docxTemplate.getInputStream(); XWPFDocument doc = new XWPFDocument(is)) {

            // 3. Sustituir las llaves {{key}} en la plantilla con los valores correspondientes
            replacePlaceholdersInDocument(doc, fichaMovilidadData);

            // 4. Escribir el documento modificado al outputStream
            doc.write(outputStream);
            log.info("Archivo DOCX generado y enviado al outputStream para el militanteId: {}", militanteId);

        } catch (IOException e) {
            log.error("Error al procesar la plantilla DOCX para el militanteId: {}", militanteId, e);
            throw new RuntimeException("Error al generar el archivo DOCX", e);
        }

    }

    private void replacePlaceholdersInParagraph(XWPFParagraph paragraph, Map<String, String> data) {
        List<XWPFRun> runs = paragraph.getRuns();
        if (runs != null && !runs.isEmpty()) {
            StringBuilder paragraphText = new StringBuilder();
            for (XWPFRun run : runs) {
                paragraphText.append(run.getText(0));
            }

            String originalText = paragraphText.toString();
            log.info("OriginalText: {}", originalText);
            String newText = originalText;
            for (Map.Entry<String, String> entry : data.entrySet()) {
                String placeholder = "{{" + entry.getKey() + "}}";
                newText = newText.replace(placeholder, entry.getValue());
            }

            // Si se realizó un cambio, actualizamos los runs
            if (!newText.equals(originalText)) {
                // Remover todos los runs existentes
                for (int i = runs.size() - 1; i >= 0; i--) {
                    paragraph.removeRun(i);
                }

                // Crear nuevos runs con el texto modificado
                String[] parts = newText.split("(?<=\\G.{1000})");
                for (String part : parts) {
                    XWPFRun newRun = paragraph.createRun();
                    newRun.setText(part, 0);
                }
                log.debug("Reemplazo realizado en párrafo: {}", newText);
            }
        }
    }

    private void replacePlaceholdersInDocument(XWPFDocument doc, Map<String, String> data) {
        // Reemplazar en párrafos del cuerpo del documento
        for (XWPFParagraph paragraph : doc.getParagraphs()) {
            replacePlaceholdersInParagraph(paragraph, data);
        }

        // Reemplazar en tablas
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        replacePlaceholdersInParagraph(paragraph, data);
                    }
                }
            }
        }
    }

    public void convertirDocxAPdf(InputStream docxInputStream, OutputStream outputStream) {
        try {
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(docxInputStream);
            Docx4J.toPDF(wordMLPackage, outputStream);
        } catch (Exception e) {
            throw new RuntimeException("Error al convertir DOCX a PDF", e);
        }
    }


    @Transactional(readOnly = true)
    public Page<DocumentoRecibidoDTO> buscar(List<String> propietarios,
                                             int page, int size,
                                             NivelDocumento nivel,
                                             Confidencialidad confid,
                                             List<TipoDocumento> tipos,
                                             List<Categoria> categorias,
                                             String texto,
                                             String sortField,
                                             int sortOrder) {

        Specification<Documento> spec = Specification.where(propietarioIn(propietarios))
                .and(nivelEq(nivel))
                .and(confidencialidadEq(confid))
                .and(tipoIn(tipos))
                .and(categoriaIn(categorias))
                .and(textoLike(texto));

        Sort sort = Sort.by(sortOrder == 1 ? Sort.Direction.ASC : Sort.Direction.DESC,
                Optional.ofNullable(sortField).orElse("fechaSubida"));

        return documentoRepository.findAll(spec, PageRequest.of(page, size, sort))
                .map(mapper::toDTO);
    }

    public String subir(NuevoDocumentoDTO dto) {
        MultipartFile archivo = dto.getArchivo();
        log.info("Recibido fichero '{}' ({} bytes)",
                archivo.getOriginalFilename(), archivo.getSize());

        String extension    = FilenameUtils.getExtension(archivo.getOriginalFilename());
        String uuid         = UUID.randomUUID().toString();
        String nombreFisico = uuid + "." + extension;
        String subPath      = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String relativePath = subPath + "/" + nombreFisico;

        try (InputStream in = archivo.getInputStream()) {
            // 1️⃣ Guardar binario
            CryptoFileInfo info = storage.store(in, relativePath);

            // 5. Persistir metadatos
            Documento doc = Documento.builder()
                    .nombreOriginal(archivo.getOriginalFilename())
                    .nombreFisico(nombreFisico)
                    .extension(extension)
                    .mimeType(archivo.getContentType())
                    .tamanoBytes(archivo.getSize())
                    .checksumSha256(info.sha256Plain())
                    .ivB64(info.ivB64())
                    .dekWrappedB64(info.dekWrappedB64())
                    .nivel(calcularNivel(dto.getPropietario()))
                    .confidencialidad(dto.getConfidencialidad())
                    .tipo(dto.getTipo())
                    .propietario(dto.getPropietario())
                    .categorias(Set.of(dto.getCategoria()))
                    .fechaSubida(Instant.now())
                    .rutaRelativa(subPath + "/" + nombreFisico)
                    .build();
            documentoRepository.save(doc);

            return doc.getUuid();
        } catch (IOException e){
            throw new IllegalArgumentException("Error al subir el documento", e);
        }
    }

    @Transactional(readOnly = true)
    public DocumentoRecibidoDTO obtenerPorUuid(String uuid) {
        return documentoRepository.findByUuid(uuid)
                .map(mapper::toDTO)
                .orElseThrow(() -> new DocumentoNoEncontradoException(uuid));
    }

    @Transactional(readOnly = true)
    public FicheroDescarga obtenerFicheroPorUuid(String uuid) {

        Documento doc = documentoRepository.findByUuid(uuid)
                .orElseThrow(() -> new DocumentoNoEncontradoException(uuid));

        DecryptedFile file = null;
        try {
            file = storage.load(
                    doc.getRutaRelativa(),
                    new CryptoFileInfo(doc.getChecksumSha256(),
                            doc.getTamanoBytes(),
                            doc.getIvB64(),
                            doc.getDekWrappedB64()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (InputStream in = file.data()) {                 // auto-close

            try {                                                 // ② intentar poner marca
                WatermarkProcessor proc =
                        watermarkService.pick(doc.getMimeType());

                WatermarkInfo info = new WatermarkInfo(
                        doc.getUuid(),
                        SecurityContextHolder.getContext()
                                .getAuthentication().getName(),
                        Instant.now());

                proc.apply(in, baos, info);                       // puede lanzar Exception

            } catch (IllegalStateException noProcessor) {         // ③ mime no soportado
                log.debug("Sin procesador de watermark para {}", doc.getMimeType());
                in.transferTo(baos);                              // copiamos tal cual
            }

        } catch (IOException io) {                                // ④ IO local ⇒ unchecked
            throw new UncheckedIOException("Error copiando flujo", io);
        } catch (Exception e) {                                   // ⑤ fallo en apply()
            throw new IllegalStateException("Watermarking error", e);
        }

        InputStreamResource res = new InputStreamResource(
                new ByteArrayInputStream(baos.toByteArray()));

        return new FicheroDescarga(res,
                doc.getNombreOriginal(),
                doc.getMimeType());
    }

    @Transactional
    public DocumentoRecibidoDTO actualizar(String uuid, ActualizarDocumentoDTO dto) {
        Documento entidad = documentoRepository.findByUuid(uuid)
                .orElseThrow(() -> new DocumentoNoEncontradoException(uuid));

        mapper.updateEntityFromDto(dto, entidad);

        return mapper.toDTO(entidad);
    }

    @Transactional
    public void eliminar(String uuid) {
        Documento doc = documentoRepository.findByUuid(uuid)
                .orElseThrow(() -> new DocumentoNoEncontradoException(uuid));

        try {
            storage.delete(doc.getRutaRelativa());
        } catch (IOException e) {
            log.error("Error al eliminar el fichero físico {}", doc.getRutaRelativa(), e);
            // No borramos la metadata si falla la IO para evitar huérfanos incoherentes
            throw new IllegalStateException("No se pudo eliminar el fichero", e);
        }
        documentoRepository.delete(doc);
        log.info("Documento {} eliminado por", uuid);
    }

    /* ----------------------------------------------------------
       Cálculo de nivel:
       B… -> COLECTIVO
       C1…, C2… -> CENTRAL
       C… (resto) -> INTERMEDIO
       ---------------------------------------------------------- */
    private NivelDocumento calcularNivel(String propietario) {
        if (propietario == null || propietario.isBlank()) {
            return NivelDocumento.COLECTIVO;   // valor por defecto
        }

        char inicio = propietario.charAt(0);
        if (inicio == 'B') {
            return NivelDocumento.COLECTIVO;
        }
        if (inicio == 'C') {
            // Obtenemos el carácter siguiente (si existe)
            if (propietario.length() > 1) {
                char siguiente = propietario.charAt(1);
                if (siguiente == '1' || siguiente == '2') {
                    return NivelDocumento.CENTRAL;
                }
            }
            return NivelDocumento.INTERMEDIO;
        }
        // Si no coincide con los casos anteriores, se considera colectivo
        return NivelDocumento.COLECTIVO;
    }
}


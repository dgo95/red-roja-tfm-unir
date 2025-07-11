package es.juventudcomunista.redroja.cjcdocumentos.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.juventudcomunista.redroja.cjcdocumentos.services.CensoDataService;
import es.juventudcomunista.redroja.cjcdocumentos.services.ExcelService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import es.juventudcomunista.redroja.cjccommonutils.enums.TipoCenso;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {

    private final CensoDataService censoDataService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void exportToExcel(TipoCenso tipo, int id, HttpServletResponse response) throws IOException {
        List<Map<String, String>> data = censoDataService.getData(tipo, id);
        InputStream inputStream;
        Workbook workbook;
        Sheet sheet;

        switch (tipo) {
            case GENERAL:
                inputStream = new ClassPathResource("plantillas/censoGeneral_v1.xlsx").getInputStream();
                workbook = new XSSFWorkbook(inputStream);
                sheet = workbook.getSheet("COLECTIVO");
                log.debug("Plantilla de Excel GENERAL cargada");
                processGeneralData(sheet, data);
                break;
            case MOS:
                inputStream = new ClassPathResource("plantillas/censoLaboralSindical_v1.xlsx").getInputStream();
                workbook = new XSSFWorkbook(inputStream);
                sheet = workbook.getSheetAt(0); // Asume que la hoja está en el índice 0
                log.debug("Plantilla de Excel MOS cargada");
                processMosData(sheet, data);
                break;
            case MES:
                workbook = new XSSFWorkbook();
                sheet = workbook.createSheet("Censo MES");
                log.debug("Nueva hoja de Excel creada para MES");
                processMesData(sheet, data, workbook);
                break;
            default:
                throw new IllegalArgumentException("Tipo de censo desconocido: " + tipo);
        }

        log.info("Finalizada la inserción de datos en el Excel");

        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        evaluator.evaluateAll();
        log.debug("Fórmulas reevaluadas en el workbook");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        log.debug("Workbook escrito en el outputStream");

        // Configurar la respuesta HTTP
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=export_modificado.xlsx");
        response.getOutputStream().write(outputStream.toByteArray());
    }

    private void processGeneralData(Sheet sheet, List<Map<String, String>> data) {
        int rowNum = 1;
        for (Map<String, String> userData : data) {
            log.debug("Procesando fila: {}", rowNum);
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                row = sheet.createRow(rowNum);
            }

            setCellValue(row, 0, userData.get("nombre"));
            setCellValue(row, 1, userData.get("apellido"));
            setCellValue(row, 2, userData.get("apellido2"));
            setCellValue(row, 3, userData.get("sexo"));
            String formattedDate;
            int age;
            try {
                LocalDate date = LocalDate.parse(userData.get("fechaNacimiento"),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                formattedDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                LocalDate currentDate = LocalDate.now();
                age = Period.between(date, currentDate).getYears();
                Row row2 = sheet.getRow(rowNum + 79);
                if (row2 == null) {
                    row2 = sheet.createRow(rowNum + 79);
                }
                setCellValue(row2, 0, age);
            } catch (DateTimeParseException e) {
                log.warn("Formato de fecha incorrecto para 'fechaNacimiento': {}", userData.get("fechaNacimiento"), e);
                formattedDate = "";
            }

            setCellValue(row, 4, formattedDate);
            setCellValue(row, 5, userData.get("estudiante"));
            setCellValue(row, 6, userData.get("centroEstudios"));
            setCellValue(row, 7, userData.get("sindicatoEstudiantil"));
            setCellValue(row, 8, userData.get("trabajador"));
            setCellValue(row, 9, userData.get("sectorLaboral"));
            setCellValue(row, 10, userData.get("sindicatoLaboral"));
            setCellValue(row, 11, userData.get("correoElectronico"));
            setCellValue(row, 12, userData.get("telefono"));
            setCellValue(row, 13, userData.get("idiomas"));
            Object habilidadesObj = userData.get("habilidades");
            LinkedHashMap<String, String> habilidades = null;
            if (habilidadesObj instanceof LinkedHashMap) {
                habilidades = (LinkedHashMap<String, String>) habilidadesObj;
            }

            setCellValue(row, 14, habilidades.get("ArtesMarciales"));
            setCellValue(row, 15, habilidades.get("HabilidadEnInstrumentoOCanto"));
            setCellValue(row, 16, habilidades.get("ConocimientosMusicales"));
            setCellValue(row, 17, habilidades.get("EstudiosMusicales"));
            setCellValue(row, 18, habilidades.get("ExperienciasTocandoEnPúblico"));
            setCellValue(row, 19, habilidades.get("ConocimientosEnDiseñoGráfico"));
            setCellValue(row, 20, habilidades.get("OtrosConocimientosTécnicos"));
            setCellValue(row, 21, userData.get("numeroCarnet"));
            rowNum++;
        }
    }

    private void processMosData(Sheet sheet, List<Map<String, String>> data) {
        int rowNum = 2;
        for (Map<String, String> userData : data) {
            log.debug("Procesando fila: {}", rowNum);
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                row = sheet.createRow(rowNum);
            }

            setCellValue(row, 1, userData.get("colectivo"));
            setCellValue(row, 2, userData.get("nombre"));
            setCellValue(row, 3, userData.get("apellido"));
            setCellValue(row, 4, userData.get("apellido2"));
            setCellValue(row, 5, userData.get("actividadEconomica"));
            setCellValue(row, 6, userData.get("profesion"));
            setCellValue(row, 7, userData.get("nombreEmpresa"));
            Object numero = userData.get("numeroTrabajadoresEmpresa");
            Integer i = null;
            if (numero instanceof Integer) {
                i = (Integer) numero;
            }
            setCellValue(row, 8, i.toString());
            setCellValue(row, 9, userData.get("nombreCentroTrabajo"));
            numero = userData.get("numeroTrabajadoresCentroTrabajo");
            if (numero instanceof Integer) {
                i = (Integer) numero;
            }
            setCellValue(row, 10, i.toString());
            setCellValue(row, 11, userData.get("existeOrganoRepresentacionTrabajadores"));
            setCellValue(row, 12, userData.get("participaOrganoRepresentacion"));
            setCellValue(row, 13, userData.get("tipoContrato"));
            numero = userData.get("antiguedad");
            if (numero instanceof Integer) {
                i = (Integer) numero;
            }
            setCellValue(row, 14, i.toString());
            setCellValue(row, 15, userData.get("sindicato"));
            setCellValue(row, 16, userData.get("federacion"));
            setCellValue(row, 17, userData.get("cargo"));
            setCellValue(row, 18, userData.get("participaAreaJuventud"));
            rowNum++;
        }
    }

    private void processMesData(Sheet sheet, List<Map<String, String>> data, Workbook workbook) {
        // Establecer estilos de celda para la cabecera
        CellStyle headerCellStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Crear la cabecera
        Row headerRow = sheet.createRow(0);
        if (!data.isEmpty()) {
            Map<String, String> firstRow = data.get(0);
            int cellIdx = 0;
            for (String key : firstRow.keySet()) {
                if (key.equals("id")) continue; // Saltar la columna id

                String headerName = convertCamelCaseToTitleCase(key);
                if (headerName.equals("Anho Finalizacion")) {
                    headerName = "Año Finalización";
                }else if(headerName.equals("Subdivision nivel educativo")){
                    headerName = "Tipo Educativo";
                }else if(headerName.equals("Subsubdivision nivel educativo")){
                    headerName = "Subtipo de estudio";
                }

                Cell cell = headerRow.createCell(cellIdx++);
                cell.setCellValue(headerName);
                cell.setCellStyle(headerCellStyle);
            }
        }

        // Rellenar los datos
        int rowNum = 1;
        for (Map<String, String> userData : data) {
            log.debug("Procesando fila: {}", rowNum);
            Row row = sheet.createRow(rowNum++);
            int cellIdx = 0;
            for (Map.Entry<String, String> entry : userData.entrySet()) {
                if (entry.getKey().equals("id")) continue; // Saltar la columna id

                Cell cell = row.createCell(cellIdx++);

                String valueS = "";
                Object value = entry.getValue();
                if (value instanceof String) {
                    valueS = (String) value;
                } else if (value instanceof Integer) {
                    valueS = ((Integer) value).toString();
                }
                cell.setCellValue(valueS);
            }
        }

        // Ajustar el tamaño de las columnas
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private String convertCamelCaseToTitleCase(String camelCase) {
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        for (char c : camelCase.toCharArray()) {
            if (Character.isUpperCase(c) && titleCase.length() > 0) {
                titleCase.append(' ');
            }
            if (nextTitleCase) {
                titleCase.append(Character.toUpperCase(c));
                nextTitleCase = false;
            } else {
                titleCase.append(Character.toLowerCase(c));
            }
        }

        return titleCase.toString();
    }

    private void setCellValue(Row row, int cellIndex, String value) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            cell = row.createCell(cellIndex);
        }
        cell.setCellValue(value);
    }

    private void setCellValue(Row row, int cellIndex, int value) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            cell = row.createCell(cellIndex);
        }
        cell.setCellValue(value);
    }
}

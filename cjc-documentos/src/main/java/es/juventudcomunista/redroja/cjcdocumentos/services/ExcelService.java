package es.juventudcomunista.redroja.cjcdocumentos.services;

import es.juventudcomunista.redroja.cjccommonutils.enums.TipoCenso;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface ExcelService {
    void exportToExcel(TipoCenso tipo, int id, HttpServletResponse response) throws IOException;
}

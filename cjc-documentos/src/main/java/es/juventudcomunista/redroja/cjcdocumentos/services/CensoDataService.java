package es.juventudcomunista.redroja.cjcdocumentos.services;

import es.juventudcomunista.redroja.cjccommonutils.enums.TipoCenso;
import java.util.List;
import java.util.Map;

public interface CensoDataService {
    List<Map<String, String>> getData(TipoCenso tipo, int id);
}
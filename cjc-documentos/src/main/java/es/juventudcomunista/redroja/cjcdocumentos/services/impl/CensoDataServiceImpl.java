package es.juventudcomunista.redroja.cjcdocumentos.services.impl;

import es.juventudcomunista.redroja.cjcdocumentos.services.CensoDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import es.juventudcomunista.redroja.cjccommonutils.enums.TipoCenso;
import es.juventudcomunista.redroja.cjccommonutils.service.RestClient;
import es.juventudcomunista.redroja.cjccommonutils.web.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CensoDataServiceImpl implements CensoDataService {

    private final RestClient restClient;

    @Override
    public List<Map<String, String>> getData(TipoCenso tipo, int id) {
        ResponseEntity<ApiResponse> response = restClient.getCenso(id, tipo, 0, 1000);
        ApiResponse apiResponse = response.getBody();

        if (apiResponse != null) {
            if (apiResponse.getData() instanceof LinkedHashMap) {
                LinkedHashMap<?, ?> rawData = (LinkedHashMap<?, ?>) apiResponse.getData();
                List<Map<String, String>> data = new ArrayList<>();
                for (Object value : rawData.values()) {
                    if (value instanceof List) {
                        List<?> list = (List<?>) value;
                        for (Object item : list) {
                            if (item instanceof Map) {
                                data.add((Map<String, String>) item);
                            } else {
                                log.error("Item in list is not of expected type Map<String, String>: {}", item.getClass());
                            }
                        }
                        break;
                    } else {
                        log.error("Value is not of expected type List: {}", value.getClass());
                    }
                }
                return data;
            } else {
                log.error("Data is not of expected type List or LinkedHashMap: {}", apiResponse.getData().getClass());
                return Collections.emptyList();
            }
        } else {
            log.error("ApiResponse is null");
            return Collections.emptyList();
        }
    }
}
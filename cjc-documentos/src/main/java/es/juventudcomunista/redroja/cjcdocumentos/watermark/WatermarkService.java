package es.juventudcomunista.redroja.cjcdocumentos.watermark;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WatermarkService {

    private final List<WatermarkProcessor> processors;

    public WatermarkService(List<WatermarkProcessor> processors) {
        this.processors = processors;
    }

    /**
     * Devuelve el primer procesador que afirme soportar el mime.
     */
    public WatermarkProcessor pick(String mime) {
        return processors.stream()
                .filter(p -> p.supports(mime))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No hay procesador de marca de agua para " + mime));
    }
}

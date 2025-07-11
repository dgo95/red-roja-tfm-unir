package es.juventudcomunista.redroja.cjcrest.dto;

import lombok.Data;

@Data
public class CuotaRequest {
    private double salario;
    private boolean esAnual;
    private Boolean tienePagasExtras;
    private Double cuantiaPagasExtras;
}

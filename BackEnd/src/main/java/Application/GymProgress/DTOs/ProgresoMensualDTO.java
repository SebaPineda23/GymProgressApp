package Application.GymProgress.DTOs;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class ProgresoMensualDTO {
    private LocalDate mes;
    private Double pesoInicialUsuario;
    private Double pesoFinalUsuario;
    private Double cambioPeso;
    private Double totalPesoLevantado;
    private Integer totalEntrenamientos;
    private Double promedioFuerzaSemanal;
    private List<ProgresoSemanalDTO> semanas;
    private String tendencia; // "MEJORANDO", "ESTABLE", "BAJANDO"
}

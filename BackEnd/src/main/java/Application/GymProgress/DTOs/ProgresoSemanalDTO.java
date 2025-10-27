package Application.GymProgress.DTOs;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ProgresoSemanalDTO {
    private LocalDate semanaInicio;
    private LocalDate semanaFin;
    private Double pesoPromedioUsuario;
    private Double totalPesoLevantado;
    private Integer totalRepeticiones;
    private Integer totalSeriesCompletadas;
    private Integer entrenamientosCompletados;
    private Double progresoFuerza; // % de mejora
    private Double volumenEntrenamiento; // peso * repeticiones
}
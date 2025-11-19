package Application.GymProgress.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MesComparacionDTO {

    private String nombre;

    private double pesoInicial;
    private double pesoFinal;
    private double cambioPeso;

    private double totalPesoLevantado;
    private int totalEntrenamientos;

    private double promedioFuerzaSemanal;
    private String tendencia;
}


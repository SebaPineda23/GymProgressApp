package Application.GymProgress.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatsResponseDTO {
    private int entrenamientosEsteMes;
    private int rachaActual;
    private double pesoTotalLevantado;
}

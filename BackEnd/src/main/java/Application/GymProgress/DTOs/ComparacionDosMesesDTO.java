package Application.GymProgress.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ComparacionDosMesesDTO {
    private MesComparacionDTO mes1;
    private MesComparacionDTO mes2;
}


package Application.GymProgress.DTOs;

import jakarta.validation.constraints.NotNull; // Importar NotNull
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DTORoutine {

    @NotNull(message = "El nombre de la rutina es obligatorio")
    @Size(min = 1, message = "El nombre de la rutina es obligatorio")
    private String nombre;

    @NotNull(message = "El objetivo de la rutina es obligatorio")
    @Size(min = 1, message = "El objetivo de la rutina es obligatorio")
    private String objetivo;

    @NotNull(message = "El listado de ejercicios no debe ser nulo")
    @Size(min = 4, message = "Debe seleccionar al menos 4 ejercicios")
    @Size(max = 6, message = "No se pueden agregar más de 6 ejercicios a la rutina")
    private List<Long> exercisesIds;
}
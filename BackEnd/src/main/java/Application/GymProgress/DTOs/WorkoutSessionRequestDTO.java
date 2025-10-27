package Application.GymProgress.DTOs;

import lombok.Data;
import java.time.LocalDate;

@Data
public class WorkoutSessionRequestDTO {
    private Long userId;
    private Long routineId;
    private LocalDate date;
    private String notes;
}
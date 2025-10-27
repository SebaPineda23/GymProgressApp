package Application.GymProgress.DTOs;


import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class WorkoutSessionResponseDTO {
    private Long id;
    private LocalDate date;
    private String notes;
    private Boolean completed;
    private RoutineDTO routine;
    private List<ExerciseExecutionDTO> exerciseExecutions;

    @Data
    public static class RoutineDTO {
        private Long id;
        private String name;
        private String objective;
    }

    @Data
    public static class ExerciseExecutionDTO {
        private Long id;
        private ExerciseDTO exercise;
        private Integer plannedSets;
        private Integer plannedReps;
        private List<SetRecordDTO> setRecords;
    }

    @Data
    public static class ExerciseDTO {
        private Long id;
        private String name;
        private String description;
        private List<String> muscleGroupSet;
    }

    @Data
    public static class SetRecordDTO {
        private Long id;
        private Integer setNumber;
        private Double weightUsed;
        private Integer realRepetitions;
        private Integer difficultyPerceived;
        private Boolean easyComplete;
    }
}

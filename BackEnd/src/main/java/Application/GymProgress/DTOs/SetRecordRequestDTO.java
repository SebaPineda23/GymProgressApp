package Application.GymProgress.DTOs;

import lombok.Data;

@Data
public class SetRecordRequestDTO {
    private Long workoutSessionId;
    private Long exerciseId;
    private Integer setNumber;
    private Double weightUsed;
    private Integer realRepetitions;
    private Integer difficultyPerceived;
    private Boolean easyComplete;
}

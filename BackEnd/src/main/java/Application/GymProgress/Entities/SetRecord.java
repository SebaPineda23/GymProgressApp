package Application.GymProgress.Entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "set_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SetRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer setNumber;
    private Double weightUsed;
    private Integer realRepetitions;
    private Integer difficultyPerceived;
    private Boolean easyComplete;

    @ManyToOne
    @JoinColumn(name = "exercise_execution_id", nullable = false)
    @JsonIgnore
    private ExerciseExecution exerciseExecution;
}

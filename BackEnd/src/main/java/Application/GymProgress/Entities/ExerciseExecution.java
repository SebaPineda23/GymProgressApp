package Application.GymProgress.Entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exercise_executions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseExecution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "workout_session_id", nullable = false)
    @JsonIgnore
    private WorkoutSession workoutSession;

    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    @JsonIgnore
    private Exercise exercise;

    private Integer plannedSets;
    private Integer plannedReps;

    @OneToMany(mappedBy = "exerciseExecution", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<SetRecord> setRecords;
    public List<SetRecord> getSetRecords() {
        if (this.setRecords == null) {
            this.setRecords = new ArrayList<>();
        }
        return this.setRecords;
    }
}

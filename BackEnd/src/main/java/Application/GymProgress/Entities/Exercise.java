package Application.GymProgress.Entities;

import Application.GymProgress.Enum.Level;
import Application.GymProgress.Enum.MuscleGroup;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "exercise_muscle_groups",
            joinColumns = @JoinColumn(name = "exercise_id")
    )
    private Set<MuscleGroup> muscleGroupSet;
    @Enumerated(EnumType.STRING)
    private Level level;
}

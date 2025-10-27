package Application.GymProgress.Repositories;

import Application.GymProgress.Entities.Exercise;
import Application.GymProgress.Enum.Level;
import Application.GymProgress.Enum.MuscleGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByMuscleGroupSetContaining(MuscleGroup muscleGroup);
    List<Exercise> findByLevel(Application.GymProgress.Enum.Level level);
}

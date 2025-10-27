package Application.GymProgress.Repositories;

import Application.GymProgress.Entities.ExerciseExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseExecutionRepository extends JpaRepository<ExerciseExecution, Long> {
    List<ExerciseExecution> findByWorkoutSessionId(Long workoutSessionId);
}

package Application.GymProgress.Repositories;

import Application.GymProgress.Entities.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {
    List<WorkoutSession> findByUserId(Long userId);
    List<WorkoutSession> findByUserIdAndCompleted(Long userId, boolean completed);
}

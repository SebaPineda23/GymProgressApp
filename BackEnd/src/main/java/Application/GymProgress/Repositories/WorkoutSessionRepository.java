package Application.GymProgress.Repositories;

import Application.GymProgress.Entities.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {
    List<WorkoutSession> findByUserId(Long userId);
    List<WorkoutSession> findByUserIdAndCompleted(Long userId, boolean completed);

    @Query("SELECT w FROM WorkoutSession w " +
            "WHERE w.user.id = :userId " +
            "AND MONTH(w.date) = :mes " +
            "AND YEAR(w.date) = :anio " +
            "ORDER BY w.date ASC")
    List<WorkoutSession> findByUserIdAndMonth(
            @Param("userId") Long userId,
            @Param("mes") int mes,
            @Param("anio") int anio
    );
}

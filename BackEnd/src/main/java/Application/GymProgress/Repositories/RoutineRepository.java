package Application.GymProgress.Repositories;

import Application.GymProgress.Entities.Routine;
import Application.GymProgress.Enum.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long> {
    List<Routine>findByUserId(Long id);
}


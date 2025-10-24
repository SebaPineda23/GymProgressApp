package Application.GymProgress.Repositories;

import Application.GymProgress.Entities.Exercise;
import Application.GymProgress.Entities.Register;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface RegisterRepository extends JpaRepository<Register, Long> {
    List<Register> findByUserId(Long id);
    List<Register> findByUserIdOrderByDateAsc(Long userId);


    @Query("SELECT r FROM Register r WHERE r.user.id = :userId AND r.date BETWEEN :startDate AND :endDate")
    List<Register> findByUserAndDateBetween(@Param("userId") Long userId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    @Query("SELECT r FROM Register r WHERE r.user.id = :userId AND r.exercise IN :exercises AND r.date BETWEEN :startDate AND :endDate")
    List<Register> findByUserAndExercisesAndDateBetween(@Param("userId") Long userId,
                                                        @Param("exercises") List<Exercise> exercises,
                                                        @Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate);


}

package Application.GymProgress.Repositories;

import Application.GymProgress.Entities.WeightRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WeightRecordRepository extends JpaRepository<WeightRecord, Long> {

    @Query("""
        SELECT w FROM WeightRecord w
        WHERE w.user.id = :userId
        AND w.date BETWEEN :inicio AND :fin
        ORDER BY w.date ASC
    """)
    List<WeightRecord> findByUserAndMonth(
            @Param("userId") Long userId,
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin
    );

    // Peso inicial del mes
    @Query("""
        SELECT w FROM WeightRecord w
        WHERE w.user.id = :userId
        AND w.date BETWEEN :inicio AND :fin
        ORDER BY w.date ASC
    """)
    WeightRecord findFirstByUserAndMonth(
            @Param("userId") Long userId,
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin
    );

    // Peso final del mes
    @Query("""
        SELECT w FROM WeightRecord w
        WHERE w.user.id = :userId
        AND w.date BETWEEN :inicio AND :fin
        ORDER BY w.date DESC
    """)
    WeightRecord findLastByUserAndMonth(
            @Param("userId") Long userId,
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin
    );
    WeightRecord findTopByUserIdAndDateBeforeOrderByDateDesc(Long userId, LocalDate fecha);

}


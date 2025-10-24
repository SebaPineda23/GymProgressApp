package Application.GymProgress.Services;

import Application.GymProgress.DTOs.DTOProgressComparison;
import Application.GymProgress.DTOs.DTOProgressStats;
import Application.GymProgress.Entities.Exercise;
import Application.GymProgress.Entities.Register;
import Application.GymProgress.Entities.Routine;
import Application.GymProgress.Repositories.RegisterRepository;
import Application.GymProgress.Repositories.RoutineRepository;
import Application.GymProgress.Repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ProgressService {

    private final RegisterRepository registerRepository;
    private final UserRepository userRepository;
    private final RoutineRepository routineRepository;

    public ProgressService(RegisterRepository registerRepository, UserRepository userRepository, RoutineRepository routineRepository) {
        this.registerRepository = registerRepository;
        this.userRepository = userRepository;
        this.routineRepository = routineRepository;
    }

    // Progreso general del usuario
    public DTOProgressComparison getWeeklyProgress(Long userId, LocalDate referenceDate) {
        LocalDate weekStart = referenceDate.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6);
        LocalDate previousWeekStart = weekStart.minusWeeks(1);
        LocalDate previousWeekEnd = weekEnd.minusWeeks(1);

        DTOProgressStats currentWeek = calculateStats(userId, weekStart, weekEnd, null);
        DTOProgressStats previousWeek = calculateStats(userId, previousWeekStart, previousWeekEnd, null);

        return compareProgress(currentWeek, previousWeek);
    }

    public DTOProgressComparison getMonthlyProgress(Long userId, LocalDate referenceDate) {
        LocalDate monthStart = referenceDate.withDayOfMonth(1);
        LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
        LocalDate previousMonthStart = monthStart.minusMonths(1);
        LocalDate previousMonthEnd = previousMonthStart.plusMonths(1).minusDays(1);

        DTOProgressStats currentMonth = calculateStats(userId, monthStart, monthEnd, null);
        DTOProgressStats previousMonth = calculateStats(userId, previousMonthStart, previousMonthEnd, null);

        return compareProgress(currentMonth, previousMonth);
    }

    // Progreso específico por rutina
    public DTOProgressComparison getRoutineWeeklyProgress(Long userId, Long routineId, LocalDate referenceDate) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        LocalDate weekStart = referenceDate.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6);
        LocalDate previousWeekStart = weekStart.minusWeeks(1);
        LocalDate previousWeekEnd = weekEnd.minusWeeks(1);

        DTOProgressStats currentWeek = calculateStats(userId, weekStart, weekEnd, routine.getExerciseList());
        DTOProgressStats previousWeek = calculateStats(userId, previousWeekStart, previousWeekEnd, routine.getExerciseList());

        return compareProgress(currentWeek, previousWeek);
    }

    public DTOProgressComparison getRoutineMonthlyProgress(Long userId, Long routineId, LocalDate referenceDate) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        LocalDate monthStart = referenceDate.withDayOfMonth(1);
        LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
        LocalDate previousMonthStart = monthStart.minusMonths(1);
        LocalDate previousMonthEnd = previousMonthStart.plusMonths(1).minusDays(1);

        DTOProgressStats currentMonth = calculateStats(userId, monthStart, monthEnd, routine.getExerciseList());
        DTOProgressStats previousMonth = calculateStats(userId, previousMonthStart, previousMonthEnd, routine.getExerciseList());

        return compareProgress(currentMonth, previousMonth);
    }

    private DTOProgressStats calculateStats(Long userId, LocalDate startDate, LocalDate endDate, List<Exercise> routineExercises) {
        List<Register> registers;

        if (routineExercises != null && !routineExercises.isEmpty()) {
            // Filtrar por ejercicios de la rutina específica
            registers = registerRepository.findByUserAndExercisesAndDateBetween(userId, routineExercises, startDate, endDate);
        } else {
            // Todos los registros del usuario
            registers = registerRepository.findByUserAndDateBetween(userId, startDate, endDate);
        }

        if (registers.isEmpty()) {
            return new DTOProgressStats(0, 0, 0, 0, 0, 0);
        }

        double totalVolume = registers.stream()
                .mapToDouble(r -> r.getWeightUsed() * r.getRealRepetitions())
                .sum();

        double maxWeight = registers.stream()
                .mapToDouble(Register::getWeightUsed)
                .max()
                .orElse(0);

        int totalRepetitions = registers.stream()
                .mapToInt(Register::getRealRepetitions)
                .sum();

        double successRate = registers.stream()
                .mapToDouble(r -> r.getRealRepetitions() >= r.getPlanedRepetitions() ? 1.0 : 0.0)
                .average()
                .orElse(0) * 100;

        int sessionsCompleted = (int) registers.stream()
                .map(Register::getDate)
                .distinct()
                .count();

        double averageDifficulty = registers.stream()
                .mapToInt(Register::getDifficultPerceived)
                .average()
                .orElse(0);

        return new DTOProgressStats(totalVolume, maxWeight, totalRepetitions, successRate, sessionsCompleted, averageDifficulty);
    }

    private DTOProgressComparison compareProgress(DTOProgressStats current, DTOProgressStats previous) {
        DTOProgressComparison comparison = new DTOProgressComparison();
        comparison.setCurrentPeriod(current);
        comparison.setPreviousPeriod(previous);

        // Calcular cambios porcentuales para cada métrica
        double volumeProgress = calculatePercentageChange(current.getTotalVolume(), previous.getTotalVolume());
        double weightProgress = calculatePercentageChange(current.getMaxWeight(), previous.getMaxWeight());
        double repsProgress = calculatePercentageChange(current.getTotalRepetitions(), previous.getTotalRepetitions());
        double successProgress = current.getSuccessRate() - previous.getSuccessRate();
        double difficultyProgress = previous.getAverageDifficulty() == 0 ? 0 :
                ((current.getAverageDifficulty() - previous.getAverageDifficulty()) / previous.getAverageDifficulty()) * 100;

        // Progreso general (ponderado)
        double overallProgress = (volumeProgress * 0.3) + (weightProgress * 0.25) +
                (repsProgress * 0.2) + (successProgress * 0.15) + (difficultyProgress * 0.1);

        comparison.setProgressPercentage(round(overallProgress, 2));
        comparison.setTrend(determineTrend(overallProgress));

        // Desglose detallado
        Map<String, Double> breakdown = new HashMap<>();
        breakdown.put("volume", round(volumeProgress, 2));
        breakdown.put("maxWeight", round(weightProgress, 2));
        breakdown.put("repetitions", round(repsProgress, 2));
        breakdown.put("successRate", round(successProgress, 2));
        breakdown.put("difficulty", round(difficultyProgress, 2));
        comparison.setMetricBreakdown(breakdown);

        return comparison;
    }

    private double calculatePercentageChange(double current, double previous) {
        if (previous == 0) return current > 0 ? 100.0 : 0.0;
        return ((current - previous) / previous) * 100;
    }

    private String determineTrend(double progress) {
        if (progress > 5) return "IMPROVING";
        if (progress < -5) return "DECLINING";
        return "STABLE";
    }

    private double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
}
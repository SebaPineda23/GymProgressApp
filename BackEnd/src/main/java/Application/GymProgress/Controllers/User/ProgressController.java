package Application.GymProgress.Controllers.User;

import Application.GymProgress.DTOs.DTOProgressComparison;
import Application.GymProgress.Services.ProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@CrossOrigin("*")
@RequestMapping("/gymProgress/progress")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @GetMapping("/weekly/{userId}")
    public ResponseEntity<DTOProgressComparison> getWeeklyProgress(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}") LocalDate date) {

        DTOProgressComparison progress = progressService.getWeeklyProgress(userId, date);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/monthly/{userId}")
    public ResponseEntity<DTOProgressComparison> getMonthlyProgress(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}") LocalDate date) {

        DTOProgressComparison progress = progressService.getMonthlyProgress(userId, date);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/routine/{userId}/{routineId}/weekly")
    public ResponseEntity<DTOProgressComparison> getRoutineWeeklyProgress(
            @PathVariable Long userId,
            @PathVariable Long routineId,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}") LocalDate date) {

        DTOProgressComparison progress = progressService.getRoutineWeeklyProgress(userId, routineId, date);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/routine/{userId}/{routineId}/monthly")
    public ResponseEntity<DTOProgressComparison> getRoutineMonthlyProgress(
            @PathVariable Long userId,
            @PathVariable Long routineId,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}") LocalDate date) {

        DTOProgressComparison progress = progressService.getRoutineMonthlyProgress(userId, routineId, date);
        return ResponseEntity.ok(progress);
    }
}
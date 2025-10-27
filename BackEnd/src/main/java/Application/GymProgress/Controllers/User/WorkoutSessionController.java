package Application.GymProgress.Controllers.User;


import Application.GymProgress.DTOs.SetRecordRequestDTO;
import Application.GymProgress.DTOs.WorkoutSessionRequestDTO;
import Application.GymProgress.DTOs.WorkoutSessionResponseDTO;
import Application.GymProgress.Entities.WorkoutSession;
import Application.GymProgress.Services.WorkoutSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/gymProgress/workout-sessions")
@RequiredArgsConstructor
public class WorkoutSessionController {

    private final WorkoutSessionService workoutSessionService;

    @PostMapping
    public ResponseEntity<WorkoutSessionResponseDTO> createWorkoutSession(@RequestBody WorkoutSessionRequestDTO request) {
        try {
            System.out.println("🔍 DEBUG - Request recibido:");
            System.out.println("  userId: " + request.getUserId());
            System.out.println("  routineId: " + request.getRoutineId());
            System.out.println("  date: " + request.getDate());
            System.out.println("  notes: " + request.getNotes());

            LocalDate fechaEntrenamiento = request.getDate() != null ?
                    request.getDate() :
                    LocalDate.now();

            System.out.println("DEBUG - Fecha a usar: " + fechaEntrenamiento);

            WorkoutSessionResponseDTO sesion = workoutSessionService.createWorkoutSession(request, fechaEntrenamiento);
            return ResponseEntity.ok(sesion);

        } catch (Exception e) {
            System.out.println("ERROR en createWorkoutSession:");
            e.printStackTrace();

            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/set-record")
    public ResponseEntity<WorkoutSessionResponseDTO> addSetRecord(@RequestBody SetRecordRequestDTO request) {
        WorkoutSessionResponseDTO session = workoutSessionService.addSetRecord(request);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutSessionResponseDTO> getWorkoutSession(@PathVariable Long id) {
        WorkoutSessionResponseDTO session = workoutSessionService.getWorkoutSession(id);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WorkoutSessionResponseDTO>> getUserWorkoutSessions(@PathVariable Long userId) {
        List<WorkoutSessionResponseDTO> sessions = workoutSessionService.getUserWorkoutSessions(userId);
        return ResponseEntity.ok(sessions);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<WorkoutSessionResponseDTO> completeWorkoutSession(@PathVariable Long id) {
        WorkoutSessionResponseDTO session = workoutSessionService.completeWorkoutSession(id);
        return ResponseEntity.ok(session);
    }
}
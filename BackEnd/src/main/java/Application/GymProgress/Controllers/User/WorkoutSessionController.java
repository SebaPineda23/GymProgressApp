package Application.GymProgress.Controllers.User;


import Application.GymProgress.DTOs.SetRecordRequestDTO;
import Application.GymProgress.DTOs.WorkoutSessionRequestDTO;
import Application.GymProgress.DTOs.WorkoutSessionResponseDTO;
import Application.GymProgress.Services.WorkoutSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/gymProgress/workout-sessions")
@RequiredArgsConstructor
public class WorkoutSessionController {

    private final WorkoutSessionService workoutSessionService;

    @PostMapping
    public ResponseEntity<WorkoutSessionResponseDTO> createWorkoutSession(@RequestBody WorkoutSessionRequestDTO request) {
        WorkoutSessionResponseDTO session = workoutSessionService.createWorkoutSession(request);
        return ResponseEntity.ok(session);
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
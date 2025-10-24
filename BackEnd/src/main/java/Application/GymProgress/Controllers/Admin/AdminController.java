package Application.GymProgress.Controllers.Admin;

import Application.GymProgress.Auth.AuthResponseRegister;
import Application.GymProgress.Auth.AuthService;
import Application.GymProgress.DTOs.DTOExercise;
import Application.GymProgress.Entities.Exercise;
import Application.GymProgress.RegisterRequest.RegisterRequest;
import Application.GymProgress.Services.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController
@RequiredArgsConstructor
@RequestMapping("/Admin/gymProgress")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final ExerciseService exerciseService;
    private final AuthService authService;
    @PostMapping("/exercises")
    public ResponseEntity<Exercise> createExercise(@RequestBody DTOExercise dto) {
        Exercise exercise = new Exercise();
        exercise.setName(dto.getName());
        exercise.setDescription(dto.getDescription());
        exercise.setLevel(dto.getLevel());
        exercise.setMuscleGroupSet(dto.getMuscleGroupSet());
        return ResponseEntity.ok(exerciseService.createExercise(exercise));
    }
    @PostMapping("/auth/register/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody RegisterRequest request) {
        try {
            AuthResponseRegister response = authService.registerAdmin(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @PutMapping("/exercise/{id}")
    public ResponseEntity<Exercise> updateExercise(@PathVariable Long id, @RequestBody DTOExercise dto) {
        Exercise updatedExercise = new Exercise();
        updatedExercise.setName(dto.getName());
        updatedExercise.setMuscleGroupSet(dto.getMuscleGroupSet());
        updatedExercise.setDescription(dto.getDescription());
        return ResponseEntity.ok(exerciseService.updateExercise(id, updatedExercise));
    }

    @DeleteMapping("/exercise/{id}")
    public ResponseEntity<Void> deleteExercise(@PathVariable Long id) {
        exerciseService.deleteExercise(id);
        return ResponseEntity.noContent().build();
    }
}



package Application.GymProgress.Services;

import Application.GymProgress.Entities.Exercise;
import Application.GymProgress.Repositories.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }

    public Exercise getExerciseById(Long id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ejercicio no encontrado"));
    }
    public List<Exercise> getExercisesByRoutine(Long routineId) {
        return exerciseRepository.findByRoutineId(routineId);
    }

    public Exercise createExercise(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }

    public Exercise updateExercise(Long id, Exercise updatedExercise) {
        Exercise exercise = getExerciseById(id);

        exercise.setName(updatedExercise.getName());
        exercise.setDescription(updatedExercise.getDescription());

        return exerciseRepository.save(exercise);
    }

    public void deleteExercise(Long id) {
        Exercise exercise = getExerciseById(id);
        exerciseRepository.delete(exercise);
    }
}


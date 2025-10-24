package Application.GymProgress.Services;

import Application.GymProgress.Entities.Exercise;
import Application.GymProgress.Entities.Routine;
import Application.GymProgress.Entities.User;
import Application.GymProgress.Repositories.ExerciseRepository;
import Application.GymProgress.Repositories.RoutineRepository;
import Application.GymProgress.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RoutineService {

    private final RoutineRepository routineRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;
    private static final int MIN_EXERCISES = 4;
    private static final int MAX_EXERCISES = 7;

    public Routine crearRoutineUser(Long userId, String nombre, String objetivo, List<Long> exerciseIds) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        Set<Long> uniqueExerciseIds = new HashSet<>(exerciseIds);

        if (uniqueExerciseIds.size() < MIN_EXERCISES || uniqueExerciseIds.size() > MAX_EXERCISES) {
            throw new RuntimeException("La rutina debe tener entre " + MIN_EXERCISES + " y " + MAX_EXERCISES + " ejercicios");
        }

        List<Exercise> ejercicios = exerciseRepository.findAllById(uniqueExerciseIds);

        if (ejercicios.size() != uniqueExerciseIds.size()) {
            throw new RuntimeException("Algunos ejercicios no fueron encontrados");
        }

        Routine nuevaRutina = new Routine();
        nuevaRutina.setName(nombre);
        nuevaRutina.setObjective(objetivo);
        nuevaRutina.setUser(user);
        nuevaRutina.setExerciseList(ejercicios);

        return routineRepository.save(nuevaRutina);
    }

    public List<Routine> getRoutinesByUser(User user) {
        return routineRepository.findByUserId(user.getId());
    }

    public Routine getRoutineById(Long id) {
        return routineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));
    }

    public Routine updateRutina(Routine rutina, String nombre, String objetivo, List<Long> exerciseIds) {
        List<Exercise> ejercicios = exerciseRepository.findAllById(new HashSet<>(exerciseIds));
        rutina.setName(nombre);
        rutina.setObjective(objetivo);
        rutina.setExerciseList(ejercicios);
        return routineRepository.save(rutina);
    }

    public void deleteRoutine(Routine rutina) {
        routineRepository.delete(rutina);
    }

}

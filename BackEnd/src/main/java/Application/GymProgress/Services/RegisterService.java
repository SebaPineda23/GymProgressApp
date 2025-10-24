package Application.GymProgress.Services;

import Application.GymProgress.Entities.Exercise;
import Application.GymProgress.Entities.Register;
import Application.GymProgress.Entities.User;
import Application.GymProgress.Enum.Level;
import Application.GymProgress.Repositories.RegisterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final RegisterRepository registerRepository;
    private final UserService userService;
    private final ExerciseService exerciseService;

    // ----------------------------
    // Crear nuevo registro
    // ----------------------------
    public Register createRegister(Long userId, Long exerciseId, double weightUsed,
                                   int plannedReps, int realReps, int difficultyPerc) {

        User user = userService.getUserById(userId);
        Exercise exercise = exerciseService.getExerciseById(exerciseId);

        boolean easyComplete = realReps >= plannedReps;

        // Ajuste de carga automático
        double suggestedWeight = adjustWeight(weightUsed, easyComplete, user.getLevel());

        // Crear registro
        Register register = new Register();
        register.setUser(user);
        register.setExercise(exercise);
        register.setDate(LocalDate.now());
        register.setWeightUsed(suggestedWeight);
        register.setPlanedRepetitions(plannedReps);
        register.setRealRepetitions(realReps);
        if(difficultyPerc<1 || difficultyPerc>10){
            throw new RuntimeException("La dificultad percibida debe estar dentro del rango del 1 al 10");
        }
        register.setDifficultPerceived(difficultyPerc);
        register.setEasyComplete(easyComplete);

        // Guardar registro
        Register savedRegister = registerRepository.save(register);

        // Actualizar progreso del usuario
        userService.updateProgress(userId, user.getActualWeight(), 1); // +1 semana entrenada

        return savedRegister;
    }

    // ----------------------------
    // Ajuste de peso según nivel
    // ----------------------------
    private double adjustWeight(double previousWeight, boolean easyComplete, Level level) {
        double factor = switch (level) {
            case PRINCIPIANTE -> easyComplete ? 1.02 : 1.0;
            case INTERMEDIO   -> easyComplete ? 1.03 : 0.98;
            case AVANZADO     -> easyComplete ? 1.05 : 0.97;
        };
        return previousWeight * factor;
    }

    // ----------------------------
    // Listar todos los registros de un usuario
    // ----------------------------
    public List<Register> getRegistersByUser(Long userId) {
        return registerRepository.findByUserId(userId);
    }

    // ----------------------------
    // Obtener registro por ID
    // ----------------------------
    public Register getRegisterById(Long id) {
        return registerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));
    }
}

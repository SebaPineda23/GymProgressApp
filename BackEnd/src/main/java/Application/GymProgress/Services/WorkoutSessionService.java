package Application.GymProgress.Services;


import Application.GymProgress.DTOs.SetRecordRequestDTO;
import Application.GymProgress.DTOs.WorkoutSessionRequestDTO;
import Application.GymProgress.DTOs.WorkoutSessionResponseDTO;
import Application.GymProgress.Entities.*;
import Application.GymProgress.Repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutSessionService {

    private final WorkoutSessionRepository workoutSessionRepository;
    private final UserRepository userRepository;
    private final RoutineRepository routineRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExerciseExecutionRepository exerciseExecutionRepository;
    private final SetRecordRepository setRecordRepository;

    @Transactional
    public WorkoutSessionResponseDTO createWorkoutSession(WorkoutSessionRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Routine routine = routineRepository.findById(request.getRoutineId())
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        WorkoutSession session = WorkoutSession.builder()
                .date(request.getDate())
                .notes(request.getNotes())
                .completed(false)
                .user(user)
                .routine(routine)
                .build();

        // Crear ExerciseExecutions para cada ejercicio de la rutina
        List<ExerciseExecution> executions = routine.getExerciseList().stream()
                .map(exercise -> ExerciseExecution.builder()
                        .workoutSession(session)
                        .exercise(exercise)
                        .plannedSets(3) // Valor por defecto, puedes personalizar
                        .plannedReps(10) // Valor por defecto, puedes personalizar
                        .build())
                .collect(Collectors.toList());

        session.setExerciseExecutions(executions);
        WorkoutSession saved = workoutSessionRepository.save(session);

        return mapToResponseDTO(saved);
    }

    @Transactional
    public WorkoutSessionResponseDTO addSetRecord(SetRecordRequestDTO request) {
        WorkoutSession session = workoutSessionRepository.findById(request.getWorkoutSessionId())
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        Exercise exercise = exerciseRepository.findById(request.getExerciseId())
                .orElseThrow(() -> new RuntimeException("Ejercicio no encontrado"));

        // Buscar o crear ExerciseExecution
        ExerciseExecution execution = session.getExerciseExecutions().stream()
                .filter(exe -> exe.getExercise().getId().equals(exercise.getId()))
                .findFirst()
                .orElseGet(() -> {
                    ExerciseExecution newExe = ExerciseExecution.builder()
                            .workoutSession(session)
                            .exercise(exercise)
                            .plannedSets(3)
                            .plannedReps(10)
                            .build();
                    session.getExerciseExecutions().add(newExe);
                    return newExe;
                });

        // Crear SetRecord
        SetRecord setRecord = SetRecord.builder()
                .setNumber(request.getSetNumber())
                .weightUsed(request.getWeightUsed())
                .realRepetitions(request.getRealRepetitions())
                .difficultyPerceived(request.getDifficultyPerceived())
                .easyComplete(request.getEasyComplete())
                .exerciseExecution(execution)
                .build();

        execution.getSetRecords().add(setRecord);
        WorkoutSession updated = workoutSessionRepository.save(session);

        return mapToResponseDTO(updated);
    }

    public WorkoutSessionResponseDTO getWorkoutSession(Long id) {
        WorkoutSession session = workoutSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));
        return mapToResponseDTO(session);
    }

    public List<WorkoutSessionResponseDTO> getUserWorkoutSessions(Long userId) {
        return workoutSessionRepository.findByUserId(userId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public WorkoutSessionResponseDTO completeWorkoutSession(Long id) {
        WorkoutSession session = workoutSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));
        session.setCompleted(true);
        WorkoutSession updated = workoutSessionRepository.save(session);
        return mapToResponseDTO(updated);
    }

    public void deleteWorkoutSession(Long id) {
        workoutSessionRepository.deleteById(id);
    }

    private WorkoutSessionResponseDTO mapToResponseDTO(WorkoutSession session) {
        WorkoutSessionResponseDTO dto = new WorkoutSessionResponseDTO();
        dto.setId(session.getId());
        dto.setDate(session.getDate());
        dto.setNotes(session.getNotes());
        dto.setCompleted(session.isCompleted());

        // Mapear rutina
        WorkoutSessionResponseDTO.RoutineDTO routineDTO = new WorkoutSessionResponseDTO.RoutineDTO();
        routineDTO.setId(session.getRoutine().getId());
        routineDTO.setName(session.getRoutine().getName());
        routineDTO.setObjective(session.getRoutine().getObjective());
        dto.setRoutine(routineDTO);

        // Mapear ejecuciones de ejercicios
        List<WorkoutSessionResponseDTO.ExerciseExecutionDTO> executionDTOs = session.getExerciseExecutions().stream()
                .map(this::mapToExerciseExecutionDTO)
                .collect(Collectors.toList());
        dto.setExerciseExecutions(executionDTOs);

        return dto;
    }

    private WorkoutSessionResponseDTO.ExerciseExecutionDTO mapToExerciseExecutionDTO(ExerciseExecution execution) {
        WorkoutSessionResponseDTO.ExerciseExecutionDTO dto = new WorkoutSessionResponseDTO.ExerciseExecutionDTO();
        dto.setId(execution.getId());
        dto.setPlannedSets(execution.getPlannedSets());
        dto.setPlannedReps(execution.getPlannedReps());

        // Mapear ejercicio
        WorkoutSessionResponseDTO.ExerciseDTO exerciseDTO = new WorkoutSessionResponseDTO.ExerciseDTO();
        exerciseDTO.setId(execution.getExercise().getId());
        exerciseDTO.setName(execution.getExercise().getName());
        exerciseDTO.setDescription(execution.getExercise().getDescription());
        exerciseDTO.setMuscleGroupSet(execution.getExercise().getMuscleGroupSet().stream()
                .map(Enum::name)
                .collect(Collectors.toList()));
        dto.setExercise(exerciseDTO);

        // Mapear sets
        List<WorkoutSessionResponseDTO.SetRecordDTO> setDTOs = execution.getSetRecords().stream()
                .map(this::mapToSetRecordDTO)
                .collect(Collectors.toList());
        dto.setSetRecords(setDTOs);

        return dto;
    }

    private WorkoutSessionResponseDTO.SetRecordDTO mapToSetRecordDTO(SetRecord setRecord) {
        WorkoutSessionResponseDTO.SetRecordDTO dto = new WorkoutSessionResponseDTO.SetRecordDTO();
        dto.setId(setRecord.getId());
        dto.setSetNumber(setRecord.getSetNumber());
        dto.setWeightUsed(setRecord.getWeightUsed());
        dto.setRealRepetitions(setRecord.getRealRepetitions());
        dto.setDifficultyPerceived(setRecord.getDifficultyPerceived());
        dto.setEasyComplete(setRecord.getEasyComplete());
        return dto;
    }
}

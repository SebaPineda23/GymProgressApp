package Application.GymProgress.Services;

import Application.GymProgress.DTOs.SetRecordRequestDTO;
import Application.GymProgress.DTOs.WorkoutSessionRequestDTO;
import Application.GymProgress.DTOs.WorkoutSessionResponseDTO;
import Application.GymProgress.Entities.*;
import Application.GymProgress.Repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutSessionService {

    private final WorkoutSessionRepository workoutSessionRepository;
    private final UserRepository userRepository;
    private final RoutineRepository routineRepository;
    private final ExerciseRepository exerciseRepository;
    private final SetRecordRepository setRecordRepository;

    @Transactional
    public WorkoutSessionResponseDTO createWorkoutSession(WorkoutSessionRequestDTO request, LocalDate fechaEntrenamiento) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Routine routine = routineRepository.findById(request.getRoutineId())
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        WorkoutSession session = WorkoutSession.builder()
                .date(fechaEntrenamiento)
                .notes(request.getNotes())
                .completed(false)
                .user(user)
                .routine(routine)
                .setRecords(Collections.emptyList())
                .build();

        WorkoutSession saved = workoutSessionRepository.save(session);
        return mapToResponseDTO(saved);
    }

    @Transactional
    public WorkoutSessionResponseDTO addSetRecord(SetRecordRequestDTO request) {
        WorkoutSession session = workoutSessionRepository.findById(request.getWorkoutSessionId())
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        Exercise exercise = exerciseRepository.findById(request.getExerciseId())
                .orElseThrow(() -> new RuntimeException("Ejercicio no encontrado"));

        SetRecord setRecord = SetRecord.builder()
                .setNumber(request.getSetNumber())
                .weightUsed(request.getWeightUsed())
                .realRepetitions(request.getRealRepetitions())
                .difficultyPerceived(request.getDifficultyPerceived())
                .easyComplete(request.getEasyComplete())
                .exercise(exercise)
                .workoutSession(session)
                .build();

        setRecordRepository.save(setRecord);
        if (session.getSetRecords() == null) {
            session.setSetRecords(List.of(setRecord));
        } else {
            session.getSetRecords().add(setRecord);
        }

        // Actualizamos la sesión en respuesta
        return mapToResponseDTO(session);
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

        // Mapeo de rutina
        WorkoutSessionResponseDTO.RoutineDTO routineDTO = new WorkoutSessionResponseDTO.RoutineDTO();
        routineDTO.setId(session.getRoutine().getId());
        routineDTO.setName(session.getRoutine().getName());
        routineDTO.setObjective(session.getRoutine().getObjective());
        dto.setRoutine(routineDTO);

        // Mapeo de sets (ahora directos)
        List<WorkoutSessionResponseDTO.SetRecordDTO> setDTOs =
                (session.getSetRecords() == null ? Collections.<SetRecord>emptyList() : session.getSetRecords())
                        .stream()
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

        // Mapeamos el ejercicio directamente
        WorkoutSessionResponseDTO.ExerciseDTO exerciseDTO = new WorkoutSessionResponseDTO.ExerciseDTO();
        exerciseDTO.setId(setRecord.getExercise().getId());
        exerciseDTO.setName(setRecord.getExercise().getName());
        exerciseDTO.setDescription(setRecord.getExercise().getDescription());
        exerciseDTO.setMuscleGroupSet(setRecord.getExercise().getMuscleGroupSet()
                .stream().map(Enum::name).collect(Collectors.toList()));
        dto.setExercise(exerciseDTO);

        return dto;
    }
}

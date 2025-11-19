/*package Application.GymProgress.Services;

import Application.GymProgress.Entities.*;
import Application.GymProgress.Enum.Level;
import Application.GymProgress.Enum.MuscleGroup;
import Application.GymProgress.Enum.Role;
import Application.GymProgress.Repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DataLoaderService {

    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final RoutineRepository routineRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final ExerciseExecutionRepository exerciseExecutionRepository;
    private final SetRecordRepository setRecordRepository;

    @Transactional
    public void cargarDatosPrueba() {
        System.out.println("📦 Cargando 2 MESES COMPLETOS de datos de prueba...");

        try {
            // Limpiar datos existentes
            limpiarDatosPorLotes();

            // Crear datos
            User usuario = crearUsuario();
            List<Exercise> ejercicios = crearEjerciciosCompletos();
            List<Routine> rutinas = crearRutinasCompletas(usuario, ejercicios);
            crearDosMesesDeEntrenamiento(usuario, rutinas, ejercicios);

            System.out.println("✅ 2 meses de datos cargados exitosamente");

        } catch (Exception e) {
            System.out.println("❌ Error cargando datos: " + e.getMessage());
            throw e;
        }
    }

    private void limpiarDatosPorLotes() {
        System.out.println("🧹 Limpiando datos anteriores...");
        setRecordRepository.deleteAllInBatch();
        exerciseExecutionRepository.deleteAllInBatch();
        workoutSessionRepository.deleteAllInBatch();
        routineRepository.deleteAllInBatch();
        exerciseRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    private User crearUsuario() {
        return userRepository.save(User.builder()
                .userName("Test User")
                .email("test@test.com")
                .password("test")
                .active(true)
                .initialWeight(82.0)  // Peso inicial más alto para ver progreso
                .actualWeight(77.5)   // Peso actual después de 2 meses
                .weeksTrained(8)      // 8 semanas = 2 meses
                .level(Level.INTERMEDIO)
                .roleSet(Set.of(Role.USER))
                .build());
    }

    private List<Exercise> crearEjerciciosCompletos() {
        return exerciseRepository.saveAll(List.of(
                // EJERCICIOS DE PECHO/HOMBROS
                Exercise.builder().name("Press Banca").muscleGroupSet(Set.of(MuscleGroup.PECTORAL, MuscleGroup.TRICEPS)).level(Level.PRINCIPIANTE).build(),
                Exercise.builder().name("Press Inclinado").muscleGroupSet(Set.of(MuscleGroup.PECTORAL, MuscleGroup.TRICEPS)).level(Level.PRINCIPIANTE).build(),
                Exercise.builder().name("Fondos en Paralelas").muscleGroupSet(Set.of(MuscleGroup.PECTORAL, MuscleGroup.TRICEPS)).level(Level.INTERMEDIO).build(),

                // EJERCICIOS DE ESPALDA
                Exercise.builder().name("Dominadas").muscleGroupSet(Set.of(MuscleGroup.ESPALDA, MuscleGroup.BICEPS)).level(Level.INTERMEDIO).build(),
                Exercise.builder().name("Remo con Barra").muscleGroupSet(Set.of(MuscleGroup.ESPALDA, MuscleGroup.BICEPS)).level(Level.PRINCIPIANTE).build(),
                Exercise.builder().name("Jalón al Pecho").muscleGroupSet(Set.of(MuscleGroup.ESPALDA, MuscleGroup.BICEPS)).level(Level.PRINCIPIANTE).build(),

                // EJERCICIOS DE PIERNAS
                Exercise.builder().name("Sentadillas").muscleGroupSet(Set.of(MuscleGroup.CUADRICEPS, MuscleGroup.GLUTEOS)).level(Level.PRINCIPIANTE).build(),
                Exercise.builder().name("Peso Muerto").muscleGroupSet(Set.of(MuscleGroup.ESPALDA, MuscleGroup.GLUTEOS, MuscleGroup.ISQUIOTIBIALES)).level(Level.INTERMEDIO).build(),
                Exercise.builder().name("Prensa de Piernas").muscleGroupSet(Set.of(MuscleGroup.CUADRICEPS, MuscleGroup.GLUTEOS)).level(Level.PRINCIPIANTE).build(),
                Exercise.builder().name("Extensiones de Cuádriceps").muscleGroupSet(Set.of(MuscleGroup.CUADRICEPS)).level(Level.PRINCIPIANTE).build(),

                // EJERCICIOS DE BRAZOS
                Exercise.builder().name("Curl de Bíceps").muscleGroupSet(Set.of(MuscleGroup.BICEPS)).level(Level.PRINCIPIANTE).build(),
                Exercise.builder().name("Extensión de Tríceps").muscleGroupSet(Set.of(MuscleGroup.TRICEPS)).level(Level.PRINCIPIANTE).build(),

                // EJERCICIOS DE ABDOMINALES
                Exercise.builder().name("Crunch Abdominal").muscleGroupSet(Set.of(MuscleGroup.ABDOMINALES)).level(Level.PRINCIPIANTE).build(),
                Exercise.builder().name("Plancha").muscleGroupSet(Set.of(MuscleGroup.CORE, MuscleGroup.OBLICUOS)).level(Level.PRINCIPIANTE).build()
        ));
    }

    private List<Routine> crearRutinasCompletas(User usuario, List<Exercise> ejercicios) {
        return routineRepository.saveAll(List.of(
                // RUTINA A: PECHO/ESPALDA
                Routine.builder()
                        .name("Rutina A: Pecho y Espalda")
                        .objective("Fuerza superior")
                        .user(usuario)
                        .exerciseList(List.of(
                                ejercicios.get(0),  // Press Banca
                                ejercicios.get(1),  // Press Inclinado
                                ejercicios.get(3),  // Dominadas
                                ejercicios.get(4),  // Remo con Barra
                                ejercicios.get(10), // Curl de Bíceps
                                ejercicios.get(12)  // Crunch Abdominal
                        ))
                        .build(),

                // RUTINA B: PIERNAS/HOMBROS
                Routine.builder()
                        .name("Rutina B: Piernas y Hombros")
                        .objective("Fuerza inferior")
                        .user(usuario)
                        .exerciseList(List.of(
                                ejercicios.get(6),  // Sentadillas
                                ejercicios.get(7),  // Peso Muerto
                                ejercicios.get(8),  // Prensa de Piernas
                                ejercicios.get(0),  // Press Banca (también trabaja hombros)
                                ejercicios.get(11), // Extensión de Tríceps
                                ejercicios.get(13)  // Plancha
                        ))
                        .build(),

                // RUTINA C: FULL BODY
                Routine.builder()
                        .name("Rutina C: Full Body")
                        .objective("Entrenamiento completo")
                        .user(usuario)
                        .exerciseList(List.of(
                                ejercicios.get(0),  // Press Banca
                                ejercicios.get(3),  // Dominadas
                                ejercicios.get(6),  // Sentadillas
                                ejercicios.get(10), // Curl de Bíceps
                                ejercicios.get(11), // Extensión de Tríceps
                                ejercicios.get(12)  // Crunch Abdominal
                        ))
                        .build()
        ));
    }

    private void crearDosMesesDeEntrenamiento(User usuario, List<Routine> rutinas, List<Exercise> ejercicios) {
        System.out.println("🏋️ Creando 2 meses de entrenamiento...");

        LocalDate fechaInicio = LocalDate.now().minusMonths(2);
        int totalSemanas = 8; // 2 meses

        // Patrón de entrenamiento: A, B, Descanso, C, A, B, Descanso
        String[] patronRutinas = {"A", "B", "DESCANSO", "C", "A", "B", "DESCANSO"};

        int diaEntrenamiento = 0;

        for (int semana = 0; semana < totalSemanas; semana++) {
            System.out.println("📅 Creando semana " + (semana + 1) + "...");

            for (int dia = 0; dia < 7; dia++) { // 7 días por semana
                LocalDate fecha = fechaInicio.plusWeeks(semana).plusDays(dia);
                String tipoRutina = patronRutinas[diaEntrenamiento % patronRutinas.length];

                if (!"DESCANSO".equals(tipoRutina)) {
                    crearSesionEntrenamiento(usuario, rutinas, tipoRutina, fecha, semana);
                }

                diaEntrenamiento++;
            }
        }

        System.out.println("✅ " + (totalSemanas * 4) + " sesiones de entrenamiento creadas");
    }

    private void crearSesionEntrenamiento(User usuario, List<Routine> rutinas, String tipoRutina, LocalDate fecha, int semana) {
        Routine rutina = switch (tipoRutina) {
            case "A" -> rutinas.get(0); // Pecho/Espalda
            case "B" -> rutinas.get(1); // Piernas/Hombros
            case "C" -> rutinas.get(2); // Full Body
            default -> rutinas.get(0);
        };

        WorkoutSession sesion = workoutSessionRepository.save(WorkoutSession.builder()
                .date(fecha)
                .completed(true)
                .user(usuario)
                .routine(rutina)
                .build());

        // Crear ejecuciones para cada ejercicio de la rutina
        for (Exercise ejercicio : rutina.getExerciseList()) {
            ExerciseExecution ejecucion = exerciseExecutionRepository.save(ExerciseExecution.builder()
                    .workoutSession(sesion)
                    .exercise(ejercicio)
                    .plannedSets(4)
                    .plannedReps(8)
                    .build());

            // Crear 4 sets por ejercicio con progresión semanal
            crearSetsConProgresion(ejecucion, ejercicio, semana);
        }
    }

    private void crearSetsConProgresion(ExerciseExecution ejecucion, Exercise ejercicio, int semana) {
        List<SetRecord> sets = new ArrayList<>();
        double pesoBase = obtenerPesoBase(ejercicio.getName());

        // Progresión: +2.5% de peso cada semana
        double factorProgresion = 1.0 + (semana * 0.025);

        for (int setNum = 1; setNum <= 4; setNum++) {
            double peso = pesoBase * factorProgresion;
            int reps = calcularRepeticiones(setNum, semana);
            int dificultad = calcularDificultad(setNum, semana);
            boolean facil = (setNum == 1 && reps >= 8);

            SetRecord set = SetRecord.builder()
                    .setNumber(setNum)
                    .weightUsed(Math.round(peso * 10.0) / 10.0) // Redondear a 1 decimal
                    .realRepetitions(reps)
                    .difficultyPerceived(dificultad)
                    .easyComplete(facil)
                    .exerciseExecution(ejecucion)
                    .build();

            sets.add(set);
        }

        setRecordRepository.saveAll(sets);
    }

    private double obtenerPesoBase(String nombreEjercicio) {
        return switch (nombreEjercicio) {
            case "Press Banca", "Sentadillas" -> 60.0;
            case "Press Inclinado", "Remo con Barra" -> 40.0;
            case "Peso Muerto" -> 80.0;
            case "Prensa de Piernas" -> 100.0;
            case "Dominadas" -> 0.0; // Peso corporal
            case "Fondos en Paralelas" -> 0.0; // Peso corporal
            case "Jalón al Pecho" -> 35.0;
            case "Extensiones de Cuádriceps" -> 25.0;
            case "Curl de Bíceps" -> 15.0;
            case "Extensión de Tríceps" -> 20.0;
            case "Crunch Abdominal", "Plancha" -> 0.0; // Peso corporal
            default -> 30.0;
        };
    }

    private int calcularRepeticiones(int setNum, int semana) {
        // Patrón de repeticiones: primer set más reps, últimos sets menos reps
        // Mejora con el tiempo
        int repsBase = switch (setNum) {
            case 1 -> 12;
            case 2 -> 10;
            case 3 -> 8;
            case 4 -> 6;
            default -> 8;
        };

        // Mejora de +1 rep cada 2 semanas
        return repsBase + (semana / 2);
    }

    private int calcularDificultad(int setNum, int semana) {
        // Dificultad aumenta con los sets y mejora con el tiempo
        int dificultadBase = setNum + 2; // Set 1: 3, Set 2: 4, etc.
        return Math.min(dificultadBase + (semana / 4), 10); // Máximo 10
    }

    public Long obtenerUsuarioPruebaId() {
        return userRepository.findByEmail("test@test.com")
                .map(User::getId)
                .orElse(1L);
    }
}
 */
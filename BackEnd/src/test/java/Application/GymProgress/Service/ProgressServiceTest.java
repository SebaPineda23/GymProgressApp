// src/test/java/Application/GymProgress/Service/ProgressServiceTest.java
package Application.GymProgress.Service;

import Application.GymProgress.DTOs.ProgresoMensualDTO;
import Application.GymProgress.DTOs.ProgresoSemanalDTO;
import Application.GymProgress.Entities.*;
import Application.GymProgress.Enum.Level;
import Application.GymProgress.Enum.MuscleGroup;
import Application.GymProgress.Repositories.UserRepository;
import Application.GymProgress.Repositories.WorkoutSessionRepository;
import Application.GymProgress.Services.ProgressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgressServiceTest {

    @Mock
    private WorkoutSessionRepository workoutSessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProgressService progresoService;

    private User usuario;
    private WorkoutSession sesionProgresoPositivo;
    private WorkoutSession sesionProgresoNegativo;
    private WorkoutSession sesionProgresoEstable;

    @BeforeEach
    void setUp() {
        usuario = User.builder()
                .id(1L)
                .userName("Test")
                .actualWeight(75.0)
                .initialWeight(80.0)
                .build();

        // Configurar mocks comunes
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(usuario));
    }

    @Test
    void testProgresoSemanal_Positivo() {
        // Given - Sesión con buen progreso
        sesionProgresoPositivo = crearSesionConProgresoPositivo();
        when(workoutSessionRepository.findByUserId(anyLong())).thenReturn(List.of(sesionProgresoPositivo));

        // When
        ProgresoSemanalDTO resultado = progresoService.obtenerProgresoSemanal(1L, LocalDate.now());

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.getTotalPesoLevantado() > 0);
        assertTrue(resultado.getTotalRepeticiones() > 0);
        assertEquals(1, resultado.getEntrenamientosCompletados());
        System.out.println("✅ Progreso Semanal Positivo:");
        System.out.println("   - Peso levantado: " + resultado.getTotalPesoLevantado());
        System.out.println("   - Repeticiones: " + resultado.getTotalRepeticiones());
    }

    @Test
    void testProgresoSemanal_Negativo() {
        // Given - Sesión con poco progreso
        sesionProgresoNegativo = crearSesionConProgresoNegativo();
        when(workoutSessionRepository.findByUserId(anyLong())).thenReturn(List.of(sesionProgresoNegativo));

        // When
        ProgresoSemanalDTO resultado = progresoService.obtenerProgresoSemanal(1L, LocalDate.now());

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.getTotalPesoLevantado() < 100); // Poco peso levantado
        System.out.println("❌ Progreso Semanal Negativo:");
        System.out.println("   - Peso levantado: " + resultado.getTotalPesoLevantado());
        System.out.println("   - Volumen bajo");
    }

    @Test
    void testProgresoMensual_TendenciaPositiva() {
        // Given - Múltiples sesiones mostrando mejora
        List<WorkoutSession> sesiones = crearSesionesConTendenciaPositiva();
        when(workoutSessionRepository.findByUserId(anyLong())).thenReturn(sesiones);

        // When
        ProgresoMensualDTO resultado = progresoService.obtenerProgresoMensual(1L, YearMonth.now());

        // Then
        assertNotNull(resultado);
        assertEquals("MEJORANDO", resultado.getTendencia());
        assertTrue(resultado.getTotalPesoLevantado() > 0);
        System.out.println("📈 Progreso Mensual - Tendencia POSITIVA:");
        System.out.println("   - Tendencia: " + resultado.getTendencia());
        System.out.println("   - Total peso: " + resultado.getTotalPesoLevantado());
    }

    @Test
    void testProgresoMensual_TendenciaNegativa() {
        // Given - Sesiones mostrando retroceso
        List<WorkoutSession> sesiones = crearSesionesConTendenciaNegativa();
        when(workoutSessionRepository.findByUserId(anyLong())).thenReturn(sesiones);

        // When
        ProgresoMensualDTO resultado = progresoService.obtenerProgresoMensual(1L, YearMonth.now());

        // Then
        assertNotNull(resultado);
        assertEquals("BAJANDO", resultado.getTendencia());
        System.out.println("📉 Progreso Mensual - Tendencia NEGATIVA:");
        System.out.println("   - Tendencia: " + resultado.getTendencia());
    }

    @Test
    void testProgresoMensual_TendenciaEstable() {
        // Given - Sesiones estables
        List<WorkoutSession> sesiones = crearSesionesConTendenciaEstable();
        when(workoutSessionRepository.findByUserId(anyLong())).thenReturn(sesiones);

        // When
        ProgresoMensualDTO resultado = progresoService.obtenerProgresoMensual(1L, YearMonth.now());

        // Then
        assertNotNull(resultado);
        assertEquals("ESTABLE", resultado.getTendencia());
        System.out.println("➡️ Progreso Mensual - Tendencia ESTABLE:");
        System.out.println("   - Tendencia: " + resultado.getTendencia());
    }

    // Métodos auxiliares para crear datos de prueba
    private WorkoutSession crearSesionConProgresoPositivo() {
        WorkoutSession sesion = new WorkoutSession();
        sesion.setId(1L);
        sesion.setDate(LocalDate.now());
        sesion.setCompleted(true);

        Exercise ejercicio = crearEjercicio("Press Banca");
        ExerciseExecution ejecucion = crearExerciseExecution(sesion, ejercicio);

        // Sets con buen progreso (alto peso y repeticiones)
        ejecucion.setSetRecords(List.of(
                crearSetRecord(1, 80.0, 10, 3, true),
                crearSetRecord(2, 80.0, 8, 4, false),
                crearSetRecord(3, 75.0, 12, 2, true)
        ));

        sesion.setExerciseExecutions(List.of(ejecucion));
        return sesion;
    }

    private WorkoutSession crearSesionConProgresoNegativo() {
        WorkoutSession sesion = new WorkoutSession();
        sesion.setId(2L);
        sesion.setDate(LocalDate.now());
        sesion.setCompleted(true);

        Exercise ejercicio = crearEjercicio("Press Banca");
        ExerciseExecution ejecucion = crearExerciseExecution(sesion, ejercicio);

        // Sets con poco progreso (bajo peso y repeticiones)
        ejecucion.setSetRecords(List.of(
                crearSetRecord(1, 40.0, 5, 7, false),
                crearSetRecord(2, 35.0, 4, 8, false),
                crearSetRecord(3, 30.0, 3, 9, false)
        ));

        sesion.setExerciseExecutions(List.of(ejecucion));
        return sesion;
    }

    private List<WorkoutSession> crearSesionesConTendenciaPositiva() {
        return List.of(
                crearSesionConVolumen(1000.0, LocalDate.now().minusWeeks(1)),
                crearSesionConVolumen(1200.0, LocalDate.now().minusWeeks(2)),
                crearSesionConVolumen(1500.0, LocalDate.now().minusWeeks(3))
        );
    }

    private List<WorkoutSession> crearSesionesConTendenciaNegativa() {
        return List.of(
                crearSesionConVolumen(800.0, LocalDate.now().minusWeeks(1)),
                crearSesionConVolumen(1000.0, LocalDate.now().minusWeeks(2)),
                crearSesionConVolumen(1200.0, LocalDate.now().minusWeeks(3))
        );
    }

    private List<WorkoutSession> crearSesionesConTendenciaEstable() {
        return List.of(
                crearSesionConVolumen(1000.0, LocalDate.now().minusWeeks(1)),
                crearSesionConVolumen(1050.0, LocalDate.now().minusWeeks(2)),
                crearSesionConVolumen(950.0, LocalDate.now().minusWeeks(3))
        );
    }

    private WorkoutSession crearSesionConVolumen(double volumen, LocalDate fecha) {
        WorkoutSession sesion = new WorkoutSession();
        sesion.setId(1L);
        sesion.setDate(fecha);
        sesion.setCompleted(true);

        Exercise ejercicio = crearEjercicio("Test Exercise");
        ExerciseExecution ejecucion = crearExerciseExecution(sesion, ejercicio);

        // Calcular sets para alcanzar el volumen deseado
        double pesoPorSet = volumen / 30; // 30 repeticiones totales
        ejecucion.setSetRecords(List.of(
                crearSetRecord(1, pesoPorSet, 10, 3, true),
                crearSetRecord(2, pesoPorSet, 10, 4, false),
                crearSetRecord(3, pesoPorSet, 10, 5, false)
        ));

        sesion.setExerciseExecutions(List.of(ejecucion));
        return sesion;
    }

    private Exercise crearEjercicio(String nombre) {
        return Exercise.builder()
                .id(1L)
                .name(nombre)
                .muscleGroupSet(Set.of(MuscleGroup.PECTORAL))
                .level(Level.PRINCIPIANTE)
                .build();
    }

    private ExerciseExecution crearExerciseExecution(WorkoutSession sesion, Exercise ejercicio) {
        return ExerciseExecution.builder()
                .id(1L)
                .workoutSession(sesion)
                .exercise(ejercicio)
                .plannedSets(3)
                .plannedReps(10)
                .build();
    }

    private SetRecord crearSetRecord(int setNumber, double weight, int reps, int difficulty, boolean easyComplete) {
        return SetRecord.builder()
                .id((long) setNumber)
                .setNumber(setNumber)
                .weightUsed(weight)
                .realRepetitions(reps)
                .difficultyPerceived(difficulty)
                .easyComplete(easyComplete)
                .build();
    }
}
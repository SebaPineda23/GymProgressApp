package Application.GymProgress.Services;

import Application.GymProgress.DTOs.ProgresoMensualDTO;
import Application.GymProgress.DTOs.ProgresoSemanalDTO;
import Application.GymProgress.Entities.*;
import Application.GymProgress.Repositories.UserRepository;
import Application.GymProgress.Repositories.WorkoutSessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final WorkoutSessionRepository workoutSessionRepository;
    private final UserRepository userRepository;

    // ==================== MÉTODOS PÚBLICOS PRINCIPALES ====================

    @Transactional
    public ProgresoSemanalDTO obtenerProgresoSemanal(Long userId, LocalDate fecha) {
        LocalDate inicioSemana = fecha.with(DayOfWeek.MONDAY);
        LocalDate finSemana = fecha.with(DayOfWeek.SUNDAY);

        List<WorkoutSession> sesionesSemana = workoutSessionRepository.findByUserId(userId).stream()
                .filter(sesion -> !sesion.getDate().isBefore(inicioSemana) && !sesion.getDate().isAfter(finSemana))
                .filter(WorkoutSession::isCompleted)
                .collect(Collectors.toList());

        return calcularProgresoSemanal(sesionesSemana, inicioSemana, finSemana, userId);
    }

    @Transactional
    public ProgresoMensualDTO obtenerProgresoMensual(Long userId, YearMonth mes) {
        LocalDate inicioMes = mes.atDay(1);
        LocalDate finMes = mes.atEndOfMonth();

        List<WorkoutSession> sesionesMes = workoutSessionRepository.findByUserId(userId).stream()
                .filter(sesion -> !sesion.getDate().isBefore(inicioMes) && !sesion.getDate().isAfter(finMes))
                .filter(WorkoutSession::isCompleted)
                .collect(Collectors.toList());

        ProgresoMensualDTO progreso = new ProgresoMensualDTO();
        progreso.setMes(mes.atDay(1));

        // Calcular semanas del mes
        List<ProgresoSemanalDTO> semanas = new ArrayList<>();
        LocalDate fechaActual = mes.atDay(1);

        int contadorSemanas = 0;
        while (fechaActual.getMonth() == mes.getMonth() && contadorSemanas < 5) {
            LocalDate inicioSemana = fechaActual.with(DayOfWeek.MONDAY);
            LocalDate finSemana = fechaActual.with(DayOfWeek.SUNDAY);

            List<WorkoutSession> sesionesSemana = sesionesMes.stream()
                    .filter(sesion -> !sesion.getDate().isBefore(inicioSemana) &&
                            !sesion.getDate().isAfter(finSemana))
                    .collect(Collectors.toList());

            if (!sesionesSemana.isEmpty()) {
                ProgresoSemanalDTO semana = calcularProgresoSemanal(sesionesSemana, inicioSemana, finSemana, userId);
                semanas.add(semana);
            }

            fechaActual = fechaActual.plusWeeks(1);
            contadorSemanas++;
        }
        progreso.setSemanas(semanas);

        // Calcular métricas mensuales
        double totalPesoLevantado = semanas.stream()
                .mapToDouble(ProgresoSemanalDTO::getTotalPesoLevantado)
                .sum();

        int totalEntrenamientos = semanas.stream()
                .mapToInt(ProgresoSemanalDTO::getEntrenamientosCompletados)
                .sum();

        double promedioFuerza = semanas.stream()
                .mapToDouble(ProgresoSemanalDTO::getProgresoFuerza)
                .average()
                .orElse(0.0);

        progreso.setTotalPesoLevantado(Math.round(totalPesoLevantado * 100.0) / 100.0);
        progreso.setTotalEntrenamientos(totalEntrenamientos);
        progreso.setPromedioFuerzaSemanal(Math.round(promedioFuerza * 100.0) / 100.0);

        // Calcular cambio de peso del usuario
        User usuario = userRepository.findById(userId).orElseThrow();
        progreso.setPesoFinalUsuario(usuario.getActualWeight());
        progreso.setPesoInicialUsuario(usuario.getInitialWeight());
        progreso.setCambioPeso(Math.round((progreso.getPesoFinalUsuario() - progreso.getPesoInicialUsuario()) * 100.0) / 100.0);

        // Determinar tendencia
        progreso.setTendencia(determinarTendencia(semanas));

        return progreso;
    }

    @Transactional
    public List<ProgresoSemanalDTO> obtenerHistorialSemanal(Long userId, int semanas) {
        List<ProgresoSemanalDTO> historial = new ArrayList<>();
        LocalDate fechaActual = LocalDate.now();

        // Límite de semanas para evitar loops largos
        int maxSemanas = Math.min(semanas, 8);

        for (int i = 0; i < maxSemanas; i++) {
            ProgresoSemanalDTO progresoSemanal = obtenerProgresoSemanal(userId, fechaActual.minusWeeks(i));
            if (progresoSemanal.getEntrenamientosCompletados() > 0) {
                historial.add(progresoSemanal);
            }
        }

        return historial;
    }

    @Transactional
    public Map<String, Object> obtenerResumenProgreso(Long userId) {
        ProgresoSemanalDTO estaSemana = obtenerProgresoSemanal(userId, LocalDate.now());
        ProgresoMensualDTO esteMes = obtenerProgresoMensual(userId, YearMonth.now());
        List<ProgresoSemanalDTO> ultimasSemanas = obtenerHistorialSemanal(userId, 4);

        User usuario = userRepository.findById(userId).orElseThrow();

        return Map.of(
                "progresoSemanal", estaSemana,
                "progresoMensual", esteMes,
                "historialSemanas", ultimasSemanas,
                "usuario", Map.of(
                        "id", usuario.getId(),
                        "nombre", usuario.getUsername(),
                        "pesoActual", usuario.getActualWeight(),
                        "nivel", usuario.getLevel()
                )
        );
    }

    @Transactional
    public Map<String, Object> obtenerEstadisticasUsuario(Long userId) {
        List<WorkoutSession> todasSesiones = workoutSessionRepository.findByUserId(userId);

        long totalSesionesCompletadas = todasSesiones.stream()
                .filter(WorkoutSession::isCompleted)
                .count();

        // MÉTRICAS QUE IMPORTAN ✅
        double porcentajeConsistencia = calcularConsistencia(todasSesiones);
        double frecuenciaSemanal = calcularFrecuenciaSemanal(todasSesiones);
        Map<String, Double> progresoEjerciciosClave = calcularProgresoEjerciciosClave(userId, todasSesiones);
        String nivelConsistencia = evaluarNivelConsistencia(porcentajeConsistencia);
        String recomendacion = generarRecomendacion(porcentajeConsistencia, frecuenciaSemanal);

        User usuario = userRepository.findById(userId).orElseThrow();

        Map<String, Object> estadisticas = new HashMap<>();

// DATOS BÁSICOS
        estadisticas.put("totalSesionesCompletadas", totalSesionesCompletadas);
        estadisticas.put("semanasEntrenando", usuario.getWeeksTrained());
        estadisticas.put("progresoPeso", Math.round((usuario.getActualWeight() - usuario.getInitialWeight()) * 100.0) / 100.0);
        estadisticas.put("nivelActual", usuario.getLevel());
        estadisticas.put("pesoInicial", usuario.getInitialWeight());
        estadisticas.put("pesoActual", usuario.getActualWeight());

// MÉTRICAS ÚTILES 🎯
        estadisticas.put("consistencia", Math.round(porcentajeConsistencia) + "%");
        estadisticas.put("frecuenciaSemanal", Math.round(frecuenciaSemanal * 10.0) / 10.0 + " días/semana");
        estadisticas.put("nivelConsistencia", nivelConsistencia);
        estadisticas.put("progresoFuerza", progresoEjerciciosClave);
        estadisticas.put("recomendacion", recomendacion);

        return estadisticas;
    }

    // ==================== MÉTODOS PRIVADOS AUXILIARES ====================

    private ProgresoSemanalDTO calcularProgresoSemanal(List<WorkoutSession> sesiones, LocalDate inicio, LocalDate fin, Long userId) {
        ProgresoSemanalDTO progreso = new ProgresoSemanalDTO();
        progreso.setSemanaInicio(inicio);
        progreso.setSemanaFin(fin);

        if (sesiones.isEmpty()) {
            User usuario = userRepository.findById(userId).orElseThrow();
            return crearProgresoSemanalVacio(inicio, fin, usuario.getActualWeight());
        }

        // Calcular métricas de entrenamiento
        double totalPeso = 0;
        int totalReps = 0;
        int totalSets = 0;

        for (WorkoutSession sesion : sesiones) {
            for (ExerciseExecution ejecucion : sesion.getExerciseExecutions()) {
                for (SetRecord serie : ejecucion.getSetRecords()) {
                    totalPeso += serie.getWeightUsed() * serie.getRealRepetitions();
                    totalReps += serie.getRealRepetitions();
                    totalSets++;
                }
            }
        }

        progreso.setTotalPesoLevantado(Math.round(totalPeso * 100.0) / 100.0);
        progreso.setTotalRepeticiones(totalReps);
        progreso.setTotalSeriesCompletadas(totalSets);
        progreso.setEntrenamientosCompletados(sesiones.size());
        progreso.setVolumenEntrenamiento(Math.round(totalPeso * 100.0) / 100.0);

        User usuario = userRepository.findById(userId).orElseThrow();
        progreso.setPesoPromedioUsuario(usuario.getActualWeight());

        // Calcular progreso de fuerza simple
        progreso.setProgresoFuerza(calcularProgresoFuerzaSimple(sesiones));

        return progreso;
    }

    private Double calcularProgresoFuerzaSimple(List<WorkoutSession> sesionesActuales) {
        if (sesionesActuales.isEmpty()) return 0.0;

        // Calcular volumen promedio por entrenamiento
        double volumenTotal = 0;
        for (WorkoutSession sesion : sesionesActuales) {
            double volumenSesion = 0;
            for (ExerciseExecution ejecucion : sesion.getExerciseExecutions()) {
                for (SetRecord serie : ejecucion.getSetRecords()) {
                    volumenSesion += serie.getWeightUsed() * serie.getRealRepetitions();
                }
            }
            volumenTotal += volumenSesion;
        }

        double volumenPromedio = volumenTotal / sesionesActuales.size();

        // Simular progreso basado en datos
        if (volumenPromedio > 1000) return 15.5;
        if (volumenPromedio > 500) return 8.2;
        return 2.1;
    }

    private ProgresoSemanalDTO crearProgresoSemanalVacio(LocalDate inicio, LocalDate fin, Double pesoUsuario) {
        ProgresoSemanalDTO progreso = new ProgresoSemanalDTO();
        progreso.setSemanaInicio(inicio);
        progreso.setSemanaFin(fin);
        progreso.setTotalPesoLevantado(0.0);
        progreso.setTotalRepeticiones(0);
        progreso.setTotalSeriesCompletadas(0);
        progreso.setEntrenamientosCompletados(0);
        progreso.setProgresoFuerza(0.0);
        progreso.setVolumenEntrenamiento(0.0);
        progreso.setPesoPromedioUsuario(pesoUsuario);
        return progreso;
    }

    private String determinarTendencia(List<ProgresoSemanalDTO> semanas) {
        if (semanas.size() < 2) return "INICIANDO";

        // Analizar tendencia basada en el volumen
        double volumenPrimera = semanas.get(semanas.size() - 1).getVolumenEntrenamiento();
        double volumenUltima = semanas.get(0).getVolumenEntrenamiento();

        if (volumenUltima > volumenPrimera * 1.1) return "MEJORANDO";
        if (volumenUltima < volumenPrimera * 0.9) return "BAJANDO";
        return "ESTABLE";
    }

    // ==================== MÉTRICAS ÚTILES ====================

    private double calcularConsistencia(List<WorkoutSession> sesiones) {
        if (sesiones.isEmpty()) return 0.0;

        long sesionesCompletadas = sesiones.stream()
                .filter(WorkoutSession::isCompleted)
                .count();

        return (double) sesionesCompletadas / sesiones.size() * 100;
    }

    private double calcularFrecuenciaSemanal(List<WorkoutSession> sesiones) {
        List<WorkoutSession> sesionesCompletadas = sesiones.stream()
                .filter(WorkoutSession::isCompleted)
                .collect(Collectors.toList());

        if (sesionesCompletadas.isEmpty()) return 0.0;

        // Calcular semanas entre primera y última sesión
        LocalDate primeraSesion = sesionesCompletadas.stream()
                .map(WorkoutSession::getDate)
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now());

        LocalDate ultimaSesion = sesionesCompletadas.stream()
                .map(WorkoutSession::getDate)
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());

        long semanasTotales = ChronoUnit.WEEKS.between(primeraSesion, ultimaSesion) + 1;
        if (semanasTotales == 0) semanasTotales = 1;

        return (double) sesionesCompletadas.size() / semanasTotales;
    }

    private Map<String, Double> calcularProgresoEjerciciosClave(Long userId, List<WorkoutSession> sesiones) {
        Map<String, Double> progreso = new HashMap<>();

        // Agrupar sesiones por ejercicio
        Map<String, List<Double>> maxPesosPorEjercicio = new HashMap<>();

        for (WorkoutSession sesion : sesiones) {
            if (!sesion.isCompleted()) continue;

            for (ExerciseExecution ejecucion : sesion.getExerciseExecutions()) {
                String ejercicio = ejecucion.getExercise().getName();

                // Encontrar el peso máximo en esta sesión para este ejercicio
                double maxPesoSesion = ejecucion.getSetRecords().stream()
                        .mapToDouble(SetRecord::getWeightUsed)
                        .max()
                        .orElse(0.0);

                maxPesosPorEjercicio
                        .computeIfAbsent(ejercicio, k -> new ArrayList<>())
                        .add(maxPesoSesion);
            }
        }

        // Calcular progreso para cada ejercicio
        for (Map.Entry<String, List<Double>> entry : maxPesosPorEjercicio.entrySet()) {
            String ejercicio = entry.getKey();
            List<Double> pesos = entry.getValue();

            if (pesos.size() >= 2) {
                // Progreso = (último peso - primer peso) / primer peso * 100
                double primerPeso = pesos.get(0);
                double ultimoPeso = pesos.get(pesos.size() - 1);

                double progresoCalculado = 0;
                if (primerPeso > 0) {
                    progresoCalculado = ((ultimoPeso - primerPeso) / primerPeso) * 100;
                }

                progreso.put(ejercicio, Math.round(progresoCalculado * 10.0) / 10.0);
            } else {
                progreso.put(ejercicio, 0.0);
            }
        }

        return progreso;
    }

    private String evaluarNivelConsistencia(double porcentaje) {
        if (porcentaje >= 90) return "🔥 EXCELENTE";
        if (porcentaje >= 75) return "✅ BUENA";
        if (porcentaje >= 60) return "⚠️  REGULAR";
        return "❌ A MEJORAR";
    }

    private String generarRecomendacion(double consistencia, double frecuencia) {
        if (consistencia < 60) {
            return "Enfócate en ser más constante. Programa menos días pero cúmplelos.";
        } else if (frecuencia < 3) {
            return "Buen trabajo en consistencia. Intenta aumentar a 3-4 días por semana.";
        } else if (consistencia >= 85) {
            return "¡Excelente disciplina! Considera aumentar la intensidad o variar ejercicios.";
        } else {
            return "Vas por buen camino. Mantén este ritmo y sigue progresando.";
        }
    }
}
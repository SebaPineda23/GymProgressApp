package Application.GymProgress.Services;

import Application.GymProgress.DTOs.*;
import Application.GymProgress.Entities.*;
import Application.GymProgress.Enum.Level;
import Application.GymProgress.Repositories.UserRepository;
import Application.GymProgress.Repositories.WeightRecordRepository;
import Application.GymProgress.Repositories.WorkoutSessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final WorkoutSessionRepository workoutSessionRepository;
    private final UserRepository userRepository;
    private final WeightRecordRepository weightRecordRepository;

    @Transactional
    public Map<String, Object> obtenerDashboardCompleto(Long userId, Optional<String> fechaParam) {
        LocalDate fechaReferencia;

        // Si no se proporciona fecha, usar la fecha actual
        if (fechaParam.isEmpty()) {
            fechaReferencia = LocalDate.now();
        } else {
            // Parsear la fecha proporcionada
            try {
                fechaReferencia = LocalDate.parse(fechaParam.get());
            } catch (Exception e) {
                throw new RuntimeException("Formato de fecha inválido. Use YYYY-MM-DD");
            }
        }

        YearMonth mesReferencia = YearMonth.from(fechaReferencia);

        // Obtener usuario
        User usuario = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Obtener todas las sesiones del usuario
        List<WorkoutSession> todasLasSesiones = workoutSessionRepository.findByUserId(userId).stream()
                .filter(WorkoutSession::isCompleted)
                .collect(Collectors.toList());

        // ============================================
        // 1️⃣ MÉTRICAS GENERALES (metricsData)
        // ============================================

        // Semana de referencia (basada en la fecha proporcionada o actual)
        LocalDate inicioSemanaReferencia = fechaReferencia.with(DayOfWeek.MONDAY);
        LocalDate finSemanaReferencia = fechaReferencia.with(DayOfWeek.SUNDAY);

        List<WorkoutSession> sesionesSemanaReferencia = todasLasSesiones.stream()
                .filter(s -> !s.getDate().isBefore(inicioSemanaReferencia) && !s.getDate().isAfter(finSemanaReferencia))
                .collect(Collectors.toList());

        // Semana anterior a la de referencia
        LocalDate inicioSemanaAnterior = inicioSemanaReferencia.minusWeeks(1);
        LocalDate finSemanaAnterior = finSemanaReferencia.minusWeeks(1);

        List<WorkoutSession> sesionesSemanaAnterior = todasLasSesiones.stream()
                .filter(s -> !s.getDate().isBefore(inicioSemanaAnterior) && !s.getDate().isAfter(finSemanaAnterior))
                .collect(Collectors.toList());

        int entrenamientosCompletados = sesionesSemanaReferencia.size();
        int entrenamientosSemanaAnterior = sesionesSemanaAnterior.size();

        double totalPesoSemanaReferencia = calcularPesoTotal(sesionesSemanaReferencia);
        double totalPesoSemanaAnterior = calcularPesoTotal(sesionesSemanaAnterior);

        int totalSeriesCompletadas = sesionesSemanaReferencia.stream()
                .mapToInt(s -> s.getSetRecords().size())
                .sum();

        double progresoFuerza = calcularProgresoFuerzaSimple(sesionesSemanaReferencia);

        String tendenciaPeso = totalPesoSemanaReferencia >= totalPesoSemanaAnterior ? "up" : "down";

        Map<String, Object> metricsData = Map.of(
                "entrenamientosCompletados", entrenamientosCompletados,
                "entrenamientosSemanaAnterior", entrenamientosSemanaAnterior,
                "totalPesoLevantado", Math.round(totalPesoSemanaReferencia * 100.0) / 100.0,
                "tendenciaPeso", tendenciaPeso,
                "totalSeriesCompletadas", totalSeriesCompletadas,
                "progresoFuerza", Math.round(progresoFuerza * 100.0) / 100.0
        );

        // ============================================
        // 2️⃣ PROGRESO SEMANAL (progresoSemanalData)
        // ============================================

        // Array de entrenamientos por día de la semana de referencia
        int[] entrenamientosPorDia = new int[7];
        for (WorkoutSession sesion : sesionesSemanaReferencia) {
            int diaSemana = sesion.getDate().getDayOfWeek().getValue() - 1; // 0=Lun, 6=Dom
            entrenamientosPorDia[diaSemana]++;
        }

        int totalRepeticiones = sesionesSemanaReferencia.stream()
                .flatMap(s -> s.getSetRecords().stream())
                .mapToInt(SetRecord::getRealRepetitions)
                .sum();

        double volumenEntrenamiento = totalPesoSemanaReferencia;

        Map<String, Object> progresoSemanalData = Map.of(
                "dias", Arrays.asList("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"),
                "entrenamientos", Arrays.stream(entrenamientosPorDia).boxed().collect(Collectors.toList()),
                "totalPesoLevantado", Math.round(totalPesoSemanaReferencia * 100.0) / 100.0,
                "totalRepeticiones", totalRepeticiones,
                "volumenEntrenamiento", Math.round(volumenEntrenamiento * 100.0) / 100.0,
                "pesoPromedioUsuario", usuario.getActualWeight()
        );

        // ============================================
        // 3️⃣ EVOLUCIÓN MENSUAL (evolucionMensualData)
        // ============================================

        // Últimos 6 meses
        List<String> meses = Arrays.asList("Ene", "Feb", "Mar", "Abr", "May", "Jun");

        // Calcular evolución del peso (simulado con degradado lineal)
        double pesoInicial = usuario.getInitialWeight();
        double pesoFinal = usuario.getActualWeight();
        double diferenciaPeso = pesoFinal - pesoInicial;

        List<Double> pesoUsuarioPorMes = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            double pesoMes = pesoInicial + (diferenciaPeso * i / 5.0);
            pesoUsuarioPorMes.add(Math.round(pesoMes * 10.0) / 10.0);
        }

        // Calcular promedio de fuerza semanal del mes de referencia
        LocalDate inicioMes = mesReferencia.atDay(1);
        LocalDate finMes = mesReferencia.atEndOfMonth();

        List<WorkoutSession> sesionesMesReferencia = todasLasSesiones.stream()
                .filter(s -> !s.getDate().isBefore(inicioMes) && !s.getDate().isAfter(finMes))
                .collect(Collectors.toList());

        double promedioFuerzaSemanal = sesionesMesReferencia.isEmpty() ? 0.0 :
                calcularPesoTotal(sesionesMesReferencia) / 4.0; // Aproximado por 4 semanas

        // Determinar tendencia basada en progresión de fuerza
        String tendencia = determinarTendenciaPorFuerza(userId, todasLasSesiones);

        Map<String, Object> evolucionMensualData = Map.of(
                "meses", meses,
                "pesoUsuario", pesoUsuarioPorMes,
                "pesoInicialUsuario", usuario.getInitialWeight(),
                "pesoFinalUsuario", usuario.getActualWeight(),
                "tendencia", tendencia,
                "promedioFuerzaSemanal", Math.round(promedioFuerzaSemanal * 100.0) / 100.0
        );

        // ============================================
        // 📦 RESPUESTA FINAL
        // ============================================

        return Map.of(
                "metricsData", metricsData,
                "progresoSemanalData", progresoSemanalData,
                "evolucionMensualData", evolucionMensualData
        );
    }

// ============================================
// 🔧 MÉTODOS AUXILIARES
// ============================================

    private double calcularPesoTotal(List<WorkoutSession> sesiones) {
        return sesiones.stream()
                .flatMap(s -> s.getSetRecords().stream())
                .mapToDouble(sr -> sr.getWeightUsed() * sr.getRealRepetitions())
                .sum();
    }

// Nota: El método calcularProgresoFuerzaSimple ya existe en tu código

    private String determinarTendenciaPorFuerza(Long userId, List<WorkoutSession> todasLasSesiones) {
        // Obtener las últimas 6 semanas de entrenamientos
        LocalDate ahora = LocalDate.now();
        List<Double> fuerzaPorSemana = new ArrayList<>();

        for (int i = 5; i >= 0; i--) {
            LocalDate inicioSemana = ahora.minusWeeks(i).with(DayOfWeek.MONDAY);
            LocalDate finSemana = ahora.minusWeeks(i).with(DayOfWeek.SUNDAY);

            List<WorkoutSession> sesionesSemana = todasLasSesiones.stream()
                    .filter(s -> !s.getDate().isBefore(inicioSemana) && !s.getDate().isAfter(finSemana))
                    .collect(Collectors.toList());

            double pesoTotalSemana = calcularPesoTotal(sesionesSemana);
            fuerzaPorSemana.add(pesoTotalSemana);
        }

        // Filtrar semanas con entrenamientos
        List<Double> semanasConDatos = fuerzaPorSemana.stream()
                .filter(f -> f > 0)
                .collect(Collectors.toList());

        if (semanasConDatos.size() < 2) return "INICIANDO";

        // Comparar primeras 2 semanas vs últimas 2 semanas
        int mitad = semanasConDatos.size() / 2;

        double promedioInicio = semanasConDatos.subList(0, Math.min(2, mitad)).stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);

        double promedioFinal = semanasConDatos.subList(
                        Math.max(semanasConDatos.size() - 2, mitad),
                        semanasConDatos.size()
                ).stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);

        if (promedioInicio == 0) return "INICIANDO";

        double cambioFuerza = ((promedioFinal - promedioInicio) / promedioInicio) * 100;

        // Clasificación de tendencia
        if (cambioFuerza > 5.0) return "MEJORANDO";      // +8% de fuerza
        if (cambioFuerza < -5.0) return "BAJANDO";       // -8% de fuerza
        return "ESTABLE";                                 // Entre -8% y +8%
    }
    @Transactional
    public UserStatsResponseDTO obtenerEstadisticasBasicas(Long userId) {
        User usuario = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<WorkoutSession> sesiones = workoutSessionRepository.findByUserId(userId);

        LocalDate ahora = LocalDate.now();
        LocalDate inicioMes = ahora.withDayOfMonth(1);

        // 1️⃣ Entrenamientos este mes
        long entrenamientosEsteMes = sesiones.stream()
                .filter(WorkoutSession::isCompleted)
                .filter(s -> !s.getDate().isBefore(inicioMes))
                .count();

        // 2️⃣ Peso total levantado
        double pesoTotalLevantado = sesiones.stream()
                .flatMap(s -> s.getSetRecords() != null ? s.getSetRecords().stream() : java.util.stream.Stream.empty())
                .mapToDouble(sr -> sr.getWeightUsed() * sr.getRealRepetitions())
                .sum();

        // 3️⃣ Racha actual (basada en el nivel del usuario)
        int sesionesRequeridas;
        switch (usuario.getLevel()) {
            case CONSTANTE -> sesionesRequeridas = 3;
            case DESAFIANTE -> sesionesRequeridas = 4;
            default -> sesionesRequeridas = 2;
        }

        int racha = 0;
        LocalDate hoy = LocalDate.now();

        // analizamos las últimas 8 semanas
        for (int i = 0; i < 8; i++) {
            LocalDate inicioSemana = hoy.minusWeeks(i).with(java.time.DayOfWeek.MONDAY);
            LocalDate finSemana = hoy.minusWeeks(i).with(java.time.DayOfWeek.SUNDAY);

            long sesionesCompletadas = sesiones.stream()
                    .filter(WorkoutSession::isCompleted)
                    .filter(s -> !s.getDate().isBefore(inicioSemana) && !s.getDate().isAfter(finSemana))
                    .count();

            if (sesionesCompletadas >= sesionesRequeridas) {
                racha++;
            } else {
                break;
            }
        }

        return new UserStatsResponseDTO(
                (int) entrenamientosEsteMes,
                racha,
                pesoTotalLevantado
        );
    }


    // ---------------------------------------------------------
    // 🔹 MÉTODOS DE CÁLCULO
    // ---------------------------------------------------------
    private ProgresoSemanalDTO calcularProgresoSemanal(List<WorkoutSession> sesiones, LocalDate inicio, LocalDate fin, Long userId) {
        ProgresoSemanalDTO progreso = new ProgresoSemanalDTO();
        progreso.setSemanaInicio(inicio);
        progreso.setSemanaFin(fin);

        if (sesiones.isEmpty()) {
            User user = userRepository.findById(userId).orElseThrow();
            return crearProgresoSemanalVacio(inicio, fin, user.getActualWeight());
        }

        double totalPeso = 0;
        int totalReps = 0;
        int totalSets = 0;

        for (WorkoutSession sesion : sesiones) {
            for (SetRecord serie : sesion.getSetRecords()) {
                totalPeso += serie.getWeightUsed() * serie.getRealRepetitions();
                totalReps += serie.getRealRepetitions();
                totalSets++;
            }
        }

        progreso.setTotalPesoLevantado(Math.round(totalPeso * 100.0) / 100.0);
        progreso.setTotalRepeticiones(totalReps);
        progreso.setTotalSeriesCompletadas(totalSets);
        progreso.setEntrenamientosCompletados(sesiones.size());
        progreso.setVolumenEntrenamiento(Math.round(totalPeso * 100.0) / 100.0);

        User user = userRepository.findById(userId).orElseThrow();
        progreso.setPesoPromedioUsuario(user.getActualWeight());
        progreso.setProgresoFuerza(calcularProgresoFuerzaSimple(sesiones));

        return progreso;
    }

    private Double calcularProgresoFuerzaSimple(List<WorkoutSession> sesiones) {
        if (sesiones.isEmpty()) return 0.0;

        double volumenTotal = sesiones.stream()
                .flatMap(s -> s.getSetRecords().stream())
                .mapToDouble(s -> s.getWeightUsed() * s.getRealRepetitions())
                .sum();

        double promedio = volumenTotal / sesiones.size();

        if (promedio > 1000) return 15.5;
        if (promedio > 500) return 8.2;
        return 2.1;
    }

    private ProgresoSemanalDTO crearProgresoSemanalVacio(LocalDate inicio, LocalDate fin, Double peso) {
        ProgresoSemanalDTO dto = new ProgresoSemanalDTO();
        dto.setSemanaInicio(inicio);
        dto.setSemanaFin(fin);
        dto.setTotalPesoLevantado(0.0);
        dto.setTotalRepeticiones(0);
        dto.setTotalSeriesCompletadas(0);
        dto.setEntrenamientosCompletados(0);
        dto.setProgresoFuerza(0.0);
        dto.setVolumenEntrenamiento(0.0);
        dto.setPesoPromedioUsuario(peso);
        return dto;
    }

    private String determinarTendencia(List<ProgresoSemanalDTO> semanas) {
        if (semanas.size() < 2) return "INICIANDO";

        double primero = semanas.get(semanas.size() - 1).getVolumenEntrenamiento();
        double ultimo = semanas.get(0).getVolumenEntrenamiento();

        if (ultimo > primero * 1.1) return "MEJORANDO";
        if (ultimo < primero * 0.9) return "BAJANDO";
        return "ESTABLE";
    }

    private String calcularTendenciaMes(double promedio) {
        if (promedio >= 10) return "EXCELENTE";
        if (promedio >= 7) return "BIEN";
        if (promedio >= 5) return "NORMAL";
        return "INICIANDO";
    }


    public ComparacionDosMesesDTO compararDosMeses(
            Long userId,
            int mes1, int año1,
            int mes2, int año2
    ) {
        MesComparacionDTO datosMes1 = calcularDatosMes(userId, mes1, año1);
        MesComparacionDTO datosMes2 = calcularDatosMes(userId, mes2, año2);

        return new ComparacionDosMesesDTO(datosMes1, datosMes2);
    }


    // ------------------------------------------------------------
// Cálculo por mes
// ------------------------------------------------------------
    private MesComparacionDTO calcularDatosMes(Long userId, int mes, int año) {

        // 1. Obtener sesiones del mes
        List<WorkoutSession> sesiones = workoutSessionRepository.findByUserIdAndMonth(userId, mes, año);

        // 2. Obtener rango de fechas del mes
        LocalDate inicioMes = LocalDate.of(año, mes, 1);
        LocalDate finMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());

        // 3. Obtener registros de peso del mes
        List<WeightRecord> pesosMes = weightRecordRepository.findByUserAndMonth(userId, inicioMes, finMes);

        // 4. Calcular peso inicial y final del mes
        double pesoInicial = pesosMes.isEmpty()
                ? obtenerUltimoPesoAntes(userId, inicioMes)          // Si no hay pesos ese mes
                : pesosMes.get(0).getWeight();

        double pesoFinal = pesosMes.isEmpty()
                ? obtenerUltimoPesoAntes(userId, finMes)            // Último peso conocido
                : pesosMes.get(pesosMes.size() - 1).getWeight();

        double cambioPeso = pesoFinal - pesoInicial;

        // 5. Cálculo de métricas
        double totalPesoLevantado = calcularPesoTotal(sesiones);
        int totalEntrenamientos = sesiones.size();
        double promedioFuerzaSemanal = calcularProgresoFuerzaSimple(sesiones);
        String tendencia = calcularTendenciaMes(promedioFuerzaSemanal);

        // 6. Nombre del mes
        String nombreMes = nombreMesCompleto(mes) + " " + año;

        return new MesComparacionDTO(
                nombreMes,
                pesoInicial,
                pesoFinal,
                cambioPeso,
                totalPesoLevantado,
                totalEntrenamientos,
                promedioFuerzaSemanal,
                tendencia
        );
    }
    private double obtenerUltimoPesoAntes(Long userId, LocalDate fecha) {
        WeightRecord last = weightRecordRepository.findTopByUserIdAndDateBeforeOrderByDateDesc(userId, fecha);
        if (last != null) return last.getWeight();

        // Si no hay registros, usar peso inicial del usuario
        User u = userRepository.findById(userId).orElseThrow();
        return u.getInitialWeight();
    }
    private String nombreMesCompleto(int mes) {
        return switch (mes) {
            case 1 -> "Enero";
            case 2 -> "Febrero";
            case 3 -> "Marzo";
            case 4 -> "Abril";
            case 5 -> "Mayo";
            case 6 -> "Junio";
            case 7 -> "Julio";
            case 8 -> "Agosto";
            case 9 -> "Septiembre";
            case 10 -> "Octubre";
            case 11 -> "Noviembre";
            case 12 -> "Diciembre";
            default -> "Desconocido";
        };
    }
}

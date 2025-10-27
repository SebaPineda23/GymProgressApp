package Application.GymProgress.Controllers.User;

import Application.GymProgress.DTOs.ProgresoMensualDTO;
import Application.GymProgress.DTOs.ProgresoSemanalDTO;
import Application.GymProgress.Services.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/gymProgres/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @GetMapping("/user/{userId}/semanal")
    public ResponseEntity<ProgresoSemanalDTO> obtenerProgresoSemanal(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        try {
            ProgresoSemanalDTO progreso = progressService.obtenerProgresoSemanal(userId, fecha);
            return ResponseEntity.ok(progreso);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}/semanal/actual")
    public ResponseEntity<ProgresoSemanalDTO> obtenerProgresoSemanalActual(@PathVariable Long userId) {
        try {
            ProgresoSemanalDTO progreso = progressService.obtenerProgresoSemanal(userId, LocalDate.now());
            return ResponseEntity.ok(progreso);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}/mensual")
    public ResponseEntity<ProgresoMensualDTO> obtenerProgresoMensual(
            @PathVariable Long userId,
            @RequestParam String mes) { // Formato: "2024-01"

        try {
            YearMonth yearMonth = YearMonth.parse(mes);
            ProgresoMensualDTO progreso = progressService.obtenerProgresoMensual(userId, yearMonth);
            return ResponseEntity.ok(progreso);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}/mensual/actual")
    public ResponseEntity<ProgresoMensualDTO> obtenerProgresoMensualActual(@PathVariable Long userId) {
        try {
            ProgresoMensualDTO progreso = progressService.obtenerProgresoMensual(userId, YearMonth.now());
            return ResponseEntity.ok(progreso);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}/historial")
    public ResponseEntity<List<ProgresoSemanalDTO>> obtenerHistorialSemanal(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "4") int semanas) {

        try {
            List<ProgresoSemanalDTO> historial = progressService.obtenerHistorialSemanal(userId, semanas);
            return ResponseEntity.ok(historial);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}/resumen")
    public ResponseEntity<Map<String, Object>> obtenerResumenProgreso(@PathVariable Long userId) {
        try {
            Map<String, Object> resumen = progressService.obtenerResumenProgreso(userId);
            return ResponseEntity.ok(resumen);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticasUsuario(@PathVariable Long userId) {
        try {
            Map<String, Object> estadisticas = progressService.obtenerEstadisticasUsuario(userId);
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}/semanal/pasada")
    public ResponseEntity<ProgresoSemanalDTO> obtenerProgresoSemanalPasada(@PathVariable Long userId) {
        try {
            ProgresoSemanalDTO progreso = progressService.obtenerProgresoSemanal(userId, LocalDate.now().minusWeeks(1));
            return ResponseEntity.ok(progreso);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}/mensual/pasado")
    public ResponseEntity<ProgresoMensualDTO> obtenerProgresoMensualPasado(@PathVariable Long userId) {
        try {
            ProgresoMensualDTO progreso = progressService.obtenerProgresoMensual(userId, YearMonth.now().minusMonths(1));
            return ResponseEntity.ok(progreso);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}/comparativa")
    public ResponseEntity<Map<String, Object>> obtenerComparativaMensual(
            @PathVariable Long userId,
            @RequestParam String mes1,
            @RequestParam String mes2) {

        try {
            YearMonth yearMonth1 = YearMonth.parse(mes1);
            YearMonth yearMonth2 = YearMonth.parse(mes2);

            ProgresoMensualDTO progreso1 = progressService.obtenerProgresoMensual(userId, yearMonth1);
            ProgresoMensualDTO progreso2 = progressService.obtenerProgresoMensual(userId, yearMonth2);

            Map<String, Object> comparativa = Map.of(
                    "mes1", progreso1,
                    "mes2", progreso2,
                    "diferenciaEntrenamientos", progreso2.getTotalEntrenamientos() - progreso1.getTotalEntrenamientos(),
                    "diferenciaPesoLevantado", progreso2.getTotalPesoLevantado() - progreso1.getTotalPesoLevantado(),
                    "evolucionTendencia", progreso2.getTendencia() + " vs " + progreso1.getTendencia()
            );

            return ResponseEntity.ok(comparativa);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
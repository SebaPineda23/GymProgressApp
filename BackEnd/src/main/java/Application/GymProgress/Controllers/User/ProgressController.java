package Application.GymProgress.Controllers.User;

import Application.GymProgress.DTOs.ComparacionDosMesesDTO;
import Application.GymProgress.DTOs.ProgresoMensualDTO;
import Application.GymProgress.DTOs.ProgresoSemanalDTO;
import Application.GymProgress.DTOs.UserStatsResponseDTO;
import Application.GymProgress.Services.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/gymProgress/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @GetMapping("/user/{userId}/dashboard")
    public ResponseEntity<Map<String, Object>> obtenerDashboard(
            @PathVariable Long userId,
            @RequestParam(required = false) String fecha) {

        Map<String, Object> dashboard = progressService.obtenerDashboardCompleto(userId, Optional.ofNullable(fecha));
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/{id}/basicStats")
    public ResponseEntity<UserStatsResponseDTO> obtenerEstadisticasBasicas(@PathVariable Long id) {
        try {
            UserStatsResponseDTO stats = progressService.obtenerEstadisticasBasicas(id);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/comparar-meses")
    public ResponseEntity<ComparacionDosMesesDTO> compararMeses(
            @RequestParam Long userId,
            @RequestParam int mes1,
            @RequestParam int anio1,
            @RequestParam int mes2,
            @RequestParam int anio2
    ) {

        ComparacionDosMesesDTO resultado =
                progressService.compararDosMeses(
                        userId, mes1, anio1, mes2, anio2
                );

        return ResponseEntity.ok(resultado);
    }


}
package Application.GymProgress.Controllers.User;

import Application.GymProgress.Entities.Register;
import Application.GymProgress.Services.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gymProgress/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping("/user/{userId}/progress")
    public ResponseEntity<List<Register>> getUserProgress(@PathVariable Long userId) {
        return ResponseEntity.ok(statisticsService.getUserProgress(userId));
    }

    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<Map<String, Object>> getUserSummary(@PathVariable Long userId) {
        return ResponseEntity.ok(statisticsService.getUserSummary(userId));
    }
}

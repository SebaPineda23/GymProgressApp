package Application.GymProgress.Services;

import Application.GymProgress.Entities.Register;
import Application.GymProgress.Repositories.RegisterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final RegisterRepository registerRepository;

    public List<Register> getUserProgress(Long userId) {
        return registerRepository.findByUserIdOrderByDateAsc(userId);
    }

    public Map<String, Object> getUserSummary(Long userId) {
        List<Register> registers = getUserProgress(userId);
        double avgWeight = registers.stream().mapToDouble(Register::getWeightUsed).average().orElse(0);
        double avgDifficulty = registers.stream().mapToInt(Register::getDifficultPerceived).average().orElse(0);
        int totalReps = registers.stream().mapToInt(Register::getRealRepetitions).sum();

        Map<String, Object> summary = new HashMap<>();
        summary.put("avgWeight", avgWeight);
        summary.put("avgDifficulty", avgDifficulty);
        summary.put("totalReps", totalReps);
        summary.put("totalSessions", registers.size());

        return summary;
    }
}


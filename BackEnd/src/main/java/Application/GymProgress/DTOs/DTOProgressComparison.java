package Application.GymProgress.DTOs;

import lombok.Data;

import java.util.Map;

@Data
public class DTOProgressComparison {
    private DTOProgressStats currentPeriod;
    private DTOProgressStats previousPeriod;
    private double progressPercentage;
    private String trend;
    private Map<String, Double> metricBreakdown;

    public DTOProgressComparison() {}
}

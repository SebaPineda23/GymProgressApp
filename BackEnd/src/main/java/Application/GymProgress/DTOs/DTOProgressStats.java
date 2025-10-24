package Application.GymProgress.DTOs;

import lombok.Data;

@Data
public class DTOProgressStats {
    private double totalVolume;
    private double maxWeight;
    private int totalRepetitions;
    private double successRate;
    private int sessionsCompleted;
    private double averageDifficulty;

    public DTOProgressStats(double totalVolume, double maxWeight, int totalRepetitions,
                         double successRate, int sessionsCompleted, double averageDifficulty) {
        this.totalVolume = totalVolume;
        this.maxWeight = maxWeight;
        this.totalRepetitions = totalRepetitions;
        this.successRate = successRate;
        this.sessionsCompleted = sessionsCompleted;
        this.averageDifficulty = averageDifficulty;
    }
}

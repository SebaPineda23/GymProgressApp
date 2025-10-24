package Application.GymProgress.DTOs;

import Application.GymProgress.Enum.Level;
import Application.GymProgress.Enum.MuscleGroup;

import java.util.Collections;
import java.util.Set;

public class DTOExercise {

    private String description;
    private double baseWeight;
    private Level level;
    private String name;
    private Set<MuscleGroup> muscleGroupSet;

    public DTOExercise() {}

    // Constructor con parámetros
    public DTOExercise(String name, String description, double baseWeight, Level level, MuscleGroup muscleGroupSet) {
        this.name = name;
        this.description = description;
        this.baseWeight = baseWeight;
        this.level = level;
        this.muscleGroupSet = Collections.singleton(muscleGroupSet);
    }

    // Getters y Setters

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getBaseWeight() {
        return baseWeight;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public void setBaseWeight(double baseWeight) {
        this.baseWeight = baseWeight;
    }

    public void setName(String userName) {
        this.name = userName;
    }

    public Set<MuscleGroup> getMuscleGroupSet() {
        return muscleGroupSet;
    }

    public void setMuscleGroupSet(Set<MuscleGroup> muscleGroupSet) {
        this.muscleGroupSet = muscleGroupSet;
    }
}

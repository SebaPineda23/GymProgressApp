// src/test/java/Application/GymProgress/Service/ProgressServiceTest.java
package Application.GymProgress.Service;

import Application.GymProgress.DTOs.DTOProgressComparison;
import Application.GymProgress.Entities.*;
import Application.GymProgress.Enum.Level;
import Application.GymProgress.Enum.MuscleGroup;
import Application.GymProgress.Enum.Role;
import Application.GymProgress.Repositories.ExerciseRepository;
import Application.GymProgress.Repositories.RegisterRepository;
import Application.GymProgress.Repositories.UserRepository;
import Application.GymProgress.Services.ProgressService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ProgressServiceTest {

    @Autowired
    private ProgressService progressService;

    @Autowired
    private RegisterRepository registerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Test
    void testPositiveProgress() {
        // Setup
        User user = createTestUser();
        Exercise exercise = createTestExercise();

        createRegister(user, exercise, LocalDate.now().with(DayOfWeek.MONDAY), 80.0, 8, 10, 7);
        createRegister(user, exercise, LocalDate.now().minusWeeks(1).with(DayOfWeek.MONDAY), 70.0, 8, 8, 6);

        // Execute
        DTOProgressComparison result = progressService.getWeeklyProgress(user.getId(), LocalDate.now());

        // Verify
        assertNotNull(result);
        assertTrue(result.getProgressPercentage() > 0);
        assertEquals("IMPROVING", result.getTrend());
    }

    private User createTestUser() {
        User user = User.builder()
                .userName("Test User")
                .email("test@test.com")
                .password("password")
                .active(true)
                .level(Level.PRINCIPIANTE)
                .roleSet(Set.of(Role.USER))
                .build();
        return userRepository.save(user);
    }

    private Exercise createTestExercise() {
        Exercise exercise = new Exercise();
        exercise.setName("Test Exercise");
        exercise.setMuscleGroupSet(Set.of(MuscleGroup.PECTORAL));
        exercise.setLevel(Level.PRINCIPIANTE);
        return exerciseRepository.save(exercise);
    }

    private void createRegister(User user, Exercise exercise, LocalDate date,
                                double weight, int plannedReps, int realReps, int difficulty) {
        Register register = new Register();
        register.setUser(user);
        register.setExercise(exercise);
        register.setDate(date);
        register.setWeightUsed(weight);
        register.setPlanedRepetitions(plannedReps);
        register.setRealRepetitions(realReps);
        register.setDifficultPerceived(difficulty);
        register.setEasyComplete(realReps >= plannedReps);
        registerRepository.save(register);
    }
}
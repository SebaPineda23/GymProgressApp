package Application.GymProgress.Services;

import Application.GymProgress.Entities.User;
import Application.GymProgress.Enum.Level;
import Application.GymProgress.Enum.Role;
import Application.GymProgress.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public boolean isFirstAdmin() {
        return userRepository.findAll().stream()
                .noneMatch(user -> user.getRoleSet().contains(Role.ADMIN));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public User updateProgress(Long userId, double newWeight, int additionalWeeks) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setActualWeight(newWeight);
        user.setWeeksTrained(user.getWeeksTrained() + additionalWeeks);

        updateLevel(user);

        return userRepository.save(user);
    }

    public void updateLevel(User user) {
        double progreso = ((user.getActualWeight() - user.getInitialWeight()) / user.getInitialWeight()) * 100;

        if(user.getWeeksTrained() > 24 || progreso > 15) {
            user.setLevel(Level.AVANZADO);
        } else if(user.getWeeksTrained() > 12 || progreso > 5) {
            user.setLevel(Level.INTERMEDIO);
        } else {
            user.setLevel(Level.PRINCIPIANTE);
        }
    }
}

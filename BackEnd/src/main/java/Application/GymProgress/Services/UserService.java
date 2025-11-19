package Application.GymProgress.Services;

import Application.GymProgress.DTOs.UpdateUserDTO;
import Application.GymProgress.Entities.User;
import Application.GymProgress.Entities.WeightRecord;
import Application.GymProgress.Enum.Level;
import Application.GymProgress.Enum.Role;
import Application.GymProgress.Repositories.UserRepository;
import Application.GymProgress.Repositories.WeightRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final WeightRecordRepository weightRecordRepository;

    public boolean isFirstAdmin() {
        return userRepository.findAll().stream()
                .noneMatch(user -> user.getRoleSet().contains(Role.ADMIN));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public User updateUser(Long userId, UpdateUserDTO updateUserDTO) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Guardamos peso previo
        Double oldWeight = user.getActualWeight();
        Double newWeight = updateUserDTO.getNewWeight();

        // Actualizamos datos básicos
        user.setName(updateUserDTO.getNewName());
        user.setLastName(updateUserDTO.getNewLastName());
        user.setEmail(updateUserDTO.getNewEmail());
        user.setLevel(updateUserDTO.getNewLevel());

        // Actualizamos el peso en la entidad User
        user.setActualWeight(newWeight);

        // Guardamos cambios del User
        User updated = userRepository.save(user);

        // Registrar nuevo WeightRecord SOLO si el peso cambió
        if (newWeight != null && !newWeight.equals(oldWeight)) {

            WeightRecord newRecord = WeightRecord.builder()
                    .weight(newWeight)
                    .date(LocalDate.now())
                    .user(user)
                    .build();

            weightRecordRepository.save(newRecord);
        }

        return updated;
    }

}

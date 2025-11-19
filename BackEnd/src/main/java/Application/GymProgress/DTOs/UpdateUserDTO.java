package Application.GymProgress.DTOs;

import Application.GymProgress.Enum.Level;
import lombok.Data;

@Data
public class UpdateUserDTO {
    String newName;
    String newEmail;
    Level newLevel;
    String newLastName;
    double newWeight;
}

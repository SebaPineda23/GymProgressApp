package Application.GymProgress.RegisterRequest;

import Application.GymProgress.Enum.Level;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    String name;
    String userName;
    String password;
    String lastName;
    String email;
    double initialWeight;
    Level level;
    private boolean isAdmin;
}

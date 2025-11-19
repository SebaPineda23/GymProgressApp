package Application.GymProgress.Auth;

import Application.GymProgress.Enum.Level;
import Application.GymProgress.Enum.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseLogin {
    private Long id;
    private String name;
    private String userName;
    private String lastName;
    private String email;
    private Level level;
    private Set<Role> roles;
    private String token;
}

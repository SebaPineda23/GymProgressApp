package Application.GymProgress.Controllers.User;

import Application.GymProgress.DTOs.UpdateUserDTO;
import Application.GymProgress.Entities.User;
import Application.GymProgress.Enum.Level;
import Application.GymProgress.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/gymProgress/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateProgress(@PathVariable Long id,
                                               @RequestBody UpdateUserDTO updateUserDTO
                                               ) {
        User updatedUser = userService.updateUser(id, updateUserDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/first-admin")
    public ResponseEntity<Boolean> isFirstAdmin() {
        return ResponseEntity.ok(userService.isFirstAdmin());
    }
}

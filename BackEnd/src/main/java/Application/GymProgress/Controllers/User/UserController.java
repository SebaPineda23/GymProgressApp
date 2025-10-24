package Application.GymProgress.Controllers.User;

import Application.GymProgress.Entities.User;
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

    @PutMapping("/{id}/progress")
    public ResponseEntity<User> updateProgress(@PathVariable Long id,
                                               @RequestParam double newWeight,
                                               @RequestParam int additionalWeeks) {
        User updatedUser = userService.updateProgress(id, newWeight, additionalWeeks);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/first-admin")
    public ResponseEntity<Boolean> isFirstAdmin() {
        return ResponseEntity.ok(userService.isFirstAdmin());
    }
}

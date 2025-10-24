package Application.GymProgress.Controllers.User;

import Application.GymProgress.Entities.Register;
import Application.GymProgress.Services.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/gymProgress/registers")
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    @PostMapping
    public ResponseEntity<Register> createRegister(@RequestParam Long userId,
                                                   @RequestParam Long exerciseId,
                                                   @RequestParam double weightUsed,
                                                   @RequestParam int plannedReps,
                                                   @RequestParam int realReps,
                                                   @RequestParam int difficultyPerc) {
        Register register = registerService.createRegister(userId, exerciseId, weightUsed,
                plannedReps, realReps, difficultyPerc);
        return ResponseEntity.ok(register);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Register>> getRegistersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(registerService.getRegistersByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Register> getRegisterById(@PathVariable Long id) {
        return ResponseEntity.ok(registerService.getRegisterById(id));
    }
}

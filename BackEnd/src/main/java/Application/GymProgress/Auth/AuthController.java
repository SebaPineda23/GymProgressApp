package Application.GymProgress.Auth;

import Application.GymProgress.Enum.Role;
import Application.GymProgress.Login.LoginRequest;
import Application.GymProgress.RegisterRequest.AdminRegisterRequest;
import Application.GymProgress.RegisterRequest.RegisterRequest;
import Application.GymProgress.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/gymProgress/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseLogin> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }
    @PostMapping("/register")
    public ResponseEntity<AuthResponseRegister> register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(authService.register(request));
    }
    @PostMapping("/register/first-admin")
    public ResponseEntity<?> registerFirstAdmin(@RequestBody RegisterRequest request) {
        try {
            // Verificar si es el primer admin
            if (!userService.isFirstAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Ya existe un administrador registrado");
            }

            // Registrar el admin
            AuthResponseRegister response = authService.registerAdmin(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
    @GetMapping("/my-roles")
    public ResponseEntity<?> getMyRoles(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.ok("No autenticado");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("username", authentication.getName());
        response.put("authorities", authentication.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.toList()));
        response.put("authenticated", authentication.isAuthenticated());

        return ResponseEntity.ok(response);
    }
}
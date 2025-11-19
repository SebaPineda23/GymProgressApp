package Application.GymProgress.Auth;

import Application.GymProgress.Entities.User;
import Application.GymProgress.Enum.Level;
import Application.GymProgress.Enum.Role;
import Application.GymProgress.Jwt.JwtService;
import Application.GymProgress.Login.LoginRequest;
import Application.GymProgress.RegisterRequest.RegisterRequest;
import Application.GymProgress.Repositories.UserRepository;
import Application.GymProgress.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @Transactional
    public AuthResponseLogin login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword())
            );
            System.out.println("Authentication successful for user: " + request.getUserName());

            User user = userRepository.findByUserName(request.getUserName())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            String token = jwtService.getToken(user);

            return AuthResponseLogin.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .userName(user.getUsername())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .level(user.getLevel())
                    .roles(user.getRoleSet())
                    .token(token)
                    .build();
        } catch (Exception e) {
            System.out.println("Authentication failed: " + e.getMessage());
            throw e;
        }
    }

    @Transactional
    public AuthResponseRegister register(RegisterRequest request) {
        // 🔹 Validar que el userName no exista
        if (userRepository.findByUserName(request.getUserName()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        // 🔹 Validar que el email no exista
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        // 🔹 Asignar rol USER por defecto
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);

        // 🔹 Crear el nuevo usuario
        User user = User.builder()
                .userName(request.getUserName())
                .name(request.getName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .level(Level.SANO)
                .initialWeight(request.getInitialWeight())
                .actualWeight(request.getInitialWeight())
                .active(true)
                .roleSet(roles)
                .build();

        // 🔹 Guardar en base de datos
        userRepository.save(user);

        // 🔹 Retornar el token JWT generado
        return AuthResponseRegister.builder()
                .token(jwtService.getToken(user))
                .build();
    }

    @Transactional
    public AuthResponseRegister registerAdmin(RegisterRequest request) {
        // Solo validaciones básicas (userName y email únicos)
        if (userRepository.findByUserName(request.getUserName()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        Set<Role> roles = new HashSet<>();
        roles.add(Role.ADMIN);
        roles.add(Role.USER);

        User user = User.builder()
                .name(request.getName())
                .userName(request.getUserName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .level(Level.SANO)
                .initialWeight(request.getInitialWeight())
                .actualWeight(request.getInitialWeight())
                .active(true)
                .roleSet(roles)
                .build();

        userRepository.save(user);

        return AuthResponseRegister.builder()
                .token(jwtService.getToken(user))
                .build();
    }

    private boolean isFirstAdmin() {
        return userRepository.findAll().stream()
                .noneMatch(user -> user.getRoleSet().contains(Role.ADMIN));
    }

    private void validateRegisterRequest(RegisterRequest request) {
        if(request.getName() == null || request.getName().isEmpty()) throw new IllegalArgumentException("Nombre obligatorio");
        if(request.getUserName() == null || request.getUserName().isEmpty()) throw new IllegalArgumentException("Nombre de usuario obligatorio");
        if(request.getLastName() == null || request.getLastName().isEmpty()) throw new IllegalArgumentException("Apellido obligatorio");
        if(request.getEmail() == null || request.getEmail().isEmpty()) throw new IllegalArgumentException("Email obligatorio");
        if(request.getPassword() == null || request.getPassword().isEmpty()) throw new IllegalArgumentException("Contraseña obligatoria");
        if(userRepository.existsByEmail(request.getEmail())) throw new IllegalArgumentException("Email ya registrado");
    }
}

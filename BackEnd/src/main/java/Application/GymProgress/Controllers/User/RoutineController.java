package Application.GymProgress.Controllers.User;

import Application.GymProgress.DTOs.DTORoutine;
import Application.GymProgress.Entities.Routine;
import Application.GymProgress.Entities.User;
import Application.GymProgress.Services.RoutineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin("*")
@RequestMapping("/gymProgress/routines")
@RequiredArgsConstructor
public class RoutineController {

    private final RoutineService routineService;

    @GetMapping
    public ResponseEntity<List<Routine>> getRutinasUsuario(
            @AuthenticationPrincipal(expression = "user") User currentUser
    ) {
        List<Routine> rutinas = routineService.getRoutinesByUser(currentUser);
        return ResponseEntity.ok(rutinas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRutinaPorId(@PathVariable Long id) {
        try {
            Routine rutina = routineService.getRoutineById(id);
            return ResponseEntity.ok(rutina);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping("/createRoutine/{id}")
    public ResponseEntity<?> crearRutina(
           @PathVariable Long id,
            @Valid @RequestBody DTORoutine dtoRoutine
    ) {
        try {
            Routine rutina = routineService.crearRoutineUser(
                    id,
                    dtoRoutine.getNombre(),
                    dtoRoutine.getObjetivo(),
                    dtoRoutine.getExercisesIds()
            );
            return ResponseEntity.ok(rutina);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarRutina(
            @PathVariable Long id,
            @AuthenticationPrincipal(expression = "user") User currentUser,
            @Valid @RequestBody DTORoutine dtoRoutine
    ) {
        try {
            Routine rutina = routineService.getRoutineById(id);

            if (!rutina.getUser().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes modificar la rutina de otro usuario");
            }

            rutina = routineService.updateRutina(
                    rutina,
                    dtoRoutine.getNombre(),
                    dtoRoutine.getObjetivo(),
                    dtoRoutine.getExercisesIds()
            );
            return ResponseEntity.ok(rutina);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarRutina(
            @PathVariable Long id,
            @AuthenticationPrincipal(expression = "user") User currentUser
    ) {
        try {
            Routine rutina = routineService.getRoutineById(id);

            if (!rutina.getUser().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes eliminar la rutina de otro usuario");
            }

            routineService.deleteRoutine(rutina);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}

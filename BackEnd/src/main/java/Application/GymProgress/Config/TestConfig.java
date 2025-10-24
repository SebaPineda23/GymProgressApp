package Application.GymProgress.Config;

import Application.GymProgress.Services.ProgressTestRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class TestConfig {

    @Bean
    @Profile("test-progress") // Solo se ejecuta con este perfil
    public CommandLineRunner progressTestRunner(ProgressTestRunner testRunner) {
        return args -> {
            System.out.println("🚀 INICIANDO PRUEBAS DE PROGRESO...");
            testRunner.runVerificationTest();
            System.out.println("✅ PRUEBAS COMPLETADAS");
        };
    }
}

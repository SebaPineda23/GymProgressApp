package Application.GymProgress;

import Application.GymProgress.Services.ProgressTestRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GymProgressApplication {

	public static void main(String[] args) {
		SpringApplication.run(GymProgressApplication.class, args);
	}
//	@Bean
//	public CommandLineRunner runProgressTests(ProgressTestRunner testRunner) {
//		return args -> {
//			System.out.println("INICIANDO PRUEBAS AUTOMÁTICAS...");
//			testRunner.runVerificationTest();
//			System.out.println("PRUEBAS COMPLETADAS - La aplicación sigue corriendo");
//		};
//	}

}

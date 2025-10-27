package Application.GymProgress;


import Application.GymProgress.DTOs.ProgresoMensualDTO;
import Application.GymProgress.DTOs.ProgresoSemanalDTO;
import Application.GymProgress.Services.DataLoaderService;
import Application.GymProgress.Services.ProgressService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class GymProgressApplication {

	public static void main(String[] args) {
		SpringApplication.run(GymProgressApplication.class, args);
	}
//	@Bean
//	CommandLineRunner testCompleto(DataLoaderService dataLoader, ProgressService progresoService) {
//		return args -> {
//			System.out.println("🚀 ===========================================");
//			System.out.println("🚀 ANALIZANDO TU PROGRESO REAL");
//			System.out.println("🚀 ===========================================");
//
//			try {
//				// 1. CARGAR DATOS DE PRUEBA
//				System.out.println("\n📦 Cargando 2 meses de datos de entrenamiento...");
//				dataLoader.cargarDatosPrueba();
//				Long userId = dataLoader.obtenerUsuarioPruebaId();
//				System.out.println("✅ Datos cargados exitosamente");
//				System.out.println("   👤 Usuario ID: " + userId);
//
//				Thread.sleep(500);
//
//				// 2. PROGRESO SEMANAL ACTUAL
//				System.out.println("\n📊 SEMANA ACTUAL");
//				System.out.println("----------------");
//				ProgresoSemanalDTO semanal = progresoService.obtenerProgresoSemanal(userId, LocalDate.now());
//				System.out.println("🗓️  Período: " + semanal.getSemanaInicio() + " a " + semanal.getSemanaFin());
//				System.out.println("💪 Entrenamientos esta semana: " + semanal.getEntrenamientosCompletados());
//				System.out.println("🏋️  Volumen de entrenamiento: " + semanal.getTotalPesoLevantado() + " kg");
//				System.out.println("📈 Progreso de fuerza: " + semanal.getProgresoFuerza() + "%");
//
//				// 3. PROGRESO MENSUAL
//				System.out.println("\n📅 RESUMEN MENSUAL");
//				System.out.println("------------------");
//				ProgresoMensualDTO mensual = progresoService.obtenerProgresoMensual(userId, YearMonth.now());
//				System.out.println("🎯 Tendencia: " + mensual.getTendencia());
//				System.out.println("⚖️  Cambio de peso: " + mensual.getCambioPeso() + " kg");
//				System.out.println("💪 Total entrenamientos: " + mensual.getTotalEntrenamientos());
//
//				// 4. ESTADÍSTICAS QUE IMPORTAN 🎯
//				System.out.println("\n📈 TUS MÉTRICAS CLAVE");
//				System.out.println("--------------------");
//				Map<String, Object> estadisticas = progresoService.obtenerEstadisticasUsuario(userId);
//
//				System.out.println("🎯 HÁBITOS DE ENTRENAMIENTO:");
//				System.out.println("   ✅ Consistencia: " + estadisticas.get("consistencia"));
//				System.out.println("   📅 Frecuencia: " + estadisticas.get("frecuenciaSemanal"));
//				System.out.println("   🏆 Nivel: " + estadisticas.get("nivelConsistencia"));
//
//				System.out.println("🏋️  PROGRESO DE FUERZA:");
//				Map<String, Double> progresoFuerza = (Map<String, Double>) estadisticas.get("progresoFuerza");
//				progresoFuerza.forEach((ejercicio, progreso) ->
//						System.out.println("   📈 " + ejercicio + ": +" + progreso + "%")
//				);
//
//				System.out.println("💡 RECOMENDACIÓN:");
//				System.out.println("   " + estadisticas.get("recomendacion"));
//
//				// 5. RESUMEN FINAL
//				System.out.println("\n🎉 ===========================================");
//				System.out.println("🎉 ¡PROGRESO ANALIZADO!");
//				System.out.println("🎉 ===========================================");
//				System.out.println("📊 Sesiones totales: " + estadisticas.get("totalSesionesCompletadas"));
//				System.out.println("⏰ Tiempo entrenando: " + estadisticas.get("semanasEntrenando") + " semanas");
//				System.out.println("⚖️  Progreso de peso: " + estadisticas.get("progresoPeso") + " kg");
//				System.out.println("   ⚖️  Peso inicial: " + estadisticas.get("pesoInicial") + " kg");
//				System.out.println("   ⚖️  Peso actual: " + estadisticas.get("pesoActual") + " kg");
//				System.out.println("🎯 Nivel actual: " + estadisticas.get("nivelActual"));
//
//			} catch (Exception e) {
//				System.out.println("\n❌ ERROR: " + e.getMessage());
//				e.printStackTrace();
//			}
//		};
//	}
}

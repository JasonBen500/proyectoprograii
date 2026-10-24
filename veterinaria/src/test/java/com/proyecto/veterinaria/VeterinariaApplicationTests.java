package com.proyecto.veterinaria;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Arranca todo el contexto: necesita MySQL y la variable JWT_SECRET.
// Quita @Disabled para correrla en tu equipo con esos datos configurados.
@Disabled("Requiere MySQL y JWT_SECRET")
@SpringBootTest
class VeterinariaApplicationTests {

	@Test
	void contextLoads() {
	}

}

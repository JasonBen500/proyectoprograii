package com.proyecto.veterinaria.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.proyecto.veterinaria.exception.IntentosExcedidosException;
import org.junit.jupiter.api.Test;

class LoginAttemptServiceTest {

    private final LoginAttemptService servicio = new LoginAttemptService();

    @Test
    void bloqueaTrasLosIntentosMaximos() {
        String clave = LoginAttemptService.clave("ana", "1.1.1.1");
        for (int i = 0; i < LoginAttemptService.MAX_INTENTOS - 1; i++) {
            servicio.registrarFallo(clave);
            assertThatCode(() -> servicio.verificarNoBloqueado(clave)).doesNotThrowAnyException();
        }
        servicio.registrarFallo(clave);

        assertThatThrownBy(() -> servicio.verificarNoBloqueado(clave))
                .isInstanceOf(IntentosExcedidosException.class);
    }

    @Test
    void limpiarDesbloquea() {
        String clave = LoginAttemptService.clave("ana", "1.1.1.1");
        for (int i = 0; i < LoginAttemptService.MAX_INTENTOS; i++) {
            servicio.registrarFallo(clave);
        }
        servicio.limpiar(clave);

        assertThatCode(() -> servicio.verificarNoBloqueado(clave)).doesNotThrowAnyException();
    }

    @Test
    void laClaveIgnoraMayusculasYEspacios() {
        assertThat(LoginAttemptService.clave(" ANA ", "1.1.1.1"))
                .isEqualTo(LoginAttemptService.clave("ana", "1.1.1.1"));
    }

    @Test
    void otroUsuarioNoSeAfecta() {
        String ana = LoginAttemptService.clave("ana", "1.1.1.1");
        String luis = LoginAttemptService.clave("luis", "1.1.1.1");
        for (int i = 0; i < LoginAttemptService.MAX_INTENTOS; i++) {
            servicio.registrarFallo(ana);
        }

        assertThatCode(() -> servicio.verificarNoBloqueado(luis)).doesNotThrowAnyException();
    }
}

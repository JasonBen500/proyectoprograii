package com.proyecto.veterinaria.service;

import com.proyecto.veterinaria.exception.IntentosExcedidosException;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Limita los intentos fallidos de login por usuario + IP (en memoria)
@Service
public class LoginAttemptService {

    static final int MAX_INTENTOS = 5;
    private static final long VENTANA_MS = 15L * 60 * 1000;
    private static final long BLOQUEO_MS = 15L * 60 * 1000;
    private static final int MAX_REGISTROS = 10_000;

    private static final class Registro {
        int fallos;
        long primerFallo;
        long bloqueadoHasta;
    }

    private final Map<String, Registro> registros = new ConcurrentHashMap<>();

    public static String clave(String usuario, String ip) {
        String u = usuario == null ? "" : usuario.trim().toLowerCase(Locale.ROOT);
        return u + "|" + (ip == null ? "" : ip);
    }

    public void verificarNoBloqueado(String clave) {
        Registro r = registros.get(clave);
        long ahora = System.currentTimeMillis();
        if (r != null && r.bloqueadoHasta > ahora) {
            long minutos = Math.max(1, (r.bloqueadoHasta - ahora + 59_999) / 60_000);
            throw new IntentosExcedidosException(
                    "Demasiados intentos fallidos. Intente de nuevo en " + minutos + " minuto(s).");
        }
    }

    public void registrarFallo(String clave) {
        if (registros.size() > MAX_REGISTROS) {
            purgar();
        }
        long ahora = System.currentTimeMillis();
        registros.compute(clave, (k, r) -> {
            boolean vencido = r == null
                    || (r.bloqueadoHasta != 0 && r.bloqueadoHasta <= ahora)
                    || ahora - r.primerFallo > VENTANA_MS;
            Registro actual = vencido ? new Registro() : r;
            if (actual.fallos == 0) {
                actual.primerFallo = ahora;
            }
            actual.fallos++;
            if (actual.fallos >= MAX_INTENTOS) {
                actual.bloqueadoHasta = ahora + BLOQUEO_MS;
            }
            return actual;
        });
    }

    public void limpiar(String clave) {
        registros.remove(clave);
    }

    private void purgar() {
        long ahora = System.currentTimeMillis();
        registros.entrySet().removeIf(e -> e.getValue().bloqueadoHasta <= ahora
                && ahora - e.getValue().primerFallo > VENTANA_MS);
    }
}

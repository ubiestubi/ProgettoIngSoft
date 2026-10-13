package it.wat.progettoingsoft.security;

import it.wat.progettoingsoft.exceptions.Errore;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;

/**
 * 401 e 403 generati da Spring Security, nello stesso formato Errore di GlobalExceptionHandler.
 * Serve perché questi errori nascono nei filtri di sicurezza (prima o fuori dai controller) e quindi
 * non passano da @RestControllerAdvice.
 * - commence: richiesta senza utente loggato (nessuna sessione, sessione scaduta o chiusa col logout).
 * - handle: utente loggato ma senza il ruolo richiesto da @PreAuthorize.
 */
@Component
@RequiredArgsConstructor
public class GestoreErroriSicurezza implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final JsonMapper jsonMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException e) throws IOException {
        scriviErrore(response, 401, "Utente non autenticato");
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException e) throws IOException {
        scriviErrore(response, 403, "Operazione non permessa");
    }

    private void scriviErrore(HttpServletResponse response, int stato, String messaggio) throws IOException {
        response.setStatus(stato);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonMapper.writeValueAsString(new Errore(stato, messaggio)));
    }
}

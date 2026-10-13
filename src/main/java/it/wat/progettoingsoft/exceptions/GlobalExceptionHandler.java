package it.wat.progettoingsoft.exceptions;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Traduce le eccezioni che escono dai controller nel codice HTTP corrispondente, con il corpo JSON formattato
 * (come oggetto Errore).
 * Questa classe serve così i controller non hanno try/catch.
 * Le eccezioni non elencate qui sono gestite da Spring con la sua risposta 500 predefinita.
 * Per maggiori info leggi comeFunzionanoLeEccezioniInSpring.md
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400: dati non validi per regole del model (ex. creaUtente con email già in uso).
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Errore> datiNonValidi(IllegalArgumentException e) {
        return risposta(400, e.getMessage());
    }

    // 400: corpo JSON che non rispetta le validazioni nel DTO (jakarta validation).
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Errore> validazioneFallita(MethodArgumentNotValidException e) {
        return risposta(400, e.getBindingResult().getFieldError().getDefaultMessage());
    }

    // 400: JSON della richiesta illeggibile, tipo un parametro con un valore sbagliato (ex. tipo=PIPPO).
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Errore> jsonNonValido(HttpMessageNotReadableException e) {
        return risposta(400, "Richiesta non valida");
    }

    // 400:
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Errore> parametroNonValido(MethodArgumentTypeMismatchException e) {
        return risposta(400, "Valore non valido per il parametro " + e.getName());
    }

    // 400: parametro obbligatorio assente nell'URL (es. POST /api/bugs/{id}/assegna senza utenteId).
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Errore> parametroMancante(MissingServletRequestParameterException e) {
        return risposta(400, "Parametro obbligatorio mancante: " + e.getParameterName());
    }

    // 401: email o password sbagliate al login (la lancia AuthenticationManager.authenticate nel controller).
    // I 401 per "non loggato" e i 403 per ruolo insufficiente li genera Spring Security fuori dai controller:
    // li scrive security/GestoreErroriSicurezza, nello stesso formato.
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Errore> credenzialiNonValide(BadCredentialsException e) {
        return risposta(401, "Credenziali non valide");
    }

    // 404: issue, bug, utente o notifica inesistente (findById dei service).
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Errore> nonTrovato(EntityNotFoundException e) {
        return risposta(404, e.getMessage());
    }

    // 404: URL che non corrisponde a nessun endpoint (solo per utenti loggati: agli altri Spring Security
    // risponde prima 401).
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Errore> urlInesistente(NoResourceFoundException e) {
        return risposta(404, "Indirizzo inesistente: /" + e.getResourcePath());
    }

    // 405: URL giusto ma metodo HTTP sbagliato (es. GET su POST /api/notifiche/leggi-nuove).
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Errore> metodoNonPermesso(HttpRequestMethodNotSupportedException e) {
        return risposta(405, "Metodo " + e.getMethod() + " non permesso su questo indirizzo");
    }

    // 409: richiesta corretta ma non effettuabile in quel momento (ex. assegnare un bug chiuso).
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Errore> conflitto(IllegalStateException e) {
        return risposta(409, e.getMessage());
    }

    private ResponseEntity<Errore> risposta(int stato, String messaggio) {
        return ResponseEntity.status(stato).body(new Errore(stato, messaggio));
    }
}

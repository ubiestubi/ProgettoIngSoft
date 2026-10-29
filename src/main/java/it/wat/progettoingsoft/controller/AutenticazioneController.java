package it.wat.progettoingsoft.controller;

import it.wat.progettoingsoft.dto.LoginRequest;
import it.wat.progettoingsoft.dto.UtenteDTO;
import it.wat.progettoingsoft.security.UtenteAutenticato;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Login e utente corrente. Il logout (POST /api/auth/logout) lo gestisce
 * direttamente Spring Security, vedi SecurityConfig.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AutenticazioneController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    // Credenziali sbagliate: authenticate lancia BadCredentialsException -> 401 (GlobalExceptionHandler).
    @PostMapping("/login")
    public UtenteDTO login(@Valid @RequestBody LoginRequest richiesta,
                           HttpServletRequest request, HttpServletResponse response) {
        Authentication autenticazione = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(richiesta.email(), richiesta.password()));

        // Sessione nuova a ogni login: se ce n'era già una (ex. un altro utente sullo stesso client)
        // la si chiude, così l'id di sessione usato prima del login non resta valido dopo.
        HttpSession vecchia = request.getSession(false);
        if (vecchia != null) {
            vecchia.invalidate();
        }

        // Salvataggio dell'utente loggato nella sessione
        SecurityContext contesto = SecurityContextHolder.createEmptyContext();
        contesto.setAuthentication(autenticazione);
        SecurityContextHolder.setContext(contesto);
        securityContextRepository.saveContext(contesto, request, response);

        return UtenteDTO.da((UtenteAutenticato) autenticazione.getPrincipal());
    }

    @GetMapping("/me")
    public UtenteDTO me(@AuthenticationPrincipal UtenteAutenticato utente) {
        return UtenteDTO.da(utente);
    }
}

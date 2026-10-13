package it.wat.progettoingsoft.controller;

import it.wat.progettoingsoft.dto.NotificaDTO;
import it.wat.progettoingsoft.entities.Notifica;
import it.wat.progettoingsoft.security.UtenteAutenticato;
import it.wat.progettoingsoft.service.UtenteNormaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Notifiche dell'utente loggato (docs/ContrattoApi.md, sezione Notifiche). Solo gli utenti normali
 * ricevono notifiche: @PreAuthorize sulla classe vale per tutti i metodi.
 */
@RestController
@RequestMapping("/api/notifiche")
@PreAuthorize("hasRole('NORMALE')")
@RequiredArgsConstructor
public class NotificaController {

    private final UtenteNormaleService utenteNormaleService;

    // Tutte le notifiche, lette e non, più recenti prima.
    @GetMapping
    public List<NotificaDTO> tutte(@AuthenticationPrincipal UtenteAutenticato utente) {
        return aDto(utenteNormaleService.getAllNotifiche(utente.getId()));
    }

    // Numero di notifiche non lette, senza segnarle come lette (es. per il badge del client).
    @GetMapping("/conteggio")
    public long conteggio(@AuthenticationPrincipal UtenteAutenticato utente) {
        return utenteNormaleService.countNewNotifiche(utente.getId());
    }

    // Le notifiche non lette, che da questo momento risultano lette. POST perché modifica dei dati.
    @PostMapping("/leggi-nuove")
    public List<NotificaDTO> leggiNuove(@AuthenticationPrincipal UtenteAutenticato utente) {
        return aDto(utenteNormaleService.getNewNotificheAndSegnaComeLette(utente.getId()));
    }

    private List<NotificaDTO> aDto(List<Notifica> notifiche) {
        List<NotificaDTO> risposta = new ArrayList<>();
        for (Notifica notifica : notifiche) {
            risposta.add(NotificaDTO.da(notifica));
        }
        return risposta;
    }
}

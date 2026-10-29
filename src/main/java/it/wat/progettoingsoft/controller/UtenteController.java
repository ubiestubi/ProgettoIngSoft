package it.wat.progettoingsoft.controller;

import it.wat.progettoingsoft.dto.AssegnatarioSuggeritoDTO;
import it.wat.progettoingsoft.dto.CreaUtenteRequest;
import it.wat.progettoingsoft.dto.UtenteDTO;
import it.wat.progettoingsoft.entities.Utente;
import it.wat.progettoingsoft.entities.UtenteNormale;
import it.wat.progettoingsoft.service.AmministratoreService;
import it.wat.progettoingsoft.service.UtenteNormaleService;
import it.wat.progettoingsoft.service.UtenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestione degli utenti (docs/ContrattoApi.md, sezione Utenti). Tutti i metodi sono riservati
 * all'amministratore: @PreAuthorize sulla classe vale per ognuno di essi.
 */
@RestController
@RequestMapping("/api/utenti")
@PreAuthorize("hasRole('AMMINISTRATORE')")
@RequiredArgsConstructor
public class UtenteController {

    private final AmministratoreService amministratoreService;
    private final UtenteService utenteService;
    private final UtenteNormaleService utenteNormaleService;

    // Nuovo utente. Email già in uso -> 400.
    @PostMapping
    public ResponseEntity<UtenteDTO> creaUtente(@Valid @RequestBody CreaUtenteRequest richiesta) {
        Utente utente = amministratoreService.creaUtente(richiesta.nome(), richiesta.cognome(), richiesta.email(),
                richiesta.password(), richiesta.ruolo());
        return ResponseEntity.status(HttpStatus.CREATED).body(UtenteDTO.da(utente));
    }

    // Tutti gli utenti, in ordine cognome-nome.
    @GetMapping
    public List<UtenteDTO> elencaUtenti() {
        // Soluzione proposta da SonarQube
        return utenteService.findAll().stream()
                .map(UtenteDTO::da)
                .toList();
    }

    // Lista di utenti normali dal meno carico al più carico, ciascuno con il numero di bug aperti assegnati.
    // L'ordine lo dà già la query di suggerisciAssegnatari, qui si aggiunge solo il conteggio.
    @GetMapping("/suggeriti")
    public List<AssegnatarioSuggeritoDTO> suggerisciAssegnatari() {
        List<AssegnatarioSuggeritoDTO> risposta = new ArrayList<>();
        for (UtenteNormale utente : amministratoreService.suggerisciAssegnatari()) {
            long bugAperti = utenteNormaleService.countBugAssegnatiAperti(utente.getId());
            risposta.add(AssegnatarioSuggeritoDTO.da(utente, bugAperti));
        }
        return risposta;
    }
}

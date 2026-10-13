package it.wat.progettoingsoft.controller;

import it.wat.progettoingsoft.dto.IssueDTO;
import it.wat.progettoingsoft.entities.Bug;
import it.wat.progettoingsoft.entities.UtenteNormale;
import it.wat.progettoingsoft.security.UtenteAutenticato;
import it.wat.progettoingsoft.service.AmministratoreService;
import it.wat.progettoingsoft.service.BugService;
import it.wat.progettoingsoft.service.UtenteNormaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Bug: assegnazione e duplicato (solo amministratore), bug assegnati all'utente loggato (solo utenti
 * normali, gli unici a cui si assegnano bug). docs/ContrattoApi.md, sezione Bug.
 * Un id di un'issue che non è un bug dà 404 (BugService.findById).
 */
@RestController
@RequestMapping("/api/bugs")
@RequiredArgsConstructor
public class BugController {

    private final BugService bugService;
    private final AmministratoreService amministratoreService;
    private final UtenteNormaleService utenteNormaleService;

    // Req. 4: utenteId deve essere un utente normale (altrimenti 404). Bug chiuso → 409.
    @PostMapping("/{id}/assegna")
    @PreAuthorize("hasRole('AMMINISTRATORE')")
    public IssueDTO assegna(@PathVariable Integer id, @RequestParam Integer utenteId) {
        Bug bug = bugService.findById(id);
        UtenteNormale utente = utenteNormaleService.findById(utenteId);
        amministratoreService.assegnaBug(bug, utente);
        return IssueDTO.da(bug);
    }

    // Req. 16: segna il bug come duplicato e lo chiude. Già duplicato → 409.
    @PostMapping("/{id}/duplicato")
    @PreAuthorize("hasRole('AMMINISTRATORE')")
    public IssueDTO duplicato(@PathVariable Integer id) {
        Bug bug = bugService.findById(id);
        amministratoreService.segnaDuplicato(bug);
        return IssueDTO.da(bug);
    }

    // Req. 4: tutti i bug assegnati, anche chiusi (prima i TODO, poi i più recenti).
    @GetMapping("/assegnati")
    @PreAuthorize("hasRole('NORMALE')")
    public List<IssueDTO> assegnati(@AuthenticationPrincipal UtenteAutenticato utente) {
        return aDto(utenteNormaleService.getBugAssegnati(utente.getId()));
    }

    // Solo i bug assegnati ancora aperti (TODO), più recenti prima.
    @GetMapping("/assegnati/aperti")
    @PreAuthorize("hasRole('NORMALE')")
    public List<IssueDTO> assegnatiAperti(@AuthenticationPrincipal UtenteAutenticato utente) {
        return aDto(utenteNormaleService.getBugAssegnatiAperti(utente.getId()));
    }

    private List<IssueDTO> aDto(List<Bug> bugs) {
        List<IssueDTO> risposta = new ArrayList<>();
        for (Bug bug : bugs) {
            risposta.add(IssueDTO.da(bug));
        }
        return risposta;
    }
}

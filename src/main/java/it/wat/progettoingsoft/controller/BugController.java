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

import java.util.List;

/**
 * Bug: assegnazione e contrassegna duplicato (solo amministratore), bug assegnati all'utente loggato (solo utenti
 * normali, gli unici a cui si assegnano i bug).
 */
@RestController
@RequestMapping("/api/bugs")
@RequiredArgsConstructor
public class BugController {

    private final BugService bugService;
    private final AmministratoreService amministratoreService;
    private final UtenteNormaleService utenteNormaleService;

    //utenteId deve essere un utente normale (altrimenti 404). Bug chiuso -> 409.
    @PostMapping("/{id}/assegna")
    @PreAuthorize("hasRole('AMMINISTRATORE')")
    public IssueDTO assegna(@PathVariable Integer id, @RequestParam Integer utenteId) {
        Bug bug = bugService.findById(id);
        UtenteNormale utente = utenteNormaleService.findById(utenteId);
        amministratoreService.assegnaBug(bug, utente);
        return IssueDTO.da(bug);
    }

    //segna il bug come duplicato e lo chiude, senza cancellarlo. Già duplicato -> 409.
    @PostMapping("/{id}/duplicato")
    @PreAuthorize("hasRole('AMMINISTRATORE')")
    public IssueDTO duplicato(@PathVariable Integer id) {
        Bug bug = bugService.findById(id);
        amministratoreService.segnaDuplicato(bug);
        return IssueDTO.da(bug);
    }

    //restituisce tutti i bug assegnati, anche chiusi (prima gli aperti, poi i più recenti).
    @GetMapping("/assegnati")
    @PreAuthorize("hasRole('NORMALE')")
    public List<IssueDTO> assegnati(@AuthenticationPrincipal UtenteAutenticato utente) {
        return aDto(utenteNormaleService.getBugAssegnati(utente.getId()));
    }

    //restituisce solo i bug assegnati ancora aperti, prima i più recenti.
    @GetMapping("/assegnati/aperti")
    @PreAuthorize("hasRole('NORMALE')")
    public List<IssueDTO> assegnatiAperti(@AuthenticationPrincipal UtenteAutenticato utente) {
        return aDto(utenteNormaleService.getBugAssegnatiAperti(utente.getId()));
    }

    //per creare il DTO
    private List<IssueDTO> aDto(List<Bug> bugs) {
        // Soluzione proposta da SonarQube
        return bugs.stream()
                .map(IssueDTO::da)
                .toList();
    }
}

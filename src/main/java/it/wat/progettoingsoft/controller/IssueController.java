package it.wat.progettoingsoft.controller;

import it.wat.progettoingsoft.costanti.CriterioOrdinamento;
import it.wat.progettoingsoft.costanti.Priorita;
import it.wat.progettoingsoft.costanti.StatoIssue;
import it.wat.progettoingsoft.costanti.TipoIssue;
import it.wat.progettoingsoft.dto.CreaIssueRequest;
import it.wat.progettoingsoft.dto.IssueDTO;
import it.wat.progettoingsoft.entities.Issue;
import it.wat.progettoingsoft.security.UtenteAutenticato;
import it.wat.progettoingsoft.service.AmministratoreService;
import it.wat.progettoingsoft.service.IssueService;
import it.wat.progettoingsoft.service.UtenteInternoService;
import it.wat.progettoingsoft.service.UtenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Issue: vista riepilogativa, ricerca, dettaglio, creazione e chiusura.
 * Lettura per tutti gli utenti loggati, creazione per gli utenti interni,
 * chiusura solo per l'amministratore.
 */
@RestController
@RequestMapping("/api/issues")
@RequiredArgsConstructor
public class IssueController {

    private final UtenteService utenteService;
    private final UtenteInternoService utenteInternoService;
    private final AmministratoreService amministratoreService;
    private final IssueService issueService;

    // Filtri opzionali (assenti = nessun filtro). Se non c'è ordinamento allora più recenti prima.
    @GetMapping
    public List<IssueDTO> visualizzaIssues(@RequestParam(required = false) TipoIssue tipo,
                                           @RequestParam(required = false) StatoIssue stato,
                                           @RequestParam(required = false) Priorita priorita,
                                           @RequestParam(required = false) CriterioOrdinamento ordinamento,
                                           @RequestParam(defaultValue = "false") boolean crescente) {
        return aDto(utenteService.visualizzaIssues(tipo, stato, priorita, ordinamento, crescente));
    }

    // Come la vista riepilogativa, più la parola chiave su titolo e descrizione.
    @GetMapping("/ricerca")
    public List<IssueDTO> cercaIssue(@RequestParam(required = false) String parolaChiave,
                                     @RequestParam(required = false) TipoIssue tipo,
                                     @RequestParam(required = false) StatoIssue stato,
                                     @RequestParam(required = false) Priorita priorita,
                                     @RequestParam(required = false) CriterioOrdinamento ordinamento,
                                     @RequestParam(defaultValue = "false") boolean crescente) {
        return aDto(utenteService.cercaIssue(parolaChiave, tipo, stato, priorita, ordinamento, crescente));
    }

    @GetMapping("/{id}")
    public IssueDTO dettaglio(@PathVariable Integer id) {
        return IssueDTO.da(issueService.findById(id));
    }

    // Byte dell'immagine così come sono stati caricati. Il client la scarica
    // solo quando serve, tipo aprendo il dettaglio, per non sovraccaricare il db.
    @GetMapping("/{id}/immagine")
    public ResponseEntity<byte[]> immagine(@PathVariable Integer id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(issueService.getImmagine(id));
    }

    // Crea issue
    @PostMapping
    @PreAuthorize("hasAnyRole('AMMINISTRATORE','NORMALE')")
    public ResponseEntity<IssueDTO> creaIssue(@AuthenticationPrincipal UtenteAutenticato utente,
                                              @Valid @RequestBody CreaIssueRequest richiesta) {
        Issue issue = utenteInternoService.creaIssue(utente.getId(), richiesta.titolo(), richiesta.descrizione(),
                richiesta.tipo(), richiesta.priorita(), richiesta.immagine());
        return ResponseEntity.status(HttpStatus.CREATED).body(IssueDTO.da(issue));
    }

    // Un'issue già chiusa resta com'è (200, nessun errore).
    @PostMapping("/{id}/chiudi")
    @PreAuthorize("hasRole('AMMINISTRATORE')")
    public IssueDTO chiudiIssue(@PathVariable Integer id) {
        Issue issue = issueService.findById(id);
        amministratoreService.chiudiIssue(issue);
        return IssueDTO.da(issue);
    }

    //Per fare i DTO
    private List<IssueDTO> aDto(List<Issue> issues) {
        // Soluzione proposta da SonarQube
        return issues.stream()
                .map(IssueDTO::da)
                .toList();
    }
}

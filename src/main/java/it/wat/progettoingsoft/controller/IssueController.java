package it.wat.progettoingsoft.controller;

import it.wat.progettoingsoft.costanti.CriterioOrdinamento;
import it.wat.progettoingsoft.costanti.Priorità;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Issue: vista riepilogativa, ricerca, dettaglio, creazione e chiusura (docs/ContrattoApi.md, sezione
 * Issue). Lettura per tutti gli utenti loggati (anche STAKEHOLDER), creazione per gli utenti interni,
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

    // Req. 3: filtri opzionali (assenti = nessun filtro); senza ordinamento, più recenti prima.
    @GetMapping
    public List<IssueDTO> visualizzaIssues(@RequestParam(required = false) TipoIssue tipo,
                                           @RequestParam(required = false) StatoIssue stato,
                                           @RequestParam(required = false) Priorità priorita,
                                           @RequestParam(required = false) CriterioOrdinamento ordinamento,
                                           @RequestParam(defaultValue = "false") boolean crescente) {
        return aDto(utenteService.visualizzaIssues(tipo, stato, priorita, ordinamento, crescente));
    }

    // Req. 11: come la vista riepilogativa, più la parola chiave su titolo e descrizione.
    @GetMapping("/ricerca")
    public List<IssueDTO> cercaIssue(@RequestParam(required = false) String parolaChiave,
                                     @RequestParam(required = false) TipoIssue tipo,
                                     @RequestParam(required = false) StatoIssue stato,
                                     @RequestParam(required = false) Priorità priorita,
                                     @RequestParam(required = false) CriterioOrdinamento ordinamento,
                                     @RequestParam(defaultValue = "false") boolean crescente) {
        return aDto(utenteService.cercaIssue(parolaChiave, tipo, stato, priorita, ordinamento, crescente));
    }

    @GetMapping("/{id}")
    public IssueDTO dettaglio(@PathVariable Integer id) {
        return IssueDTO.da(issueService.findById(id));
    }

    // Byte dell'immagine così come sono stati caricati (niente Base64 né JSON): il client la scarica
    // solo quando serve, ad es. aprendo il dettaglio. Il formato (PNG, JPEG, ...) non è salvato.
    @GetMapping("/{id}/immagine")
    public ResponseEntity<byte[]> immagine(@PathVariable Integer id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(issueService.getImmagine(id));
    }

    // Req. 2: il creatore è l'utente loggato.
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

    private List<IssueDTO> aDto(List<Issue> issues) {
        List<IssueDTO> risposta = new ArrayList<>();
        for (Issue issue : issues) {
            risposta.add(IssueDTO.da(issue));
        }
        return risposta;
    }
}

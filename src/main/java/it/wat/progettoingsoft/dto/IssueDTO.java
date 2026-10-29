package it.wat.progettoingsoft.dto;

import it.wat.progettoingsoft.costanti.Priorita;
import it.wat.progettoingsoft.costanti.StatoIssue;
import it.wat.progettoingsoft.costanti.TipoIssue;
import it.wat.progettoingsoft.entities.Bug;
import it.wat.progettoingsoft.entities.Issue;

import java.time.LocalDateTime;

/**
 * DTO per le Issue ma è valido anche per i Bug.
 * Assegnatario e duplicato sono null se l'issue non è un bug (assegnatario è null anche
 * per un bug non ancora assegnato).
 * L'immagine non c'è, per tenere leggere le liste: haImmagine dice se esiste, e il client la
 * scarica a parte con GET /api/issues/{id}/immagine.
 */
public record IssueDTO(Integer id, String titolo, String descrizione, LocalDateTime data,
                       TipoIssue tipo, StatoIssue stato, Priorita priorita, boolean haImmagine,
                       UtenteDTO utenteCreatore, UtenteDTO assegnatario, Boolean duplicato) {

    public static IssueDTO da(Issue issue) {
        UtenteDTO assegnatario = null;
        Boolean duplicato = null;
        if (issue instanceof Bug bug) { //sistemato da sonarqube, mi toglie "Bug bug = (Bug) issue"
            if (bug.getAssegnatario() != null) {
                assegnatario = UtenteDTO.da(bug.getAssegnatario());
            }
            duplicato = bug.isDuplicato();
        }
        return new IssueDTO(issue.getId(), issue.getTitolo(), issue.getDescrizione(), issue.getData(),
                issue.getTipo(), issue.getStato(), issue.getPriorita(), issue.getImmagine() != null,
                UtenteDTO.da(issue.getUtenteCreatore()), assegnatario, duplicato);
    }
}

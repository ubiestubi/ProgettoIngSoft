package it.wat.progettoingsoft.dto;

import it.wat.progettoingsoft.costanti.Priorità;
import it.wat.progettoingsoft.costanti.StatoIssue;
import it.wat.progettoingsoft.costanti.TipoIssue;
import it.wat.progettoingsoft.entities.Bug;
import it.wat.progettoingsoft.entities.Issue;

import java.time.LocalDateTime;

/**
 * Issue nel JSON, anche per i Bug: assegnatario e duplicato sono null se l'issue
 * non è un bug (assegnatario è null anche per un bug non ancora assegnato).
 * Chiave "priorita" senza accento, per non creare problemi di encoding al client.
 * L'immagine non c'è, per tenere leggere le liste: haImmagine dice se esiste, e il client la
 * scarica a parte con GET /api/issues/{id}/immagine.
 */
public record IssueDTO(Integer id, String titolo, String descrizione, LocalDateTime data,
                       TipoIssue tipo, StatoIssue stato, Priorità priorita, boolean haImmagine,
                       UtenteDTO utenteCreatore, UtenteDTO assegnatario, Boolean duplicato) {

    public static IssueDTO da(Issue issue) {
        UtenteDTO assegnatario = null;
        Boolean duplicato = null;
        if (issue instanceof Bug) {
            Bug bug = (Bug) issue;
            if (bug.getAssegnatario() != null) {
                assegnatario = UtenteDTO.da(bug.getAssegnatario());
            }
            duplicato = bug.isDuplicato();
        }
        return new IssueDTO(issue.getId(), issue.getTitolo(), issue.getDescrizione(), issue.getData(),
                issue.getTipo(), issue.getStato(), issue.getPriorità(), issue.getImmagine() != null,
                UtenteDTO.da(issue.getUtenteCreatore()), assegnatario, duplicato);
    }
}

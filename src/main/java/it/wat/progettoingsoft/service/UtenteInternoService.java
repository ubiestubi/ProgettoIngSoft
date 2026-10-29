package it.wat.progettoingsoft.service;

import it.wat.progettoingsoft.entities.Bug;
import it.wat.progettoingsoft.entities.Issue;
import it.wat.progettoingsoft.costanti.Priorita;
import it.wat.progettoingsoft.costanti.StatoIssue;
import it.wat.progettoingsoft.costanti.TipoIssue;
import it.wat.progettoingsoft.entities.UtenteInterno;
import it.wat.progettoingsoft.repository.IssueRepository;
import it.wat.progettoingsoft.repository.UtenteInternoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UtenteInternoService {

    private final IssueRepository issueRepository;
    private final UtenteInternoRepository utenteInternoRepository;

    /**
     * UtenteInterno.creaIssue(): crea una Issue, o un Bug se tipo == BUG:
     * Bug estende Issue, quindi si salva con lo stesso IssueRepository.
     * Priorità e immagine sono opzionali: possono essere passate come null.
     * idCreatore è l'id dell'utente loggato; deve essere un UtenteInterno.
     */
    public Issue creaIssue(Integer idCreatore, String titolo, String descrizione,
                            TipoIssue tipo, Priorita priorita, byte[] immagine) {
        Optional<UtenteInterno> trovato = utenteInternoRepository.findById(idCreatore);
        if (trovato.isEmpty()) {
            throw new EntityNotFoundException("Utente interno non trovato: " + idCreatore);
        }
        UtenteInterno creatore = trovato.get();

        Issue issue;
        if (tipo == TipoIssue.BUG) {
            issue = new Bug();
        } else {
            issue = new Issue();
        }
        issue.setUtenteCreatore(creatore);
        issue.setTitolo(titolo);
        issue.setDescrizione(descrizione);
        issue.setTipo(tipo);
        //le issue create sono inizialmente nello stato TODO.
        issue.setStato(StatoIssue.TODO);
        //la priorità è opzionale, se non indicata vale NONE.
        if (priorita == null) {
            priorita = Priorita.NONE;
        }
        issue.setPriorita(priorita);
        issue.setImmagine(immagine);
        issue.setData(LocalDateTime.now(ZoneId.of("Europe/Rome")));
        return issueRepository.save(issue);
    }
}

package it.wat.progettoingsoft.service;

import it.wat.progettoingsoft.costanti.StatoIssue;
import it.wat.progettoingsoft.entities.Bug;
import it.wat.progettoingsoft.entities.Notifica;
import it.wat.progettoingsoft.entities.UtenteNormale;
import it.wat.progettoingsoft.repository.BugRepository;
import it.wat.progettoingsoft.repository.NotificaRepository;
import it.wat.progettoingsoft.repository.UtenteNormaleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UtenteNormaleService {

    private final NotificaRepository notificaRepository;
    private final BugRepository bugRepository;
    private final UtenteNormaleRepository utenteNormaleRepository;

    // Un id di amministratore o stakeholder non è un UtenteNormale: dà lo stesso errore di un id inesistente.
    public UtenteNormale findById(Integer id) {
        Optional<UtenteNormale> trovato = utenteNormaleRepository.findById(id);
        if (trovato.isEmpty()) {
            throw new EntityNotFoundException("Utente normale non trovato: " + id);
        }
        return trovato.get();
    }

    /**
     * UtenteNormale.getAllNotifiche(): tutte le notifiche dell'utente, lette e non,
     * più recenti prima.
     */
    public List<Notifica> getAllNotifiche(Integer idUtente) {
        return notificaRepository.findByDestinatarioIdOrderByDataDescIdDesc(idUtente);
    }

    /**
     * UtenteNormale.getNewNotificheAndSegnaComeLette(): legge le notifiche non
     * lette e le segna come lette. Le notifiche restituite hanno già letta = true,
     * come nel DB; più recenti prima.
     */
    @Transactional
    public List<Notifica> getNewNotificheAndSegnaComeLette(Integer idUtente) {
        List<Notifica> nuove = notificaRepository.findByDestinatarioIdAndLettaFalseOrderByDataDescIdDesc(idUtente);
        for (Notifica notifica : nuove) {
            notifica.setLetta(true);
        }
        return notificaRepository.saveAll(nuove);
    }

    /**
     * Numero di notifiche non lette, senza segnarle come lette (ex. per il badge
     * del front-end).
     */
    public long countNewNotifiche(Integer idUtente) {
        return notificaRepository.countByDestinatarioIdAndLettaFalse(idUtente);
    }

    /**
     * Numero di bug assegnati ancora aperti (TODO): serve per il carico corrente mostrato
     * nei suggerimenti di assegnazione.
     */
    public long countBugAssegnatiAperti(Integer idUtente) {
        return bugRepository.countByAssegnatarioIdAndStato(idUtente, StatoIssue.TODO);
    }

    /**
     * UtenteNormale.getBugAssegnati(): i bug assegnati all'utente compresi
     * quelli chiusi. Prima i TODO poi i CHIUSO, e in ogni gruppo i più recenti prima.
     */
    public List<Bug> getBugAssegnati(Integer idUtente) {
        return bugRepository.findByAssegnatarioIdOrderByStatoAscDataDescIdDesc(idUtente);
    }

    /**
     * Solo i bug assegnati ancora aperti (TODO), più recenti prima.
     */
    public List<Bug> getBugAssegnatiAperti(Integer idUtente) {
        return bugRepository.findByAssegnatarioIdAndStatoOrderByDataDescIdDesc(idUtente, StatoIssue.TODO);
    }
}

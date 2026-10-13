package it.wat.progettoingsoft.service;

import it.wat.progettoingsoft.costanti.CriterioOrdinamento;
import it.wat.progettoingsoft.entities.Issue;
import it.wat.progettoingsoft.costanti.Priorità;
import it.wat.progettoingsoft.costanti.StatoIssue;
import it.wat.progettoingsoft.costanti.TipoIssue;
import it.wat.progettoingsoft.entities.Utente;
import it.wat.progettoingsoft.repository.IssueRepository;
import it.wat.progettoingsoft.repository.UtenteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UtenteService {

    private final UtenteRepository utenteRepository;
    private final IssueRepository issueRepository;

    /**
     * Utente.visualizzaIssues(): vista riepilogativa con filtro e ordinamento
     * opzionali (req. 3). Parametri null = nessun filtro su quel criterio;
     * criterio null = più recenti prima.
     */
    public List<Issue> visualizzaIssues(TipoIssue tipo, StatoIssue stato, Priorità priorità,
                                        CriterioOrdinamento criterio, boolean crescente) {
        return cercaOrdinate(tipo, stato, priorità, null, criterio, crescente);
    }

    /**
     * Utente.cercaIssue(): ricerca per parola chiave su titolo e descrizione, su
     * tutti i tipi di issue (req. 11), con gli stessi filtri e ordinamento di
     * visualizzaIssues. Parola chiave null o vuota = nessuna ricerca, restituisce
     * tutte le issue che passano i filtri.
     */
    public List<Issue> cercaIssue(String parolaChiave, TipoIssue tipo, StatoIssue stato,
                                  Priorità priorità, CriterioOrdinamento criterio, boolean crescente) {
        String chiave = null;
        if (parolaChiave != null && !parolaChiave.isBlank()) {
            chiave = parolaChiave.trim();
        }
        return cercaOrdinate(tipo, stato, priorità, chiave, criterio, crescente);
    }

    /**
     * Sceglie la query di IssueRepository in base al criterio e alla direzione:
     * l'ordinamento lo fa il DB. Criterio null = più recenti prima.
     */
    private List<Issue> cercaOrdinate(TipoIssue tipo, StatoIssue stato, Priorità priorità, String chiave,
                                      CriterioOrdinamento criterio, boolean crescente) {
        if (criterio == null) {
            return issueRepository.cercaPerDataDecrescente(tipo, stato, priorità, chiave);
        }

        switch (criterio) {
            case DATA -> {
                if (crescente) {
                    return issueRepository.cercaPerDataCrescente(tipo, stato, priorità, chiave);
                }
                return issueRepository.cercaPerDataDecrescente(tipo, stato, priorità, chiave);
            }
            case PRIORITA -> {
                if (crescente) {
                    return issueRepository.cercaPerPrioritàCrescente(tipo, stato, priorità, chiave);
                }
                return issueRepository.cercaPerPrioritàDecrescente(tipo, stato, priorità, chiave);
            }
            case TITOLO -> {
                if (crescente) {
                    return issueRepository.cercaPerTitoloCrescente(tipo, stato, priorità, chiave);
                }
                return issueRepository.cercaPerTitoloDecrescente(tipo, stato, priorità, chiave);
            }
            case STATO -> {
                if (crescente) {
                    return issueRepository.cercaPerStatoCrescente(tipo, stato, priorità, chiave);
                }
                return issueRepository.cercaPerStatoDecrescente(tipo, stato, priorità, chiave);
            }
            case TIPO -> {
                if (crescente) {
                    return issueRepository.cercaPerTipoCrescente(tipo, stato, priorità, chiave);
                }
                return issueRepository.cercaPerTipoDecrescente(tipo, stato, priorità, chiave);
            }
            default -> throw new IllegalArgumentException("Criterio non gestito: " + criterio);
        }
    }

    /**
     * Niente normalizzazione: la colonna Utenti.email usa la collation predefinita di MySQL 8
     * (case-insensitive), quindi "Mario@X.it" trova già l'utente salvato come "mario@x.it".
     */
    public Optional<Utente> findByEmail(String email) {
        return utenteRepository.findByEmail(email);
    }

    // Ordinati per cognome, nome, email.
    public List<Utente> findAll() {
        return utenteRepository.findAllByOrderByCognomeAscNomeAscEmailAsc();
    }

    public Utente findById(Integer id) {
        Optional<Utente> trovato = utenteRepository.findById(id);
        if (trovato.isEmpty()) {
            throw new EntityNotFoundException("Utente non trovato: " + id);
        }
        return trovato.get();
    }
}

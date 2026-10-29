package it.wat.progettoingsoft.service;

import it.wat.progettoingsoft.costanti.CriterioOrdinamento;
import it.wat.progettoingsoft.entities.Issue;
import it.wat.progettoingsoft.costanti.Priorita;
import it.wat.progettoingsoft.costanti.StatoIssue;
import it.wat.progettoingsoft.costanti.TipoIssue;
import it.wat.progettoingsoft.entities.Utente;
import it.wat.progettoingsoft.repository.IssueRepository;
import it.wat.progettoingsoft.repository.UtenteRepository;
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
    public List<Issue> visualizzaIssues(TipoIssue tipo, StatoIssue stato, Priorita priorita,
                                        CriterioOrdinamento criterio, boolean crescente) {
        return cercaOrdinate(tipo, stato, priorita, null, criterio, crescente);
    }

    /**
     * Utente.cercaIssue(): ricerca per parola chiave su titolo e descrizione, su
     * tutti i tipi di issue (req. 11), con gli stessi filtri e ordinamento di
     * visualizzaIssues. Parola chiave null o vuota = nessuna ricerca, restituisce
     * tutte le issue che passano i filtri.
     */
    public List<Issue> cercaIssue(String parolaChiave, TipoIssue tipo, StatoIssue stato,
                                  Priorita priorita, CriterioOrdinamento criterio, boolean crescente) {
        String chiave = null;
        if (parolaChiave != null && !parolaChiave.isBlank()) {
            chiave = parolaChiave.trim();
        }
        return cercaOrdinate(tipo, stato, priorita, chiave, criterio, crescente);
    }

    /**
     * Sceglie la query di IssueRepository in base al criterio e alla direzione:
     * l'ordinamento lo fa il DB. Criterio null = più recenti prima.
     */
    private List<Issue> cercaOrdinate(TipoIssue tipo, StatoIssue stato, Priorita priorita, String chiave,
                                      CriterioOrdinamento criterio, boolean crescente) {
        if (criterio == null) {
            return issueRepository.cercaPerDataDecrescente(tipo, stato, priorita, chiave);
        }

        switch (criterio) {
            case DATA -> {
                if (crescente) {
                    return issueRepository.cercaPerDataCrescente(tipo, stato, priorita, chiave);
                }
                return issueRepository.cercaPerDataDecrescente(tipo, stato, priorita, chiave);
            }
            case PRIORITA -> {
                if (crescente) {
                    return issueRepository.cercaPerPrioritaCrescente(tipo, stato, priorita, chiave);
                }
                return issueRepository.cercaPerPrioritaDecrescente(tipo, stato, priorita, chiave);
            }
            case TITOLO -> {
                if (crescente) {
                    return issueRepository.cercaPerTitoloCrescente(tipo, stato, priorita, chiave);
                }
                return issueRepository.cercaPerTitoloDecrescente(tipo, stato, priorita, chiave);
            }
            case STATO -> {
                if (crescente) {
                    return issueRepository.cercaPerStatoCrescente(tipo, stato, priorita, chiave);
                }
                return issueRepository.cercaPerStatoDecrescente(tipo, stato, priorita, chiave);
            }
            case TIPO -> {
                if (crescente) {
                    return issueRepository.cercaPerTipoCrescente(tipo, stato, priorita, chiave);
                }
                return issueRepository.cercaPerTipoDecrescente(tipo, stato, priorita, chiave);
            }
            default -> throw new IllegalArgumentException("Criterio non gestito: " + criterio);
        }
    }

    /**
     * Niente normalizzazione: lsiccome siamo in MySQL se cerco "Mario@X.it" trovo già l'utente salvato come "mario@x.it".
     */
    public Optional<Utente> findByEmail(String email) {
        return utenteRepository.findByEmail(email);
    }

    // Ordinati per cognome, nome, email.
    public List<Utente> findAll() {
        return utenteRepository.findAllByOrderByCognomeAscNomeAscEmailAsc();
    }


}

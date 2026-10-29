package it.wat.progettoingsoft.service;

import it.wat.progettoingsoft.entities.Amministratore;
import it.wat.progettoingsoft.entities.Bug;
import it.wat.progettoingsoft.entities.Issue;
import it.wat.progettoingsoft.costanti.Ruolo;
import it.wat.progettoingsoft.costanti.StatoIssue;
import it.wat.progettoingsoft.entities.Utente;
import it.wat.progettoingsoft.entities.UtenteEsterno;
import it.wat.progettoingsoft.entities.UtenteNormale;
import it.wat.progettoingsoft.repository.BugRepository;
import it.wat.progettoingsoft.repository.IssueRepository;
import it.wat.progettoingsoft.repository.UtenteNormaleRepository;
import it.wat.progettoingsoft.repository.UtenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AmministratoreService {

    private final UtenteRepository utenteRepository;
    private final UtenteNormaleRepository utenteNormaleRepository;
    private final BugRepository bugRepository;
    private final IssueRepository issueRepository;
    private final NotificaService notificaService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Amministratore.creaUtente(email, password, ruolo): crea una nuova utenza del tipo indicato.
     * Nome e cognome vengono salvati senza spazi iniziali/finali, l'email in minuscolo, e deve essere univoca;
     * la password viene salvata come hash BCrypt.
     * Campi obbligatori, infine il formato dell'email lo controlla CreaUtenteRequest (con Jakarta Validation).
     */
    public Utente creaUtente(String nome, String cognome, String email, String password, Ruolo ruolo) {
        String emailNormalizzata = email.toLowerCase();
        if (utenteRepository.findByEmail(emailNormalizzata).isPresent()) {
            throw new IllegalArgumentException("Email già in uso: " + emailNormalizzata);
        }

        Utente utente;
        switch (ruolo) {
            case AMMINISTRATORE -> utente = new Amministratore();
            case NORMALE -> utente = new UtenteNormale();
            case STAKEHOLDER -> utente = new UtenteEsterno();
            default -> throw new IllegalArgumentException("Ruolo non gestito: " + ruolo);
        }
        utente.setNome(nome.trim());
        utente.setCognome(cognome.trim());
        utente.setEmail(emailNormalizzata);
        utente.setPassword(passwordEncoder.encode(password));
        return utenteRepository.save(utente);
    }

    /**
     * Amministratore.assegnaBug(bug, utente): assegna il bug a un utente normale
     * e lo notifica. Un bug CHIUSO non si può assegnare. Se il bug era già
     * assegnato a un altro utente viene riassegnato e la notifica arriva a entrambi ma
     * se era già assegnato allo stesso utente non cambia nulla.
     */
    @Transactional
    public void assegnaBug(Bug bug, UtenteNormale utente) {
        if (bug.getStato() == StatoIssue.CHIUSO) {
            throw new IllegalStateException("Il bug #" + bug.getId() + " è chiuso e non può essere assegnato");
        }

        UtenteNormale vecchioAssegnatario = bug.getAssegnatario();
        // Confronto per id: le entità non ridefiniscono equals e l'assegnatario può essere un proxy lazy.
        if (vecchioAssegnatario != null && vecchioAssegnatario.getId().equals(utente.getId())) {
            return;
        }

        bug.setAssegnatario(utente);
        bugRepository.save(bug);

        String riferimentoBug = "il bug #" + bug.getId() + ": " + bug.getTitolo();
        notificaService.creaNotifica(utente, "Ti è stato assegnato " + riferimentoBug);
        if (vecchioAssegnatario != null) {
            notificaService.creaNotifica(vecchioAssegnatario, "Non ti è più assegnato " + riferimentoBug);
        }
    }

    /**
     * Amministratore.segnaDuplicato(bug): contrassegna il bug come duplicato e lo
     * chiude immediatamente, senza cancellarlo. Un bug già segnato come duplicato non si può
     * segnare di nuovo ma un bug chiuso perché risolto sì. L'assegnatario resta, e se
     * c'è riceve una notifica.
     */
    @Transactional
    public void segnaDuplicato(Bug bug) {
        if (bug.isDuplicato()) {
            throw new IllegalStateException("Il bug #" + bug.getId() + " è già segnato come duplicato");
        }

        bug.setDuplicato(true);
        bug.setStato(StatoIssue.CHIUSO);
        bugRepository.save(bug);

        UtenteNormale assegnatario = bug.getAssegnatario();
        if (assegnatario != null) {
            notificaService.creaNotifica(assegnatario,
                    "Il bug #" + bug.getId() + ": " + bug.getTitolo() + " è stato segnato come duplicato e chiuso");
        }
    }

    /**
     * chiudiIssue(issue): porta l'issue a CHIUSO. Operazione riservata all'amministratore.
     * Un'issue già chiusa resta com'è (nessuna notifica). Se è un bug con un assegnatario, l'assegnatario
     * riceve una notifica.
     */
    @Transactional
    public void chiudiIssue(Issue issue) {
        if (issue.getStato() == StatoIssue.CHIUSO) {
            return;
        }

        issue.setStato(StatoIssue.CHIUSO);
        issueRepository.save(issue);

        if (issue instanceof Bug bug) { //sistemato da sonarqube, mi toglie "Bug bug = (Bug) issue"
            UtenteNormale assegnatario = bug.getAssegnatario();
            if (assegnatario != null) {
                notificaService.creaNotifica(assegnatario,
                        "Il bug #" + bug.getId() + ": " + bug.getTitolo() + " è stato chiuso");
            }
        }
    }

    /**
     * Amministratore.suggerisciAssegnatari(): utenti normali ordinati per carico
     * di lavoro corrente crescente. Il carico è il numero di bug TODO (aka aperti)
     * assegnati, e a parità di carico si ordina per email.
     */
    public List<UtenteNormale> suggerisciAssegnatari() {
        return utenteNormaleRepository.findAllOrdinatiPerCarico(StatoIssue.TODO);
    }
}

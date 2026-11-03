package it.wat.progettoingsoft.service;

import it.wat.progettoingsoft.costanti.Priorita;
import it.wat.progettoingsoft.costanti.StatoIssue;
import it.wat.progettoingsoft.costanti.TipoIssue;
import it.wat.progettoingsoft.entities.Bug;
import it.wat.progettoingsoft.entities.UtenteNormale;
import it.wat.progettoingsoft.repository.IssueRepository;
import it.wat.progettoingsoft.repository.UtenteInternoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

/**
 * Test di unità di UtenteInternoService.creaIssue(idCreatore, titolo, descrizione, tipo, priorita, immagine).
 * I repository sono mock creati con Mockito.
 */
class UtenteInternoServiceTest {

    private final Integer idUtenteCreatore = 7;

    private IssueRepository issueRepository;
    private UtenteInternoRepository utenteInternoRepository;
    private UtenteInternoService utenteInternoService;
    private UtenteNormale utenteCreatore;

    // Setup: crea i mock per le repository e la classe contenente creaIssue, infine crea un utente con id=7
    @BeforeEach
    void setUp() {
        issueRepository = mock(IssueRepository.class);
        utenteInternoRepository = mock(UtenteInternoRepository.class);
        utenteInternoService = new UtenteInternoService(issueRepository, utenteInternoRepository);

        utenteCreatore = new UtenteNormale();
        utenteCreatore.setId(idUtenteCreatore);
        when(utenteInternoRepository.findById(idUtenteCreatore)).thenReturn(Optional.of(utenteCreatore));
    }

    // CI1 - Utente creatore inesistente: la repository non lo trova e lancia l'eccezione
    @Test
    void creaIssue_creatoreNonTrovato_lanciaEccezione() {
        // precondizione: l'id 99 non corrisponde a nessun utente
        when(utenteInternoRepository.findById(99)).thenReturn(Optional.empty());

        // si lancia l'eccezione con questo id
        assertThrows(EntityNotFoundException.class, () ->
                        utenteInternoService.creaIssue(99, "Titolo", "Descrizione",
                                TipoIssue.BUG, Priorita.ALTA, null));

        // controlla che nessuna issue è stata salvata
        verify(issueRepository, times(0)).save(any());
    }

    // CI2 - Issue tipo BUG: viene creato un Bug, non assegnato e non duplicato.
    @Test
    void creaIssue_tipoBug_creaBug() {

        utenteInternoService.creaIssue(idUtenteCreatore, "Login non funziona", "Errore 500 al login",
                TipoIssue.BUG, Priorita.ALTA, null);

        // assert
        verify(issueRepository).save(any(Bug.class));
        verify(issueRepository).save(argThat(allOf(
                hasProperty("tipo", equalTo(TipoIssue.BUG)),
                hasProperty("assegnatario", is(nullValue())),
                hasProperty("duplicato", is(false)))));
    }

    // CI3 — Issue tipo QUESTION
    @Test
    void creaIssue_tipoQuestion() {

        utenteInternoService.creaIssue(idUtenteCreatore, "Avete provato la nuova funzione dell'IDE?",
                "Vorrei qualche delucidazione in merito",
                TipoIssue.QUESTION, Priorita.BASSA, null);

        // assert: salvata un'issue di tipo QUESTION
        verify(issueRepository).save(argThat(hasProperty("tipo", equalTo(TipoIssue.QUESTION))));
    }

    // CI4 - Issue tipo DOCUMENTATION
    @Test
    void creaIssue_tipoDocumentation() {

        utenteInternoService.creaIssue(idUtenteCreatore, "Cosa restituisce questo endpoint?",
                "Manca una documentazione adeguata",
                TipoIssue.DOCUMENTATION, Priorita.MEDIA, null);

        // assert
        verify(issueRepository).save(argThat(hasProperty("tipo", equalTo(TipoIssue.DOCUMENTATION))));
    }

    // CI5 - Issue tipo FEATURE
    @Test
    void creaIssue_tipoFeature() {

        utenteInternoService.creaIssue(idUtenteCreatore, "Dimenticato di implementare la crittografia dei dati",
                "Feature importante richiesta dal cliente",
                TipoIssue.FEATURE, Priorita.ALTA, null);

        // assert
        verify(issueRepository).save(argThat(hasProperty("tipo", equalTo(TipoIssue.FEATURE))));
    }

    // CI6 - Issue con priorità non indicata: vale NONE.
    @Test
    void creaIssue_prioritaNull() {

        utenteInternoService.creaIssue(idUtenteCreatore, "Login non funziona", "Errore 500 al login",
                TipoIssue.FEATURE, null, null);

        // assert
        verify(issueRepository).save(argThat(hasProperty("priorita", equalTo(Priorita.NONE))));
    }

    // CI7 - Issue con priorità indicata
    @Test
    void creaIssue_prioritaIndicata() {
        utenteInternoService.creaIssue(idUtenteCreatore, "Login non funziona", "Errore 500 al login",
                TipoIssue.FEATURE, Priorita.MEDIA, null);

        // assert
        verify(issueRepository).save(argThat(hasProperty("priorita", equalTo(Priorita.MEDIA))));
    }

    // CI8 - Regole di servizio valide per ogni issue: imposta data di creazione e stato=TODO
    @Test
    void creaIssue_impostaStatoTodoEData() {

        utenteInternoService.creaIssue(idUtenteCreatore, "Login non funziona", "Errore 500 al login",
                TipoIssue.QUESTION, Priorita.ALTA, null);

        // assert
        verify(issueRepository).save(argThat(allOf(
                hasProperty("stato", equalTo(StatoIssue.TODO)),
                hasProperty("data", is(not(nullValue()))),
                hasProperty("utenteCreatore", equalTo(utenteCreatore)),
                hasProperty("titolo", equalTo("Login non funziona")),
                hasProperty("descrizione", equalTo("Errore 500 al login")),
                hasProperty("immagine", is(nullValue())))));
    }

    // CI9 - Issue con immagine presente
    @Test
    void creaIssue_conImmagine() {
        byte[] immagine = {1, 2, 3, 4};

        utenteInternoService.creaIssue(idUtenteCreatore, "Login non funziona", "Errore 500 al login",
                TipoIssue.BUG, Priorita.ALTA, immagine);

        // assert
        verify(issueRepository).save(argThat(hasProperty("immagine", equalTo(immagine))));
    }
}

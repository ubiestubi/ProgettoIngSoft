package it.wat.progettoingsoft.repository;

import it.wat.progettoingsoft.costanti.Priorità;
import it.wat.progettoingsoft.costanti.StatoIssue;
import it.wat.progettoingsoft.costanti.TipoIssue;
import it.wat.progettoingsoft.entities.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Vista riepilogativa e ricerca (req. 3 e 11): una query per ogni criterio di ordinamento e direzione.
 * Tutte condividono gli stessi filtri (ISSUE_FILTRATE): ogni parametro null = nessun filtro su quel
 * criterio; la parola chiave cerca su titolo e descrizione senza distinguere maiuscole.
 *
 * PRIORITÀ, STATO e TIPO sono ENUM di MySQL, che li ordina secondo l'ordine di dichiarazione nelle
 * migration, non alfabeticamente: NONE, BASSA, MEDIA, ALTA (V2); TODO, CHIUSO; QUESTION, BUG,
 * DOCUMENTATION, FEATURE (V1). A parità di criterio, più recenti prima.
 * In fondo a ogni ORDER BY c'è l'id: la colonna data è un DATETIME senza frazioni di secondo, quindi due
 * issue create nello stesso secondo avrebbero la stessa data e un ordine casuale; l'id cresce con
 * l'ordine di inserimento.
 */
public interface IssueRepository extends JpaRepository<Issue, Integer> {

    String ISSUE_FILTRATE = "SELECT i FROM Issue i"
            + " WHERE (:tipo IS NULL OR i.tipo = :tipo)"
            + " AND (:stato IS NULL OR i.stato = :stato)"
            + " AND (:priorita IS NULL OR i.priorità = :priorita)"
            + " AND (:parolaChiave IS NULL"
            + "      OR LOWER(i.titolo) LIKE LOWER(CONCAT('%', :parolaChiave, '%'))"
            + "      OR LOWER(i.descrizione) LIKE LOWER(CONCAT('%', :parolaChiave, '%')))";

    // DATA
    @Query(ISSUE_FILTRATE + " ORDER BY i.data ASC, i.id ASC")
    List<Issue> cercaPerDataCrescente(@Param("tipo") TipoIssue tipo,
                                      @Param("stato") StatoIssue stato,
                                      @Param("priorita") Priorità priorità,
                                      @Param("parolaChiave") String parolaChiave);

    @Query(ISSUE_FILTRATE + " ORDER BY i.data DESC, i.id DESC")
    List<Issue> cercaPerDataDecrescente(@Param("tipo") TipoIssue tipo,
                                        @Param("stato") StatoIssue stato,
                                        @Param("priorita") Priorità priorità,
                                        @Param("parolaChiave") String parolaChiave);

    // PRIORITÀ: crescente = da NONE ad ALTA (ordine dell'ENUM dopo V2__ordine_priorita.sql).
    @Query(ISSUE_FILTRATE + " ORDER BY i.priorità ASC, i.data DESC, i.id DESC")
    List<Issue> cercaPerPrioritàCrescente(@Param("tipo") TipoIssue tipo,
                                          @Param("stato") StatoIssue stato,
                                          @Param("priorita") Priorità priorità,
                                          @Param("parolaChiave") String parolaChiave);

    @Query(ISSUE_FILTRATE + " ORDER BY i.priorità DESC, i.data DESC, i.id DESC")
    List<Issue> cercaPerPrioritàDecrescente(@Param("tipo") TipoIssue tipo,
                                            @Param("stato") StatoIssue stato,
                                            @Param("priorita") Priorità priorità,
                                            @Param("parolaChiave") String parolaChiave);

    // TITOLO
    @Query(ISSUE_FILTRATE + " ORDER BY i.titolo ASC, i.data DESC, i.id DESC")
    List<Issue> cercaPerTitoloCrescente(@Param("tipo") TipoIssue tipo,
                                        @Param("stato") StatoIssue stato,
                                        @Param("priorita") Priorità priorità,
                                        @Param("parolaChiave") String parolaChiave);

    @Query(ISSUE_FILTRATE + " ORDER BY i.titolo DESC, i.data DESC, i.id DESC")
    List<Issue> cercaPerTitoloDecrescente(@Param("tipo") TipoIssue tipo,
                                          @Param("stato") StatoIssue stato,
                                          @Param("priorita") Priorità priorità,
                                          @Param("parolaChiave") String parolaChiave);

    // STATO: crescente = TODO prima di CHIUSO.
    @Query(ISSUE_FILTRATE + " ORDER BY i.stato ASC, i.data DESC, i.id DESC")
    List<Issue> cercaPerStatoCrescente(@Param("tipo") TipoIssue tipo,
                                       @Param("stato") StatoIssue stato,
                                       @Param("priorita") Priorità priorità,
                                       @Param("parolaChiave") String parolaChiave);

    @Query(ISSUE_FILTRATE + " ORDER BY i.stato DESC, i.data DESC, i.id DESC")
    List<Issue> cercaPerStatoDecrescente(@Param("tipo") TipoIssue tipo,
                                         @Param("stato") StatoIssue stato,
                                         @Param("priorita") Priorità priorità,
                                         @Param("parolaChiave") String parolaChiave);

    // TIPO: crescente = QUESTION, BUG, DOCUMENTATION, FEATURE.
    @Query(ISSUE_FILTRATE + " ORDER BY i.tipo ASC, i.data DESC, i.id DESC")
    List<Issue> cercaPerTipoCrescente(@Param("tipo") TipoIssue tipo,
                                      @Param("stato") StatoIssue stato,
                                      @Param("priorita") Priorità priorità,
                                      @Param("parolaChiave") String parolaChiave);

    @Query(ISSUE_FILTRATE + " ORDER BY i.tipo DESC, i.data DESC, i.id DESC")
    List<Issue> cercaPerTipoDecrescente(@Param("tipo") TipoIssue tipo,
                                        @Param("stato") StatoIssue stato,
                                        @Param("priorita") Priorità priorità,
                                        @Param("parolaChiave") String parolaChiave);
}

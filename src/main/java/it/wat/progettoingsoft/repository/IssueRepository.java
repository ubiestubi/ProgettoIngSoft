package it.wat.progettoingsoft.repository;

import it.wat.progettoingsoft.costanti.Priorita;
import it.wat.progettoingsoft.costanti.StatoIssue;
import it.wat.progettoingsoft.costanti.TipoIssue;
import it.wat.progettoingsoft.entities.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Vista riepilogativa e ricerca: una query per ogni criterio di ordinamento e direzione.
 * Tutte condividono gli stessi filtri (ISSUE_FILTRATE): ogni parametro null = nessun filtro su quel
 * criterio; la parola chiave cerca su titolo e descrizione senza distinguere maiuscole.
 *
 * PRIORITÀ, STATO e TIPO sono ENUM di MySQL, che li ordina secondo l'ordine di dichiarazione nelle
 * migration, non alfabeticamente: NONE, BASSA, MEDIA, ALTA (V2); TODO, CHIUSO; QUESTION, BUG,
 * DOCUMENTATION, FEATURE (V1). A parità di criterio, più recenti prima.
 */
public interface IssueRepository extends JpaRepository<Issue, Integer> {

    String ISSUE_FILTRATE = "SELECT i FROM Issue i"
            + " WHERE (:tipo IS NULL OR i.tipo = :tipo)"
            + " AND (:stato IS NULL OR i.stato = :stato)"
            + " AND (:priorita IS NULL OR i.priorita = :priorita)"
            + " AND (:parolaChiave IS NULL"
            + "      OR LOWER(i.titolo) LIKE LOWER(CONCAT('%', :parolaChiave, '%'))"
            + "      OR LOWER(i.descrizione) LIKE LOWER(CONCAT('%', :parolaChiave, '%')))";

    // DATA
    @Query(ISSUE_FILTRATE + " ORDER BY i.data ASC, i.id ASC")
    List<Issue> cercaPerDataCrescente(@Param("tipo") TipoIssue tipo,
                                      @Param("stato") StatoIssue stato,
                                      @Param("priorita") Priorita priorita,
                                      @Param("parolaChiave") String parolaChiave);

    @Query(ISSUE_FILTRATE + " ORDER BY i.data DESC, i.id DESC")
    List<Issue> cercaPerDataDecrescente(@Param("tipo") TipoIssue tipo,
                                        @Param("stato") StatoIssue stato,
                                        @Param("priorita") Priorita priorita,
                                        @Param("parolaChiave") String parolaChiave);

    // PRIORITÀ: crescente = da NONE ad ALTA (ordine dell'ENUM, vedi V2__ordine_priorita.sql).
    @Query(ISSUE_FILTRATE + " ORDER BY i.priorita ASC, i.data DESC, i.id DESC")
    List<Issue> cercaPerPrioritaCrescente(@Param("tipo") TipoIssue tipo,
                                          @Param("stato") StatoIssue stato,
                                          @Param("priorita") Priorita priorita,
                                          @Param("parolaChiave") String parolaChiave);

    @Query(ISSUE_FILTRATE + " ORDER BY i.priorita DESC, i.data DESC, i.id DESC")
    List<Issue> cercaPerPrioritaDecrescente(@Param("tipo") TipoIssue tipo,
                                            @Param("stato") StatoIssue stato,
                                            @Param("priorita") Priorita priorita,
                                            @Param("parolaChiave") String parolaChiave);

    // TITOLO
    @Query(ISSUE_FILTRATE + " ORDER BY i.titolo ASC, i.data DESC, i.id DESC")
    List<Issue> cercaPerTitoloCrescente(@Param("tipo") TipoIssue tipo,
                                        @Param("stato") StatoIssue stato,
                                        @Param("priorita") Priorita priorita,
                                        @Param("parolaChiave") String parolaChiave);

    @Query(ISSUE_FILTRATE + " ORDER BY i.titolo DESC, i.data DESC, i.id DESC")
    List<Issue> cercaPerTitoloDecrescente(@Param("tipo") TipoIssue tipo,
                                          @Param("stato") StatoIssue stato,
                                          @Param("priorita") Priorita priorita,
                                          @Param("parolaChiave") String parolaChiave);

    // STATO: crescente = TODO prima di CHIUSO.
    @Query(ISSUE_FILTRATE + " ORDER BY i.stato ASC, i.data DESC, i.id DESC")
    List<Issue> cercaPerStatoCrescente(@Param("tipo") TipoIssue tipo,
                                       @Param("stato") StatoIssue stato,
                                       @Param("priorita") Priorita priorita,
                                       @Param("parolaChiave") String parolaChiave);

    @Query(ISSUE_FILTRATE + " ORDER BY i.stato DESC, i.data DESC, i.id DESC")
    List<Issue> cercaPerStatoDecrescente(@Param("tipo") TipoIssue tipo,
                                         @Param("stato") StatoIssue stato,
                                         @Param("priorita") Priorita priorita,
                                         @Param("parolaChiave") String parolaChiave);

    // TIPO: crescente = QUESTION, BUG, DOCUMENTATION, FEATURE.
    @Query(ISSUE_FILTRATE + " ORDER BY i.tipo ASC, i.data DESC, i.id DESC")
    List<Issue> cercaPerTipoCrescente(@Param("tipo") TipoIssue tipo,
                                      @Param("stato") StatoIssue stato,
                                      @Param("priorita") Priorita priorita,
                                      @Param("parolaChiave") String parolaChiave);

    @Query(ISSUE_FILTRATE + " ORDER BY i.tipo DESC, i.data DESC, i.id DESC")
    List<Issue> cercaPerTipoDecrescente(@Param("tipo") TipoIssue tipo,
                                        @Param("stato") StatoIssue stato,
                                        @Param("priorita") Priorita priorita,
                                        @Param("parolaChiave") String parolaChiave);
}

package it.wat.progettoingsoft.repository;

import it.wat.progettoingsoft.costanti.StatoIssue;
import it.wat.progettoingsoft.entities.UtenteNormale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UtenteNormaleRepository extends JpaRepository<UtenteNormale, Integer> {

    // suggerisciAssegnatari(): tutti gli utenti normali ordinati per numero di bug assegnati
    // nello stato indicato (praticamente con stato = TODO è il carico corrente, dal meno carico al più carico.)
    // A parità di carico l'ordine è per cognome, nome ed email.
    @Query("SELECT u FROM UtenteNormale u"
            + " ORDER BY (SELECT COUNT(b) FROM Bug b WHERE b.assegnatario = u AND b.stato = :stato) ASC,"
            + " u.cognome ASC, u.nome ASC, u.email ASC")
    List<UtenteNormale> findAllOrdinatiPerCarico(@Param("stato") StatoIssue stato);
}

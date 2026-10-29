package it.wat.progettoingsoft.repository;

import it.wat.progettoingsoft.entities.Bug;
import it.wat.progettoingsoft.costanti.StatoIssue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BugRepository extends JpaRepository<Bug, Integer> {

    List<Bug> findByAssegnatarioId(Integer assegnatarioId);

    // getBugAssegnati(): prima gli aperti poi i chiusi (ordine di dichiarazione dell'ENUM stato del db (vedi
    // V1__init.sql), e in ogni gruppo i più recenti prima (a parità di data, id più alto prima).
    List<Bug> findByAssegnatarioIdOrderByStatoAscDataDescIdDesc(Integer assegnatarioId);

    // Bug assegnati in un certo stato (aperto o chiuso), più recenti prima (per il getBugAssegnatiAperti).
    List<Bug> findByAssegnatarioIdAndStatoOrderByDataDescIdDesc(Integer assegnatarioId, StatoIssue stato);

    // Numero di bug assegnati in un certo stato(aperto o chiuso) (per carico corrente nei suggeriti).
    long countByAssegnatarioIdAndStato(Integer assegnatarioId, StatoIssue stato);
}

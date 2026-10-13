package it.wat.progettoingsoft.repository;

import it.wat.progettoingsoft.entities.Bug;
import it.wat.progettoingsoft.costanti.StatoIssue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BugRepository extends JpaRepository<Bug, Integer> {

    List<Bug> findByAssegnatarioId(Integer assegnatarioId);

    // getBugAssegnati(): prima i TODO poi i CHIUSO (ordine di dichiarazione dell'ENUM stato in
    // V1__init.sql), e in ogni gruppo i più recenti prima (a parità di data, id più alto prima).
    List<Bug> findByAssegnatarioIdOrderByStatoAscDataDescIdDesc(Integer assegnatarioId);

    // Bug assegnati in un certo stato, più recenti prima (getBugAssegnatiAperti con TODO).
    List<Bug> findByAssegnatarioIdAndStatoOrderByDataDescIdDesc(Integer assegnatarioId, StatoIssue stato);

    // Numero di bug assegnati in un certo stato (carico corrente nei suggeriti, con TODO).
    long countByAssegnatarioIdAndStato(Integer assegnatarioId, StatoIssue stato);
}

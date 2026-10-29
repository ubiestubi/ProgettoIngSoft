package it.wat.progettoingsoft.repository;

import it.wat.progettoingsoft.entities.Notifica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificaRepository extends JpaRepository<Notifica, Integer> {

    // Più recenti prima, IdDesc a parità di data
    List<Notifica> findByDestinatarioIdOrderByDataDescIdDesc(Integer destinatarioId);

    // Solo le non lette, più recenti prima.
    List<Notifica> findByDestinatarioIdAndLettaFalseOrderByDataDescIdDesc(Integer destinatarioId);

    // Numero di notifiche non lette, senza caricarle.
    long countByDestinatarioIdAndLettaFalse(Integer destinatarioId);
}

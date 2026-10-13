package it.wat.progettoingsoft.repository;

import it.wat.progettoingsoft.entities.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UtenteRepository extends JpaRepository<Utente, Integer> {

    Optional<Utente> findByEmail(String email);

    // Tutti gli utenti per cognome e nome (lista utenti dell'amministratore); l'email, univoca, dà un
    // ordine stabile tra omonimi.
    List<Utente> findAllByOrderByCognomeAscNomeAscEmailAsc();
}

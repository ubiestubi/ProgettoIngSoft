package it.wat.progettoingsoft.repository;

import it.wat.progettoingsoft.entities.UtenteInterno;
import org.springframework.data.jpa.repository.JpaRepository;

// Amministratori e utenti normali (per creaIssue: solo un UtenteInterno può essere creatore di un'issue).
public interface UtenteInternoRepository extends JpaRepository<UtenteInterno, Integer> {
}

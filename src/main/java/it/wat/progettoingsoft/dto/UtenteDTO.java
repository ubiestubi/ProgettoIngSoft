package it.wat.progettoingsoft.dto;

import it.wat.progettoingsoft.costanti.Ruolo;
import it.wat.progettoingsoft.entities.Utente;
import it.wat.progettoingsoft.security.UtenteAutenticato;

/**
 * Utente nel JSON: senza password. Il ruolo lo dà la sottoclasse (Utente.getRuolo()).
 */
public record UtenteDTO(Integer id, String nome, String cognome, String email, Ruolo ruolo) {

    public static UtenteDTO da(Utente utente) {
        return new UtenteDTO(utente.getId(), utente.getNome(), utente.getCognome(), utente.getEmail(),
                utente.getRuolo());
    }

    // Utente loggato (login, /me): i dati sono già nella sessione, senza rileggerli dal DB.
    public static UtenteDTO da(UtenteAutenticato utente) {
        return new UtenteDTO(utente.getId(), utente.getNome(), utente.getCognome(), utente.getEmail(),
                utente.getRuolo());
    }
}

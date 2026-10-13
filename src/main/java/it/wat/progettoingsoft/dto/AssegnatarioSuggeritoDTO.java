package it.wat.progettoingsoft.dto;

import it.wat.progettoingsoft.entities.UtenteNormale;

/**
 * Un utente normale suggerito per l'assegnazione di un bug (req. 14), con il suo carico corrente:
 * il numero di bug TODO che ha già assegnati.
 */
public record AssegnatarioSuggeritoDTO(Integer id, String nome, String cognome, String email, long bugAperti) {

    public static AssegnatarioSuggeritoDTO da(UtenteNormale utente, long bugAperti) {
        return new AssegnatarioSuggeritoDTO(utente.getId(), utente.getNome(), utente.getCognome(), utente.getEmail(),
                bugAperti);
    }
}

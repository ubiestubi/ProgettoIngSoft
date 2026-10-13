package it.wat.progettoingsoft.dto;

import it.wat.progettoingsoft.entities.Notifica;

import java.time.LocalDateTime;

/**
 * Notifica nel JSON, senza destinatario: è sempre l'utente loggato.
 */
public record NotificaDTO(Integer id, String testo, LocalDateTime data, boolean letta) {

    public static NotificaDTO da(Notifica notifica) {
        return new NotificaDTO(notifica.getId(), notifica.getTesto(), notifica.getData(), notifica.isLetta());
    }
}

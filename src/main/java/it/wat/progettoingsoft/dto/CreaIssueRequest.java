package it.wat.progettoingsoft.dto;

import it.wat.progettoingsoft.costanti.Priorità;
import it.wat.progettoingsoft.costanti.TipoIssue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Corpo della richiesta di creazione issue (UtenteInternoService.creaIssue).
 * priorita e immagine (stringa Base64) sono opzionali: null = NONE / nessuna immagine.
 * Lunghezze massime come le colonne di Issues.
 */
public record CreaIssueRequest(
        @NotBlank(message = "Il titolo è obbligatorio")
        @Size(max = 100, message = "Il titolo può avere al massimo 100 caratteri")
        String titolo,
        @NotBlank(message = "La descrizione è obbligatoria")
        @Size(max = 500, message = "La descrizione può avere al massimo 500 caratteri")
        String descrizione,
        @NotNull(message = "Il tipo è obbligatorio") TipoIssue tipo,
        Priorità priorita,
        @Size(max = 2097152, message = "L'immagine può essere al massimo di 2 MB")
        byte[] immagine) {
}

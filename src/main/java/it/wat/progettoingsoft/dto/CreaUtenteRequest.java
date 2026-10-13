package it.wat.progettoingsoft.dto;

import it.wat.progettoingsoft.costanti.Ruolo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Corpo della richiesta di creazione utente (AmministratoreService.creaUtente).
 * Un ruolo diverso da AMMINISTRATORE/NORMALE/STAKEHOLDER rende il JSON illeggibile (→ 400).
 * Lunghezze massime come le colonne di Utenti.
 */
public record CreaUtenteRequest(
        @NotBlank(message = "Il nome è obbligatorio")
        @Size(max = 50, message = "Il nome può avere al massimo 50 caratteri")
        String nome,
        @NotBlank(message = "Il cognome è obbligatorio")
        @Size(max = 50, message = "Il cognome può avere al massimo 50 caratteri")
        String cognome,
        @NotBlank(message = "L'email è obbligatoria")
        @Email(message = "Email non valida")
        @Size(max = 100, message = "L'email può avere al massimo 100 caratteri")
        String email,
        @NotBlank(message = "La password è obbligatoria") String password,
        @NotNull(message = "Il ruolo è obbligatorio") Ruolo ruolo) {
}

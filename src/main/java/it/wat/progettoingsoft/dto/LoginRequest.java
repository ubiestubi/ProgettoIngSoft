package it.wat.progettoingsoft.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request per il POST /api/auth/login.
 * Controlli sull'email: un'email con spazi iniziali/finali o malformata dà 400,
 * le maiuscole le normalizza UtenteService.findByEmail.
 */
public record LoginRequest(
        @NotBlank(message = "L'email è obbligatoria")
        @Email(message = "Email non valida")
        String email,
        @NotBlank(message = "La password è obbligatoria") String password) {
}

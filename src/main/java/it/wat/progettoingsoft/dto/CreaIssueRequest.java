package it.wat.progettoingsoft.dto;

import it.wat.progettoingsoft.costanti.Priorita;
import it.wat.progettoingsoft.costanti.TipoIssue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Arrays;
import java.util.Objects;

/**
 * Corpo della richiesta di creazione issue (UtenteInternoService.creaIssue).
 * priorita e immagine sono opzionali
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
        Priorita priorita,
        @Size(max = 2097152, message = "L'immagine può essere al massimo di 2 MB")
        byte[] immagine) {

        //Soluzione proposta da SonaQube per la issue "Equals method should be overridden in records containing array fields"
        //Cioè che siccome è un record, i metodi equal, hashcode e toString confrontano in maniera standard, ma ho l'attributo immagine
        //che è un array e qua i confronti si rompono. Con questi override sistemo.

        @Override
        public boolean equals(Object o) {
                if (this == o) {
                        return true;
                }
                if (o == null || getClass() != o.getClass()) {
                        return false;
                }
                CreaIssueRequest richiesta = (CreaIssueRequest) o;
                return Objects.equals(titolo, richiesta.titolo)
                        && Objects.equals(descrizione, richiesta.descrizione)
                        && tipo == richiesta.tipo
                        && priorita == richiesta.priorita
                        && Arrays.equals(immagine, richiesta.immagine);
        }

        @Override
        public int hashCode() {
                int result = Objects.hash(titolo, descrizione, tipo, priorita);
                result = 31 * result + Arrays.hashCode(immagine);
                return result;
        }

        @Override
        public String toString() {
                String dimensioneImmagine = "nessuna";
                if (immagine != null) {
                        dimensioneImmagine = immagine.length + " byte";
                }
                return "CreaIssueRequest{" +
                        "titolo=" + titolo +
                        ", descrizione=" + descrizione +
                        ", tipo=" + tipo +
                        ", priorita=" + priorita +
                        ", immagine=" + dimensioneImmagine +
                        '}';
        }
}

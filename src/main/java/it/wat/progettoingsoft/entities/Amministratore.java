package it.wat.progettoingsoft.entities;

import it.wat.progettoingsoft.costanti.Ruolo;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("AMMINISTRATORE")
@Getter
@Setter
@NoArgsConstructor
public class Amministratore extends UtenteInterno {

    @Override
    public Ruolo getRuolo() {
        return Ruolo.AMMINISTRATORE;
    }
}

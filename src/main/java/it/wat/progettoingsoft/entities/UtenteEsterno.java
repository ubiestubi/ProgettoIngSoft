package it.wat.progettoingsoft.entities;

import it.wat.progettoingsoft.costanti.Ruolo;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// È un utente di sola lettura. Non si usa in questa demo ma era presente in un caso d'uso e quindi ho deciso di mantenerlo
@Entity
@DiscriminatorValue("STAKEHOLDER")
@Getter
@Setter
@NoArgsConstructor
public class UtenteEsterno extends Utente {

    @Override
    public Ruolo getRuolo() {
        return Ruolo.STAKEHOLDER;
    }
}

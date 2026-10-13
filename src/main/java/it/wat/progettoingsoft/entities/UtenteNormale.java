package it.wat.progettoingsoft.entities;

import it.wat.progettoingsoft.costanti.Ruolo;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("NORMALE")
@Getter
@Setter
@NoArgsConstructor
public class UtenteNormale extends UtenteInterno {

    @OneToMany(mappedBy = "destinatario", fetch = FetchType.LAZY)
    private List<Notifica> notifiche = new ArrayList<>();

    @Override
    public Ruolo getRuolo() {
        return Ruolo.NORMALE;
    }
}

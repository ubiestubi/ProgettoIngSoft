package it.wat.progettoingsoft.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Bugs")
@PrimaryKeyJoinColumn(name = "issue_id")
@Getter
@Setter
@NoArgsConstructor
public class Bug extends Issue {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assegnatario_utente_id")
    private UtenteNormale assegnatario;

    @Column(name = "isDuplicato", nullable = false)
    private boolean duplicato;
}

package it.wat.progettoingsoft.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Notifiche")
@Getter
@Setter
@NoArgsConstructor
public class Notifica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notifica_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinatario_utente_id", nullable = false)
    private UtenteNormale destinatario;

    @Column(name = "data", nullable = false, updatable = false)
    private LocalDateTime data;

    @Column(name = "testo", nullable = false, length = 500)
    private String testo;

    @Column(name = "letta", nullable = false)
    private boolean letta;
}

package it.wat.progettoingsoft.entities;

import it.wat.progettoingsoft.costanti.Priorità;
import it.wat.progettoingsoft.costanti.StatoIssue;
import it.wat.progettoingsoft.costanti.TipoIssue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Issues")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issue_id")
    private Integer id;

    @Column(name = "titolo", nullable = false, length = 100)
    private String titolo;

    @Column(name = "descrizione", nullable = false, length = 500)
    private String descrizione;

    @Column(name = "data", nullable = false, updatable = false)
    private LocalDateTime data;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoIssue tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "stato", nullable = false)
    private StatoIssue stato;

    @Enumerated(EnumType.STRING)
    @Column(name = "priorità", nullable = false)
    private Priorità priorità;

    // Lunghezza oltre i 16 MB: Hibernate la mappa su LONGBLOB, come la colonna nello schema
    // (senza length sarebbe TINYBLOB, 255 byte, e la validazione dello schema fallirebbe).
    @Lob
    @Column(name = "immagine", length = 16777216)
    private byte[] immagine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utente_creatore_id", nullable = false)
    private UtenteInterno utenteCreatore;
}

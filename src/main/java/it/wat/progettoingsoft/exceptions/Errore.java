package it.wat.progettoingsoft.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Corpo JSON di tutte le risposte di errore:
 * { "stato": 401, "errore": "Credenziali non valide" }.
 * Per maggiori info leggi comeFunzionanoLeEccezioniInSpring.md
 */
@Getter
@AllArgsConstructor
public class Errore {

    private int stato;
    private String errore;
}
package it.wat.progettoingsoft.costanti;

/**
 * Sostituisce StatoBug: lo stato sta su Issue, non solo su Bug (req. 2 e 3).
 * CHIUSO copre sia la risoluzione sia la chiusura per duplicato (Bug.duplicato
 * distingue il secondo caso).
 */
public enum StatoIssue {
    TODO,
    CHIUSO
}

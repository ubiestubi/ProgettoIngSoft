package it.wat.progettoingsoft.costanti;

/**
 * Tipo di utenza da creare (Amministratore.creaUtente). Non è un campo persistito:
 * su Utente il ruolo è codificato dalla gerarchia di sottoclassi (colonna discriminatore
 * "ruolo" su Utenti), con gli stessi letterali di questo enum.
 */
public enum Ruolo {
    AMMINISTRATORE,
    NORMALE,
    STAKEHOLDER
}

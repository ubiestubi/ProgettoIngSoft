Funzionalità del sistema

BugBoard26 è una piattaforma per la gestione collaborativa di issue in progetti software. Il sistema
consente a team di sviluppo di segnalare problemi relativi a un progetto, monitorarne lo stato, assegnarli
a membri del team e tenere traccia delle attività di risoluzione. Il sistema deve consistere in
un’applicazione (mobile, desktop o web-based) performante e affidabile, attraverso cui gli utenti possono
fruire delle funzionalità in modo intuitivo e rapido.

Funzionalità del sistema BugBoard26

1. Deve essere implementato un sistema di autenticazione semplice e sicuro, basato su email e
   password. Le informazioni gestite dall’applicazione sono critiche per l’azienda, ed è fondamentale
   preservarne l’integrità e la segretezza. Il sistema viene fornito con un account da amministratore
   già attivo, con credenziali di default. Un amministratore può creare ulteriori utenze, specificando
   una email, una password, e indicando se quell’utenza sarà “normale” oppure “di
   amministrazione”.
2. Tutti gli utenti autenticati possono segnalare una issue indicando almeno un titolo e una
   descrizione. Alcuni utenti potrebbero voler specificare anche una priorità e sarebbe gradita la
   possibilità di allegare un’immagine. Le issue possono essere di diverso tipo: question (per
   richieste di chiarimenti), bug (per segnalare malfunzionamenti), documentation (per segnalare
   problemi relativi alla documentazione), e feature (per indicare la richiesta o il suggerimento di
   nuove funzionalità). Le issue create sono inizialmente nello stato “todo”.
3. Il sistema deve offrire una vista riepilogativa delle issue, con la possibilità di filtrare o ordinare i
   risultati in base a criteri come tipologia, stato, priorità o altri parametri rilevanti.
4. Ogni bug dovrebbe poter essere assegnato (da un amministratore) a un membro del team. Quando
   viene assegnato un bug a un utente, quest’ultimo riceve una notifica. Gli utenti possono
   visualizzare i bug loro assegnati.

11. È necessaria una funzione di ricerca che consenta di trovare bug in base a parole chiave.
 
14. Il sistema dovrebbe suggerire automaticamente a chi assegnare un bug, basandosi sul carico di
    lavoro corrente degli utenti.

16. Un amministratore può contrassegnare un bug come duplicato di un altro. Questo comporta la
    chiusura immediata del bug duplicato.

    Ulteriori vincoli
    Tutte le funzionalità assegnate devono essere realizzate implementando un sistema distribuito che
    preveda due macro-componenti indipendenti:

    • Back-end: Almeno un componente che gestisce la logica di business e i dati, esponendo
    interfacce di programmazione accessibili via rete (ad esempio, tramite API REST). Il back-end deve
    essere completamente indipendente dalla parte front-end, ovvero, deve poter funzionare ed
    essere eseguito in maniera autonoma, senza alcuna conoscenza o dipendenza diretta da come il
    front-end verrà implementato.

    • Front-end: Un'interfaccia utente che si appoggia ai servizi offerti dal back-end esclusivamente
    attraverso la rete. Il front-end deve comunicare con il back-end solo tramite le API esposte, e deve
    poter essere sostituito o modificato senza la necessità di modifica alcuna sul back-end.
    Analogamente, il front-end deve poter essere eseguito separatamente dal back-end.
    Il back-end è responsabile della gestione centralizzata dello stato del sistema, garantendo che tutte le
    informazioni e i dati persistenti siano conservati e aggiornati unicamente su di esso. È altresì auspicabile
    (ma non obbligatorio) che il back-end sia distribuito utilizzando tecnologie di containerizzazione come
    Docker e che, al momento della discussione del progetto con demo del prodotto, sia messo in opera
    utilizzando tecnologie allo stato dell’arte quali ad esempio servizi di public Cloud Computing come Azure
    o AWS, e sia quindi accessibile attraverso la rete Internet.
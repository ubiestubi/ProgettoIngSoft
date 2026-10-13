# Contratto delle API (back-end ↔ client JavaFX)

Endpoint del back-end di BugBoard26, escluso il login. Per autenticazione, cookie di sessione, formato degli
errori e significato dei codici vedi `ContrattoLogin.md`: qui valgono le stesse regole.

- Tutti gli endpoint richiedono di essere loggati: senza login la risposta è `401`.
- Se il ruolo dell'utente non permette l'operazione, la risposta è `403`
  (`{"stato":403,"errore":"Operazione non permessa"}`).
- Corpo delle richieste e delle risposte in JSON (`Content-Type: application/json`).
- I valori degli enum vanno scritti esattamente così, in maiuscolo (es. `"NORMALE"`, non `"normale"`).

## Utenti

Tutti gli endpoint di questa sezione sono **riservati agli amministratori** (`403` per gli altri ruoli).

### Creare un utente

```
POST /api/utenti
```

Richiesta:

```json
{
  "nome": "Mario",
  "cognome": "Rossi",
  "email": "mario.rossi@example.com",
  "password": "la-password",
  "ruolo": "NORMALE"
}
```

| Campo | Obbligatorio | Note |
|---|---|---|
| `nome` | sì | Massimo 50 caratteri. Gli spazi iniziali/finali vengono tolti. |
| `cognome` | sì | Massimo 50 caratteri. Gli spazi iniziali/finali vengono tolti. |
| `email` | sì | Formato email valido, senza spazi iniziali/finali (fare `.trim()`), massimo 100 caratteri. Viene salvata in minuscolo. |
| `password` | sì | Non vuota. |
| `ruolo` | sì | `AMMINISTRATORE`, `NORMALE` o `STAKEHOLDER`. |

Risposta `201 Created`: l'utente creato, nello stesso formato del login.

```json
{
  "id": 2,
  "nome": "Mario",
  "cognome": "Rossi",
  "email": "mario.rossi@example.com",
  "ruolo": "NORMALE"
}
```

Errori:

| Codice | Quando |
|---|---|
| `400` | Campo mancante, nome o cognome oltre 50 caratteri, email non valida, ruolo inesistente, oppure email già usata da un altro utente (il messaggio lo dice: `"Email già in uso: ..."`). |

### Elenco degli utenti

```
GET /api/utenti
```

Risposta `200 OK`: tutti gli utenti, in ordine alfabetico di cognome e poi di nome.

```json
[
  { "id": 4, "nome": "Anna", "cognome": "Bianchi", "email": "anna@example.com", "ruolo": "NORMALE" },
  { "id": 1, "nome": "Amministratore", "cognome": "BugBoard", "email": "admin@bugboard.it", "ruolo": "AMMINISTRATORE" },
  { "id": 2, "nome": "Mario", "cognome": "Rossi", "email": "mario.rossi@example.com", "ruolo": "NORMALE" }
]
```

### Assegnatari suggeriti

```
GET /api/utenti/suggeriti
```

Serve quando si assegna un bug: elenca gli **utenti normali** (gli unici a cui si può assegnare un bug) dal
meno carico al più carico. Il carico è il numero di bug ancora aperti (`TODO`) già assegnati; a parità di
carico, ordine alfabetico di cognome e poi di nome. Il primo della lista è il suggerimento.

Risposta `200 OK`:

```json
[
  { "id": 4, "nome": "Anna", "cognome": "Bianchi", "email": "anna@example.com", "bugAperti": 0 },
  { "id": 2, "nome": "Mario", "cognome": "Rossi", "email": "mario.rossi@example.com", "bugAperti": 3 }
]
```

| Campo | Significato |
|---|---|
| `id` | Id dell'utente, da usare per assegnargli il bug. |
| `nome`, `cognome` | Nome e cognome dell'utente. |
| `email` | Email dell'utente. |
| `bugAperti` | Numero di bug `TODO` che ha già assegnati. |

## Issue

| Operazione | Chi può farla |
|---|---|
| Vedere la lista, cercare, vedere il dettaglio e l'immagine | Tutti gli utenti loggati (anche `STAKEHOLDER`) |
| Creare un'issue | `AMMINISTRATORE` e `NORMALE` |
| Chiudere un'issue | Solo `AMMINISTRATORE` |

### Formato di un'issue

Tutti gli endpoint di questa sezione restituiscono le issue in questo formato. Un **bug** ha in più
`assegnatario` (null finché non è assegnato) e `duplicato`; per gli altri tipi questi due campi sono `null`.

Bug:

```json
{
  "id": 4,
  "titolo": "Crash al salvataggio",
  "descrizione": "Si chiude da solo",
  "data": "2026-10-06T15:53:30",
  "tipo": "BUG",
  "stato": "TODO",
  "priorita": "ALTA",
  "haImmagine": false,
  "utenteCreatore": { "id": 2, "nome": "Mario", "cognome": "Rossi", "email": "mario.rossi@example.com", "ruolo": "NORMALE" },
  "assegnatario": { "id": 4, "nome": "Anna", "cognome": "Bianchi", "email": "anna@example.com", "ruolo": "NORMALE" },
  "duplicato": false
}
```

Issue di un altro tipo:

```json
{
  "id": 2,
  "titolo": "Esporta in PDF",
  "descrizione": "Vorrei esportare il report",
  "data": "2026-10-06T15:52:30",
  "tipo": "FEATURE",
  "stato": "TODO",
  "priorita": "NONE",
  "haImmagine": true,
  "utenteCreatore": { "id": 2, "nome": "Mario", "cognome": "Rossi", "email": "mario.rossi@example.com", "ruolo": "NORMALE" },
  "assegnatario": null,
  "duplicato": null
}
```

| Campo | Valori |
|---|---|
| `data` | Data e ora di creazione, ora italiana, formato ISO (`LocalDateTime.parse` la legge direttamente). |
| `tipo` | `QUESTION`, `BUG`, `DOCUMENTATION`, `FEATURE` |
| `stato` | `TODO`, `CHIUSO` |
| `priorita` | `NONE` (non indicata), `BASSA`, `MEDIA`, `ALTA` |
| `haImmagine` | `true` se l'issue ha un'immagine allegata. L'immagine non è nel JSON (le liste resterebbero pesanti): si scarica con `GET /api/issues/{id}/immagine`. |

### Lista delle issue (vista riepilogativa)

```
GET /api/issues
```

Tutti i parametri sono facoltativi; se mancano non si filtra e si ordina dalle più recenti.

| Parametro | Valori | Effetto |
|---|---|---|
| `tipo` | `QUESTION`, `BUG`, `DOCUMENTATION`, `FEATURE` | Solo le issue di quel tipo. |
| `stato` | `TODO`, `CHIUSO` | Solo le issue in quello stato. |
| `priorita` | `NONE`, `BASSA`, `MEDIA`, `ALTA` | Solo le issue con quella priorità. |
| `ordinamento` | `DATA`, `PRIORITA`, `TITOLO`, `STATO`, `TIPO` | Criterio di ordinamento. |
| `crescente` | `true`, `false` (default `false`) | Direzione dell'ordinamento. |

Ordine crescente per ogni criterio (decrescente = al contrario):

| Criterio | Crescente |
|---|---|
| `DATA` | dalla più vecchia alla più recente |
| `PRIORITA` | `NONE`, `BASSA`, `MEDIA`, `ALTA` |
| `TITOLO` | alfabetico A → Z |
| `STATO` | `TODO`, `CHIUSO` |
| `TIPO` | `QUESTION`, `BUG`, `DOCUMENTATION`, `FEATURE` |

A parità di criterio, prima le più recenti. Esempi:

```
GET /api/issues?tipo=BUG&stato=TODO
GET /api/issues?ordinamento=PRIORITA                 (da ALTA a NONE)
GET /api/issues?ordinamento=TITOLO&crescente=true    (A → Z)
```

Risposta `200 OK`: array di issue (vuoto se nessuna corrisponde).

### Ricerca per parola chiave

```
GET /api/issues/ricerca?parolaChiave=login
```

Cerca la parola nel **titolo e nella descrizione** di tutte le issue, senza distinguere maiuscole e minuscole.
Accetta anche tutti i parametri della lista (`tipo`, `stato`, `priorita`, `ordinamento`, `crescente`), con lo
stesso significato. Se `parolaChiave` manca o è vuota, restituisce le stesse issue della lista.

Risposta `200 OK`: array di issue.

### Dettaglio di un'issue

```
GET /api/issues/{id}
```

Risposta `200 OK`: l'issue. `404` se non esiste.

### Immagine di un'issue

```
GET /api/issues/{id}/immagine
```

Da chiamare solo se `haImmagine` è `true`, ad esempio quando si apre il dettaglio.

Risposta `200 OK` con `Content-Type: application/octet-stream`: i **byte dell'immagine così come sono stati
caricati** (non JSON, non Base64). Il formato (PNG, JPEG, ...) non viene salvato, ma JavaFX lo riconosce da solo:

```java
HttpResponse<InputStream> risposta = client.send(richiesta, HttpResponse.BodyHandlers.ofInputStream());
Image immagine = new Image(risposta.body());
```

`404` se l'issue non esiste o non ha un'immagine.

### Creare un'issue

```
POST /api/issues
```

Il creatore è l'utente loggato.

```json
{
  "titolo": "Login non funziona",
  "descrizione": "Errore 500 al login",
  "tipo": "BUG",
  "priorita": "ALTA",
  "immagine": null
}
```

| Campo | Obbligatorio | Note |
|---|---|---|
| `titolo` | sì | Massimo 100 caratteri. |
| `descrizione` | sì | Massimo 500 caratteri. |
| `tipo` | sì | `QUESTION`, `BUG`, `DOCUMENTATION`, `FEATURE`. |
| `priorita` | no | `BASSA`, `MEDIA`, `ALTA`; se manca vale `NONE`. |
| `immagine` | no | File dell'immagine codificato in Base64 (`Base64.getEncoder().encodeToString(bytes)`), massimo 2 MB prima della codifica. |

Risposta `201 Created`: l'issue creata, con `stato` `TODO`.

| Codice | Quando |
|---|---|
| `400` | Campo obbligatorio mancante, testo troppo lungo, immagine oltre 2 MB, valore di `tipo`/`priorita` inesistente. |
| `403` | L'utente è uno `STAKEHOLDER`. |

### Chiudere un'issue

```
POST /api/issues/{id}/chiudi
```

Nessun corpo. Porta l'issue a `CHIUSO`. Se è già chiusa non succede niente e la risposta è comunque `200`.
Se è un bug assegnato, l'assegnatario riceve una notifica.

Risposta `200 OK`: l'issue aggiornata.

| Codice | Quando |
|---|---|
| `403` | L'utente non è un amministratore. |
| `404` | L'issue non esiste. |

## Bug

Un bug è un'issue con `tipo` `BUG`: le risposte di questa sezione sono nel formato issue descritto sopra
(con `assegnatario` e `duplicato` valorizzati). Un id che appartiene a un'issue di altro tipo dà `404`.

| Operazione | Chi può farla |
|---|---|
| Assegnare un bug, segnarlo come duplicato | Solo `AMMINISTRATORE` |
| Vedere i bug assegnati a sé | Solo `NORMALE` (gli unici a cui si assegnano bug) |

### Assegnare un bug

```
POST /api/bugs/{id}/assegna?utenteId=4
```

Nessun corpo. `utenteId` è l'id di un **utente normale**: si può prendere dall'elenco degli assegnatari
suggeriti (`GET /api/utenti/suggeriti`).

- L'utente riceve una notifica.
- Se il bug era già assegnato a un altro utente, viene riassegnato e anche il vecchio assegnatario riceve una
  notifica.
- Se era già assegnato allo stesso utente non cambia niente (nessuna notifica), risposta comunque `200`.

Risposta `200 OK`: il bug aggiornato.

| Codice | Quando |
|---|---|
| `400` | Manca `utenteId`, o non è un numero. |
| `403` | L'utente loggato non è un amministratore. |
| `404` | Il bug non esiste, oppure `utenteId` non è di un utente normale (inesistente, amministratore o stakeholder). |
| `409` | Il bug è chiuso. |

### Segnare un bug come duplicato

```
POST /api/bugs/{id}/duplicato
```

Nessun corpo. Il bug viene segnato come duplicato (`duplicato: true`) e **chiuso subito**. Se ha un
assegnatario, questo riceve una notifica.

Risposta `200 OK`: il bug aggiornato.

| Codice | Quando |
|---|---|
| `403` | L'utente loggato non è un amministratore. |
| `404` | Il bug non esiste. |
| `409` | Il bug è già segnato come duplicato. |

### Bug assegnati a me

```
GET /api/bugs/assegnati
GET /api/bugs/assegnati/aperti
```

- `/assegnati`: tutti i bug assegnati all'utente loggato, **anche quelli chiusi**. Prima i `TODO`, poi i
  `CHIUSO`; in ogni gruppo i più recenti prima.
- `/assegnati/aperti`: solo quelli ancora `TODO`, più recenti prima.

Risposta `200 OK`: array di bug (vuoto se non ne ha). `403` se l'utente loggato non è un utente normale.

## Notifiche

Solo gli utenti `NORMALE` ricevono notifiche: tutti gli endpoint di questa sezione rispondono `403` agli altri
ruoli. Si vedono solo le notifiche dell'utente loggato.

Le notifiche arrivano quando:
- ti viene assegnato un bug, o un bug che avevi viene riassegnato a un altro;
- un bug assegnato a te viene segnato come duplicato;
- un bug assegnato a te viene chiuso.

Formato di una notifica:

```json
{
  "id": 3,
  "testo": "Ti è stato assegnato il bug #7: Bug A",
  "data": "2026-10-06T16:23:36",
  "letta": false
}
```

### Tutte le notifiche

```
GET /api/notifiche
```

Risposta `200 OK`: tutte le notifiche, lette e non lette, più recenti prima. Non cambia lo stato `letta`.

### Numero di notifiche non lette

```
GET /api/notifiche/conteggio
```

Risposta `200 OK`: **un numero semplice** (non un oggetto JSON), ad esempio:

```
3
```

Non segna niente come letto: serve ad esempio per il badge con il numero di notifiche nuove.

### Leggere le notifiche nuove

```
POST /api/notifiche/leggi-nuove
```

Nessun corpo. Risposta `200 OK`: le notifiche non lette, più recenti prima, che **da questo momento risultano
lette** (nella risposta hanno già `letta: true`). Una seconda chiamata restituisce `[]` finché non ne arrivano
di nuove. È `POST` e non `GET` perché modifica i dati.

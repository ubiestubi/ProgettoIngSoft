# Contratto di autenticazione (back-end ↔ client JavaFX)

Accordo su come il client JavaFX si autentica presso il back-end REST di BugBoard26. Le voci segnate
**[da concordare]** sono proposte del back-end: vanno confermate insieme prima di implementarle.

## In breve

L'autenticazione usa la **sessione HTTP** standard:

1. Il client manda email e password a `POST /api/auth/login`.
2. Se le credenziali sono giuste, il back-end crea una sessione e risponde con i dati dell'utente e un
   cookie di sessione (`JSESSIONID`).
3. Il client rimanda quel cookie in **ogni** richiesta successiva: così il back-end sa chi sta chiamando.
4. Se il cookie manca o la sessione è scaduta, il back-end risponde `401` e il client torna al login.

Il client non deve leggere né gestire il cookie a mano: se ne occupa `HttpClient`, basta configurarlo una
volta (vedi sotto).

## Configurare il client (una volta sola)

Con l'`HttpClient` standard di Java (`java.net.http`) basta creare **un solo** client con un `CookieManager`
e usare sempre quello per tutte le chiamate:

```java
HttpClient client = HttpClient.newBuilder()
        .cookieHandler(new CookieManager())
        .build();
```

Il `CookieManager` salva il cookie ricevuto dal login e lo rimanda da solo a ogni richiesta successiva.

- **Un solo `HttpClient` per tutta l'applicazione** (ad es. un campo statico o un singleton): un client nuovo
  ha un `CookieManager` vuoto, quindi risulterebbe non loggato.
- Le chiamate HTTP vanno fatte **fuori dal JavaFX Application Thread** (ad es. dentro un `Task`), altrimenti
  l'interfaccia si blocca durante l'attesa della risposta. Gli aggiornamenti della UI si fanno poi con
  `Platform.runLater`.

## Login

```
POST /api/auth/login
Content-Type: application/json
```

Richiesta:

```json
{
  "email": "mario.rossi@example.com",
  "password": "la-password"
}
```

L'email non distingue maiuscole: `"Mario.Rossi@Example.com"` funziona. Va però mandata **senza spazi
iniziali/finali** (fare `.trim()` sul campo di testo): con gli spazi, o con un formato non valido, la risposta è
`400`.

Risposta `200 OK` (più l'header `Set-Cookie: JSESSIONID=...`, gestito dal `CookieManager`):

```json
{
  "id": 2,
  "nome": "Mario",
  "cognome": "Rossi",
  "email": "mario.rossi@example.com",
  "ruolo": "NORMALE"
}
```

| Campo | Significato |
|---|---|
| `id` | Id dell'utente autenticato. |
| `nome`, `cognome` | Nome e cognome dell'utente. |
| `email` | Email come salvata dal back-end (minuscola). |
| `ruolo` | `AMMINISTRATORE`, `NORMALE` o `STAKEHOLDER`. Serve al client per decidere quali schermate e pulsanti mostrare. |

Errori:

| Codice | Quando |
|---|---|
| `400 Bad Request` | Email o password mancanti nel JSON, oppure email non valida (formato sbagliato o spazi iniziali/finali). |
| `401 Unauthorized` | Email inesistente o password sbagliata. Il messaggio è lo stesso nei due casi, per non rivelare quali email sono registrate. |

## Richieste autenticate

Tutti gli endpoint tranne `POST /api/auth/login` richiedono di essere loggati. Non serve aggiungere niente
alle richieste: il cookie lo manda il `CookieManager`.

| Codice | Quando | Cosa fa il client |
|---|---|---|
| `401 Unauthorized` | Non loggato, sessione scaduta, o back-end riavviato. | Torna alla schermata di login. |
| `403 Forbidden` | Loggato, ma il ruolo non permette l'operazione (es. un utente `NORMALE` che assegna un bug). | Mostra un messaggio di permesso negato. Idealmente non succede, perché il client nasconde le azioni non permesse in base a `ruolo`. |

## Utente corrente

```
GET /api/auth/me
```

Risposta `200 OK`: lo stesso oggetto del login (`id`, `nome`, `cognome`, `email`, `ruolo`). Con sessione assente o scaduta
risponde `401`. Utile per verificare se la sessione è ancora valida.

## Logout

```
POST /api/auth/logout
```

Risposta `204 No Content`. Il back-end chiude la sessione: le richieste successive rispondono `401` finché
non si rifà il login.

## Formato e codici degli errori

Tutte le risposte di errore hanno lo stesso corpo, con un messaggio in italiano che il client può mostrare
all'utente:

```json
{
  "stato": 401,
  "errore": "Credenziali non valide"
}
```

Codici usati da **tutti** gli endpoint (non solo dal login):

| Codice | Significato |
|---|---|
| `400 Bad Request` | Dati mancanti o non validi (es. creare un utente senza email, JSON illeggibile, un valore di filtro inesistente). |
| `401 Unauthorized` | Credenziali sbagliate al login, oppure non loggato / sessione scaduta. |
| `403 Forbidden` | Loggato, ma il ruolo non permette l'operazione. |
| `404 Not Found` | La risorsa richiesta (issue, bug, utente, notifica) non esiste, oppure l'indirizzo non corrisponde a nessun endpoint. |
| `405 Method Not Allowed` | Indirizzo giusto ma metodo HTTP sbagliato (es. `GET` dove serve `POST`). |
| `409 Conflict` | Richiesta corretta ma non possibile nello stato attuale (es. assegnare un bug già chiuso, segnare come duplicato un bug già duplicato). |
| `500 Internal Server Error` | Errore imprevisto del server. **Eccezione:** il corpo è nel formato predefinito di Spring, non in quello sopra; il client deve solo mostrare un messaggio generico. |

## Punti da concordare

- **Durata della sessione:** la sessione scade dopo un periodo di **inattività** (nessuna richiesta).
  Proposta: **30 minuti** (il default di Spring Boot). Ogni richiesta azzera il conto.
- **Riavvio del back-end:** le sessioni stanno in memoria, quindi dopo un riavvio tutti gli utenti ricevono
  `401` e devono rifare il login. Il client deve gestire il `401` in qualunque schermata, non solo all'avvio.
- **Indirizzo del back-end:** in sviluppo `http://localhost:8080`.

## Primo accesso

Il DB parte con un solo utente, l'amministratore di default (Amministratore BugBoard): email `admin@bugboard.it`, password `password`.
Gli altri utenti (amministratori, normali, stakeholder) li crea lui dal client.

## Dettagli lato back-end

Non servono al client, sono qui per completezza.

- Spring Security con sessione HTTP: al login l'utente autenticato viene salvato nella sessione (dentro il
  `SecurityContext`); i permessi sono `@PreAuthorize("hasRole(...)")` sui metodi dei controller. A ogni login
  la sessione viene rinnovata (nuovo `JSESSIONID`).
- Password salvate come hash BCrypt, mai in chiaro; il login le verifica con il `PasswordEncoder`.
- Il ruolo deriva dalla sottoclasse di `Utente` (`Amministratore` → `AMMINISTRATORE`, ecc.), con l'autorità
  `ROLE_<ruolo>` per Spring Security.
- Niente CORS: il client JavaFX non è un browser, quindi la restrizione non si applica.

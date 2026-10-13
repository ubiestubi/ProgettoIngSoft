# Come funzionano le eccezioni in spring

## Recap eccezioni
Ci sono metodi che possono lanciare eccezioni quando qualcosa va storto, e questi metodo vengono circondati dal 
try-catch, che cattura l'eccezione (se viene lanciata) e ci fa qualcosa.

Se un metodo lancia un'eccezione e non può catturarla allora questa passa al metodo sopra nello stack di chiamata.

Lanciare una eccezione ferma l'esecuzione del metodo e si esegue il ramo catch del metodo che l'ha catturata.

Il finally lasciamolo perdere per adesso.


## Bello spring
Teoricamente bisogna circondare ogni metodo capace di lanciare eccezioni col try-catch, ma è un sacco di codice
quindi spring fa una cosa intelligente: è strutturato in modo che non bisogna scrivere il try-catch sui metodi che
possono lanciare eccezioni

## Come fa?
Siccome abbiamo detto che le eccezioni risalgono lo stack di chiamata, spring ci offre uno strumento che permette di 
mettere in cima una classe che cattura tutti gli errori, la classe GlobalExceptionHandler.

Questo strumento è il @RestControllerAdvice, una annotazione da inserire in una classe che chiama la classe quando 
un'eccezione esce da un controller.

Quindi un metodo si scrive pensando che vada tutto bene, poi butta un "if va male" che lancia l'eccezione con un trow.
Sarà poi catturata dal GlobalExceptionHandler.
Esempio:

    public class UserService {
        public User findUserById(String id) throws InvalidDataException {
            if (id == null || id.isEmpty()) {
                throw new InvalidDataException("Invalid user ID");
             }
            // logic to find the user
            return new User();
        }

## Ok ma perchè scrivere eccezioni personalizzate?
Perchè tutte le eccezioni finiscono nel GlobalHandler, e se lanciamo la stessa eccezione per tutto allora il 
GlobalHandler gestisce ogni eccezione nello stesso modo, e non va bene.

L'idea è lanciare eccezioni che facciano capire cosa è successo, così il GlobalHandler può gestirle correttamente,
anche perchè c'è un elefante nella stanza che non abbiamo ancora _addressato_

## Codici di stato HTTP 🐘
Questa applicazione è un backend, cioè un server. Che parlerà con un frontend, cioè un client.
E come parlano server e client? Parlano tramite una richiesta inviata dal client e una risposta restituita dal server,
usando (in questo caso) il protocollo HTTP.

Il server deve quindi poter accettare le richieste http del client e deve poter rispondere a tale richieste, sempre
usando il protocollo http.

Quando un client chiede di eseguire un metodo che poi lancia un'eccezione, il server deve rispondere al client "Oh
guarda c'è l'eccezione" e lo fa con dei codici, detti Codici di stato HTTP 

Ci sono vari codici ed ognuno rappresenta in generale cosa è andato storto, la tabella completa è qui: 

    https://it.wikipedia.org/wiki/Codici_di_stato_HTTP

## Tradurre le eccezioni in codici di stato http
Siccome il server lancia eccezioni e il client richiede codici di stato http, è ovvio che ci vuole un qualcosa che
traduca le eccezioni in codici di stato.
Questo lavoro è svolto dal GlobalHandler.

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorDetails errorDetails = new ErrorDetails("RESOURCE_NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

Il GlobalHandler cattura l'eccezione personalizzata ResourceNotFoundException, e restituisce il l'oggetto 
ResponseEntity, che è costituito da un campo status e un campo body: nel campo stato ci vuole il codice http mentre
nel body ci puoi mettere quello che vuoi, una stringa o un oggetto solitamente, che costituisce il corpo della risposta.
Nell'esempio lo stato è HttpStatus.NOT_FOUND che è una costante per indicare 404.

### Formattare la ResponseEntity
Siccome la ResponseEntity può avere qualsiasi corpo è buona norma formattarlo, spesso in JSON, quindi in questo progetto
è stato creato l'oggetto Errore che viene inviato come corpo della ResponseEntity, e non è nient'altro che un JSON
strutturato come { "stato": 401, "errore": "Credenziali non valide" }. Ovviamente il campo status del ResponseEntity
rimane uguale.

L'ho fatto per fare in modo che il frontend possa ricevere gli errori sempre formattati uguali, così da poterli
gestire più facilmente.

## Tutto questo per dire che:
Il server lancia eccezioni, vengono catturate dal GlobalHandler che le traduce in codici http che vengono
restituiti al client in maniera formattata

## Per una spiegazione forse migliore di come implementare il tutto
https://medium.com/@sharmapraveen91/handle-exceptions-in-spring-boot-a-guide-to-clean-code-principles-e8a9d56cafe8

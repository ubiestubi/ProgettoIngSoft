# BugBoard26 – Back-end

## Requisiti
- Docker Desktop installato e avviato.

### Installazione
1. Scaricare dal link i due file `compose.yaml` e `bugboard-server.tar.gz`
   e metterli nella stessa cartella.
2. Aprire un terminale in quella cartella e caricare l'immagine del back-end:
   `docker load -i bugboard-server.tar.gz`

## Avvio
`docker compose up -d`

Al primo avvio Docker può richiedere qualche minuto. 
Il back-end parte automaticamente quando il database è pronto.

Il back-end è raggiungibile all'indirizzo http://localhost:8080.

## Primo accesso
Il sistema contiene già un amministratore:
- email: admin@bugboard.it
- password: password

## Arresto
- `docker compose down` ferma i container, i dati restano salvati.
- `docker compose down -v` ferma i container e cancella anche i dati del database.

## Link per il download
FileSender:


Mirror (Teams):
https://communitystudentiunina-my.sharepoint.com/:f:/g/personal/u_musto_studenti_unina_it/IgArB_oG9uFMQ5LTcVQUakrmAcshTORCLn3K1V4J0z0thnY?e=BiqROx

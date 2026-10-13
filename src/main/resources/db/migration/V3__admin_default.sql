-- Amministratore di default, necessario per il primo login, poi gli altri utenti li crea l'amministratore.
--
--   nome e cognome: Amministratore BugBoard
--   email: admin@bugboard.it
--   password: password
--
-- Nel DB la password va hashata (col BCrypt), ho usato questo: https://bcrypt-generator.com

INSERT INTO `Bugboard26`.`Utenti` (`nome`, `cognome`, `email`, `password`, `ruolo`)
VALUES ('Amministratore', 'BugBoard', 'admin@bugboard.it', '$2a$10$kU1ePRtcsntFSl/LMVW64es8mF7bUMErE6nCRGUjVPzohXiSRnvka', 'AMMINISTRATORE');

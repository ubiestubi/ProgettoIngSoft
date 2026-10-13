-- Riordina i letterali dell'ENUM "priorità" dalla meno alla più importante, perchè
-- MySQL ordina gli ENUM secondo l'ordine di dichiarazione, quindi ORDER BY priorità ASC mi
-- dà l'ordine NONE, BASSA, MEDIA, ALTA
-- Perchè l'ho fatto? Per semplificarmi la vita con i filtri (vedi il casino in IsssueRepository)
ALTER TABLE `Bugboard26`.`Issues`
    MODIFY `priorità` ENUM('NONE', 'BASSA', 'MEDIA', 'ALTA') NOT NULL;

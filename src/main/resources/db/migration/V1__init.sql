-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema Bugboard26
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema Bugboard26
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `Bugboard26` DEFAULT CHARACTER SET utf8 ;
USE `Bugboard26` ;

-- -----------------------------------------------------
-- Table `Bugboard26`.`Utenti`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Bugboard26`.`Utenti` (
                                                     `utente_id` INT NOT NULL AUTO_INCREMENT,
                                                     `nome` VARCHAR(50) NOT NULL,
                                                     `cognome` VARCHAR(50) NOT NULL,
                                                     `email` VARCHAR(100) NOT NULL,
                                                     `password` VARCHAR(255) NOT NULL,
                                                     `ruolo` ENUM('AMMINISTRATORE', 'NORMALE', 'STAKEHOLDER') NOT NULL,
                                                     PRIMARY KEY (`utente_id`),
                                                     UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE,
                                                     UNIQUE INDEX `utente_id_UNIQUE` (`utente_id` ASC) VISIBLE)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Bugboard26`.`Notifiche`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Bugboard26`.`Notifiche` (
                                                        `notifica_id` INT NOT NULL AUTO_INCREMENT,
                                                        `destinatario_utente_id` INT NOT NULL,
                                                        `testo` VARCHAR(500) NOT NULL,
                                                        `data` DATETIME NOT NULL,
                                                        `letta` TINYINT NOT NULL,
                                                        PRIMARY KEY (`notifica_id`),
                                                        UNIQUE INDEX `notifica_id_UNIQUE` (`notifica_id` ASC) VISIBLE,
                                                        INDEX `fk_Notifiche_Utenti_idx` (`destinatario_utente_id` ASC) VISIBLE,
                                                        CONSTRAINT `fk_Notifiche_Utenti`
                                                            FOREIGN KEY (`destinatario_utente_id`)
                                                                REFERENCES `Bugboard26`.`Utenti` (`utente_id`)
                                                                ON DELETE NO ACTION
                                                                ON UPDATE NO ACTION)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Bugboard26`.`Issues`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Bugboard26`.`Issues` (
                                                     `issue_id` INT NOT NULL AUTO_INCREMENT,
                                                     `utente_creatore_id` INT NOT NULL,
                                                     `tipo` ENUM('QUESTION', 'BUG', 'DOCUMENTATION', 'FEATURE') NOT NULL,
                                                     `titolo` VARCHAR(100) NOT NULL,
                                                     `stato` ENUM('TODO', 'CHIUSO') NOT NULL,
                                                     `priorità` ENUM('ALTA', 'MEDIA', 'BASSA', 'NONE') NOT NULL,
                                                     `descrizione` VARCHAR(500) NOT NULL,
                                                     `data` DATETIME NOT NULL,
                                                     `immagine` LONGBLOB NULL,
                                                     PRIMARY KEY (`issue_id`),
                                                     INDEX `fk_Issues_Utenti1_idx` (`utente_creatore_id` ASC) VISIBLE,
                                                     CONSTRAINT `fk_Issues_Utenti1`
                                                         FOREIGN KEY (`utente_creatore_id`)
                                                             REFERENCES `Bugboard26`.`Utenti` (`utente_id`)
                                                             ON DELETE NO ACTION
                                                             ON UPDATE NO ACTION)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Bugboard26`.`Bugs`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Bugboard26`.`Bugs` (
                                                   `issue_id` INT NOT NULL,
                                                   `assegnatario_utente_id` INT NULL,
                                                   `isDuplicato` TINYINT NOT NULL DEFAULT 0,
                                                   PRIMARY KEY (`issue_id`),
                                                   INDEX `fk_Bugs_Utenti1_idx` (`assegnatario_utente_id` ASC) VISIBLE,
                                                   CONSTRAINT `fk_Bugs_Issues1`
                                                       FOREIGN KEY (`issue_id`)
                                                           REFERENCES `Bugboard26`.`Issues` (`issue_id`)
                                                           ON DELETE CASCADE
                                                           ON UPDATE NO ACTION,
                                                   CONSTRAINT `fk_Bugs_Utenti1`
                                                       FOREIGN KEY (`assegnatario_utente_id`)
                                                           REFERENCES `Bugboard26`.`Utenti` (`utente_id`)
                                                           ON DELETE NO ACTION
                                                           ON UPDATE NO ACTION)
    ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

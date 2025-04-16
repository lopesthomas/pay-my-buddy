-- MySQL Script generated by MySQL Workbench
-- Wed Apr 16 18:48:10 2025
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema paymybuddydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema paymybuddydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `paymybuddydb` DEFAULT CHARACTER SET utf8 ;
USE `paymybuddydb` ;

-- -----------------------------------------------------
-- Table `paymybuddydb`.`user_db`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `paymybuddydb`.`user_db` (
  `id` BIGINT(40) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_name` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `paymybuddydb`.`bank_account`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `paymybuddydb`.`bank_account` (
  `idbank_account` BIGINT(40) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(40) UNSIGNED NOT NULL,
  `balance` DOUBLE NOT NULL,
  PRIMARY KEY (`idbank_account`),
  UNIQUE INDEX `idbank_account_UNIQUE` (`idbank_account` ASC) VISIBLE,
  INDEX `id_idx` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_user_id`
    FOREIGN KEY (`user_id`)
    REFERENCES `paymybuddydb`.`user_db` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `paymybuddydb`.`transaction`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `paymybuddydb`.`transaction` (
  `id` BIGINT(40) UNSIGNED NOT NULL AUTO_INCREMENT,
  `sender_id` BIGINT(40) UNSIGNED NOT NULL,
  `receiver_id` BIGINT(40) UNSIGNED NOT NULL,
  `description` TEXT(50) NULL,
  `amount` DOUBLE UNSIGNED NOT NULL,
  `transaction_date` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
  INDEX `sender_id_idx` (`sender_id` ASC) VISIBLE,
  INDEX `receiver_id_idx` (`receiver_id` ASC) VISIBLE,
  CONSTRAINT `fk_sender_id`
    FOREIGN KEY (`sender_id`)
    REFERENCES `paymybuddydb`.`bank_account` (`idbank_account`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_receiver_id`
    FOREIGN KEY (`receiver_id`)
    REFERENCES `paymybuddydb`.`bank_account` (`idbank_account`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `paymybuddydb`.`connections`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `paymybuddydb`.`connections` (
  `id` BIGINT(40) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(40) UNSIGNED NOT NULL,
  `friend_id` BIGINT(40) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
  INDEX `connection_id_idx` (`user_id` ASC) VISIBLE,
  INDEX `fk_frienconnection_id_idx` (`friend_id` ASC) VISIBLE,
  CONSTRAINT `fk_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `paymybuddydb`.`user_db` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_friend`
    FOREIGN KEY (`friend_id`)
    REFERENCES `paymybuddydb`.`user_db` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- Data for table `paymybuddydb`.`user_db`
-- -----------------------------------------------------
START TRANSACTION;
USE `paymybuddydb`;
INSERT INTO `paymybuddydb`.`user_db` (`id`, `user_name`, `email`, `password`) VALUES (1, 'test', 'test@test.com', 'test');
INSERT INTO `paymybuddydb`.`user_db` (`id`, `user_name`, `email`, `password`) VALUES (2, 'test2', 'test2@test2.com', 'test2');

COMMIT;


-- -----------------------------------------------------
-- Data for table `paymybuddydb`.`bank_account`
-- -----------------------------------------------------
START TRANSACTION;
USE `paymybuddydb`;
INSERT INTO `paymybuddydb`.`bank_account` (`idbank_account`, `user_id`, `balance`) VALUES (1, 1, 100);
INSERT INTO `paymybuddydb`.`bank_account` (`idbank_account`, `user_id`, `balance`) VALUES (2, 2, 50);

COMMIT;


-- -----------------------------------------------------
-- Data for table `paymybuddydb`.`transaction`
-- -----------------------------------------------------
START TRANSACTION;
USE `paymybuddydb`;
INSERT INTO `paymybuddydb`.`transaction` (`id`, `sender_id`, `receiver_id`, `description`, `amount`, `transaction_date`) VALUES (1, 1, 2, 'test', 10, '2025-01-15T13:47');

COMMIT;


-- -----------------------------------------------------
-- Data for table `paymybuddydb`.`connections`
-- -----------------------------------------------------
START TRANSACTION;
USE `paymybuddydb`;
INSERT INTO `paymybuddydb`.`connections` (`id`, `user_id`, `friend_id`) VALUES (1, 1, 2);

COMMIT;


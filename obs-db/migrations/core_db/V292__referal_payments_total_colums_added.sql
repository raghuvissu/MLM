Drop procedure IF EXISTS addTotalPayments;
DELIMITER //
create procedure addTotalPayments() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'total_payments'
     and TABLE_NAME = 'm_referal_master'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `m_referal_master` 
ADD COLUMN `total_payments` DECIMAL(19,6) NULL DEFAULT '0' AFTER `flag`;

END IF;
END //
DELIMITER ;
call addTotalPayments();
Drop procedure IF EXISTS addTotalPayments;

Drop procedure IF EXISTS addTotalPaymentsByReferals;
DELIMITER //
create procedure addTotalPaymentsByReferals() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'total_payments_by_referals'
     and TABLE_NAME = 'm_referal_master'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `m_referal_master` 
ADD COLUMN `total_payments_by_referals` DECIMAL(19,6) NULL DEFAULT '0' AFTER `total_payments`;

END IF;
END //
DELIMITER ;
call addTotalPaymentsByReferals();
Drop procedure IF EXISTS addTotalPaymentsByReferals;

Drop procedure IF EXISTS addreferalCount;
DELIMITER //
create procedure addreferalCount() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'referal_count'
     and TABLE_NAME = 'm_referal_master'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `m_referal_master` 
ADD COLUMN `referal_count` INT(10) NULL DEFAULT '0.000000' AFTER `total_payments`;

END IF;
END //
DELIMITER ;
call addreferalCount();
Drop procedure IF EXISTS addreferalCount;
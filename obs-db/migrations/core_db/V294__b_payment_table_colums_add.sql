Drop procedure IF EXISTS addUseWallet;
DELIMITER //
create procedure addUseWallet() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'use_wallet_amount'
     and TABLE_NAME = 'b_payments'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_payments` 
ADD COLUMN `use_wallet_amount` CHAR(1) NULL DEFAULT 'N' AFTER `is_wallet_payment`;

END IF;
END //
DELIMITER ;
call addUseWallet();
Drop procedure IF EXISTS addUseWallet;

Drop procedure IF EXISTS addWalletAmount;
DELIMITER //
create procedure addWalletAmount() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'wallet_amount'
     and TABLE_NAME = 'b_payments'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_payments` 
ADD COLUMN `wallet_amount` DECIMAL(19,6) NULL DEFAULT NULL AFTER `ref_id`;

END IF;
END //
DELIMITER ;
call addWalletAmount();
Drop procedure IF EXISTS addWalletAmount;

Drop procedure IF EXISTS addpaymentStatus;
DELIMITER //
create procedure addpaymentStatus() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'status'
     and TABLE_NAME = 'b_payments'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_payments` 
ADD COLUMN `status` INT(1) NOT NULL DEFAULT '0' AFTER `wallet_amount`;

END IF;
END //
DELIMITER ;
call addpaymentStatus();
Drop procedure IF EXISTS addpaymentStatus;

Drop procedure IF EXISTS addOtp;
DELIMITER //
create procedure addOtp() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'otp'
     and TABLE_NAME = 'b_payments'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_payments` 
ADD COLUMN `otp` VARCHAR(10) NULL DEFAULT NULL AFTER `status`;

END IF;
END //
DELIMITER ;
call addOtp();
Drop procedure IF EXISTS addOtp;

INSERT IGNORE INTO m_permission VALUES (null, 'Master/Others', 'CONFIRM_PAYMENT', 'PAYMENT', 'CONFIRM', '0');

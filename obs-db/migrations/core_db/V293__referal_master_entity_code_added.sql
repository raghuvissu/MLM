Drop procedure IF EXISTS addExternalCode;
DELIMITER //
create procedure addExternalCode() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'external_code'
     and TABLE_NAME = 'm_referal_master'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `m_referal_master` 
ADD COLUMN `external_code` VARCHAR(100) NULL DEFAULT NULL AFTER `external_id`;

END IF;
END //
DELIMITER ;
call addExternalCode();
Drop procedure IF EXISTS addExternalCode;

ALTER TABLE `m_client` CHANGE COLUMN `caf_id` `caf_id` VARCHAR(100) NULL DEFAULT NULL AFTER `po_id`;

INSERT IGNORE INTO m_permission VALUES (null, 'Master/Others', 'CREATE_COMMISSIONPAYMENT', 'COMMISSIONPAYMENT', 'CREATE', '0');
INSERT IGNORE INTO m_permission VALUES (null, 'Master/Others', 'READ_COMMISSIONPAYMENT', 'COMMISSIONPAYMENT', 'READ', '0');
INSERT IGNORE INTO m_permission VALUES (null, 'Master/Others', 'CREATE_REFERAL', 'REFERAL', 'CREATE', '0');
INSERT IGNORE INTO m_permission VALUES (null, 'Master/Others', 'READ_REFERAL', 'REFERAL', 'READ', '0');
INSERT IGNORE INTO m_permission VALUES (null, 'Master/Others', 'UPDATE_Commission_amount', 'Commission_amount', 'UPDATE', '0');
	
CREATE TABLE IF NOT EXISTS `m_referal_master` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`parent_id` BIGINT(20) NULL DEFAULT NULL,
	`hierarchy` VARCHAR(100) NULL DEFAULT NULL,
	`external_id` VARCHAR(100) NULL DEFAULT NULL,
	`name` VARCHAR(50) NOT NULL,
	`opening_date` DATE NOT NULL,
	`office_type` INT(10) NOT NULL DEFAULT '1',
	`actual_level` INT(10) NOT NULL DEFAULT '1',
	`rate` INT(10) NOT NULL DEFAULT '1',
	`total_amount` DECIMAL(19,6) NOT NULL DEFAULT '0.000000',
	PRIMARY KEY (`id`),
	UNIQUE INDEX `name_refrl` (`name`),
	UNIQUE INDEX `externalid_refrl` (`external_id`),
	INDEX `FK2291C477E2551DCC` (`parent_id`),
	CONSTRAINT `m_referal_master_ibfk_1` FOREIGN KEY (`parent_id`) REFERENCES `m_referal_master` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
ROW_FORMAT=COMPACT
AUTO_INCREMENT=1;



CREATE TABLE IF NOT EXISTS `m_commission_payment` (
	`id` INT(20) NOT NULL AUTO_INCREMENT,
	`client_id` BIGINT(20) NOT NULL,
	`payment_date` DATETIME NOT NULL,
	`paymode_id` INT(20) NOT NULL,
	`debit_amount` DECIMAL(19,6) NULL DEFAULT NULL,
	`credit_amount` DECIMAL(19,6) NULL DEFAULT NULL,
	`receipt_no` VARCHAR(100) NULL DEFAULT NULL,
	`is_deleted` TINYINT(1) NOT NULL DEFAULT '0',
	`Remarks` VARCHAR(200) NULL DEFAULT NULL,
	`createdby_id` BIGINT(20) NULL DEFAULT NULL,
	`created_date` DATETIME NULL DEFAULT NULL,
	`lastmodified_date` DATETIME NULL DEFAULT NULL,
	`lastmodifiedby_id` BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `bp_clid` (`client_id`),
	INDEX `bp_pmtdt` (`payment_date`),
	INDEX `idx_py_pdate` (`payment_date`),
	INDEX `idx_py_pcid` (`createdby_id`),
	INDEX `idx_py_isdel` (`is_deleted`),
	INDEX `idx_py_clientid` (`client_id`)
)
COMMENT='latin1_swedish_ci'
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1;

Drop procedure IF EXISTS addCAFID;
DELIMITER //
create procedure addCAFID() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'caf_id'
     and TABLE_NAME = 'm_client'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `m_client` 
ADD COLUMN `caf_id` BIGINT(20) NULL DEFAULT NULL AFTER `po_id`;

END IF;
END //
DELIMITER ;
call addCAFID();
Drop procedure IF EXISTS addCAFID;

CREATE TABLE IF NOT EXISTS `c_account_number_format` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`account_type_enum` SMALLINT(1) NOT NULL,
	`prefix_type_enum` SMALLINT(2) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `account_type_enum` (`account_type_enum`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `commission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `office_id` bigint(20) NOT NULL,
  `system_level` varchar(2) DEFAULT NULL,
  `actual_level` bigint(20) DEFAULT NULL,
  `rate` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_office_id` (`office_id`),
  CONSTRAINT `fk_commission_office_id` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

-- Dumping data for table obstenant-default.commission: ~10 rows (approximately)
/*!40000 ALTER TABLE `commission` DISABLE KEYS */;
INSERT IGNORE INTO `commission` (`id`, `office_id`, `system_level`, `actual_level`, `rate`) VALUES
	(1, 1, NULL, 10, 15),
	(2, 1, '1', 9, 10),
	(3, 1, '2', 8, 8),
	(4, 1, '3', 7, 5),
	(5, 1, '4', 6, 3),
	(6, 1, '5', 5, 3),
	(7, 1, '6', 4, 3),
	(8, 1, '7', 3, 3),
	(9, 1, '8', 2, 2),
	(10, 1, '9', 1, 1);
/*!40000 ALTER TABLE `commission` ENABLE KEYS */;

-- Dumping data for table obstenant-default.x_registered_table: ~1 rows (approximately)
/*!40000 ALTER TABLE `x_registered_table` DISABLE KEYS */;
INSERT IGNORE INTO `x_registered_table` (`registered_table_name`, `application_table_name`, `category`) VALUES
	('Commission', 'm_office', 100);
INSERT IGNORE INTO `x_registered_table` (`registered_table_name`, `application_table_name`, `category`) VALUES
	('Commission_amount', 'm_office', 100);
INSERT IGNORE INTO `x_registered_table` (`registered_table_name`, `application_table_name`, `category`) VALUES
	('Basic Validation', 'm_office', 100);
/*!40000 ALTER TABLE `x_registered_table` ENABLE KEYS */;

-- Dumping structure for table obstenant-default.commission_amount
CREATE TABLE IF NOT EXISTS `commission_amount` (
  `office_id` bigint(20) NOT NULL,
  `Type_cd_commission Type` int(20) NOT NULL,
  `Value` decimal(19,6) NOT NULL,
  PRIMARY KEY (`office_id`),
  CONSTRAINT `fk_commission_amount_office_id` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table obstenant-default.commission_amount: ~1 rows (approximately)
/*!40000 ALTER TABLE `commission_amount` DISABLE KEYS */;
INSERT IGNORE INTO `commission_amount` (`office_id`, `Type_cd_commission Type`, `Value`) VALUES
	(1, 56, 100.000000);
/*!40000 ALTER TABLE `commission_amount` ENABLE KEYS */;



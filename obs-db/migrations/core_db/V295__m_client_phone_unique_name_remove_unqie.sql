INSERT IGNORE INTO `m_permission`(`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
 VALUES ('Master/Others', 'CREATE_Commission', 'Commission', 'CREATE', '0');
 
 INSERT IGNORE INTO `m_permission`(`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
 VALUES ('Master/Others', 'UPDATE_Commission', 'Commission', 'UPDATE', '0');
 
 INSERT IGNORE INTO `m_permission`(`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
 VALUES ('Master/Others', 'DELETE_Commission', 'Commission', 'DELETE', '0');
 
 
 INSERT IGNORE INTO `b_country` (`id`, `country_code`, `country_name`, `is_active`) VALUES
	(1, 'IN', 'India', 'Y');
	
INSERT IGNORE INTO `b_state` (`id`, `state_code`, `state_name`, `parent_code`, `createdby_id`, `lastmodifiedby_id`, `created_date`, `lastmodified_date`, `is_delete`) VALUES
	(1, 'TS', 'Telangana', 1, NULL, NULL, NULL, NULL, 'N');
	
INSERT IGNORE INTO `b_city` (`id`, `city_code`, `city_name`, `parent_code`, `createdby_id`, `created_date`, `lastmodifiedby_id`, `lastmodified_date`, `is_delete`) VALUES
	(1, 'hyd', 'Hyderabad', 1, 1, '2018-10-06 13:36:20', 1, '2018-10-06 13:36:20', 'N');
	
CREATE TABLE IF NOT EXISTS `basic validation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `office_id` bigint(20) NOT NULL,
  `On Registration` decimal(19,6) NOT NULL,
  `Minimum Payment` decimal(19,6) NOT NULL,
  `No Of Days Adjustment` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_office_id` (`office_id`),
  CONSTRAINT `fk_basic_validation_office_id` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;


INSERT IGNORE INTO `basic validation` (`id`, `office_id`, `On Registration`, `Minimum Payment`, `No Of Days Adjustment`) VALUES
	(1, 1, 50.000000, 100.000000, 90);
	
Drop procedure IF EXISTS addOfficeId;
DELIMITER //
create procedure addOfficeId() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'office_id'
     and TABLE_NAME = 'm_commission_payment'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `m_commission_payment`
ADD COLUMN `office_id` BIGINT(20) NOT NULL AFTER `Remarks`;

END IF;
END //
DELIMITER ;
call addOfficeId();
Drop procedure IF EXISTS addOfficeId;
	
ALTER TABLE `m_client` 
ADD UNIQUE KEY `unique_phone_number`(`phone`);

ALTER TABLE `m_referal_master` 
DROP INDEX `name_refrl`;

ALTER VIEW `fin_trans_vw` AS 	select distinct `b_bill_item`.`id` AS `transId`,`m_appuser`.`username` AS `username`,`b_bill_item`.`client_id` AS `client_id`,NULL AS `clientName`,NULL AS `rate`, NULL AS `office_name`,NULL AS `paymentStatus`,NULL AS `note`,NULL AS `otp`,if((`b_charge`.`charge_type` = 'NRC'),'Once','Periodic') AS `tran_type`,cast(`b_bill_item`.`invoice_date` as date) AS `transDate`,'INVOICE' AS `transType`,if((`b_bill_item`.`invoice_amount` > 0),`b_bill_item`.`invoice_amount`,0) AS `dr_amt`,if((`b_bill_item`.`invoice_amount` < 0),abs(`b_bill_item`.`invoice_amount`),0) AS `cr_amt`,1 AS `flag` from ((`b_bill_item` join `m_appuser`) join `b_charge`) where ((`b_bill_item`.`createdby_id` = `m_appuser`.`id`) and (`b_bill_item`.`id` = `b_charge`.`billitem_id`) and (`b_bill_item`.`invoice_date` <= now())) union all select distinct `b_adjustments`.`id` AS `transId`,`m_appuser`.`username` AS `username`,`b_adjustments`.`client_id` AS `client_id`,NULL AS `clientName`,NULL AS `rate`,  NULL AS `office_name`,NULL AS `paymentStatus`,NULL AS `note`,NULL AS `otp`,(select `m_code_value`.`code_value` from `m_code_value` where ((`m_code_value`.`code_id` = 12) and (`b_adjustments`.`adjustment_code` = `m_code_value`.`id`))) AS `tran_type`,cast(`b_adjustments`.`adjustment_date` as date) AS `transdate`,'ADJUSTMENT' AS `transType`,0 AS `dr_amt`,(case `b_adjustments`.`adjustment_type` when 'CREDIT' then `b_adjustments`.`adjustment_amount` end) AS `cr_amount`,1 AS `flag` from (`b_adjustments` join `m_appuser`) where ((`b_adjustments`.`adjustment_date` <= now()) and (`b_adjustments`.`adjustment_type` = 'CREDIT') and (`b_adjustments`.`createdby_id` = `m_appuser`.`id`)) union all select distinct `b_adjustments`.`id` AS `transId`,`m_appuser`.`username` AS `username`,`b_adjustments`.`client_id` AS `client_id`,NULL AS `clientName`,NULL AS `rate`,  NULL AS `office_name`,NULL AS `paymentStatus`,NULL AS `note`,NULL AS `otp`,(select `m_code_value`.`code_value` from `m_code_value` where ((`m_code_value`.`code_id` = 12) and (`b_adjustments`.`adjustment_code` = `m_code_value`.`id`))) AS `tran_type`,cast(`b_adjustments`.`adjustment_date` as date) AS `transdate`,'ADJUSTMENT' AS `transType`,(case `b_adjustments`.`adjustment_type` when 'DEBIT' then `b_adjustments`.`adjustment_amount` end) AS `dr_amount`,0 AS `cr_amt`,1 AS `flag` from (`b_adjustments` join `m_appuser`) where ((`b_adjustments`.`adjustment_date` <= now()) and (`b_adjustments`.`adjustment_type` = 'DEBIT') and (`b_adjustments`.`createdby_id` = `m_appuser`.`id`)) union all select distinct `b_payments`.`id` AS `transId`,`m_appuser`.`username` AS `username`,`b_payments`.`client_id` AS `client_id`,NULL AS `clientName`,NULL AS `rate`,  NULL AS `office_name`,`b_payments`.`status` AS `paymentStatus`,`b_payments`.`Remarks` AS `note`,`b_payments`.`otp` AS `otp`,(select `m_code_value`.`code_value` from `m_code_value` where ((`m_code_value`.`code_id` = 11) and (`b_payments`.`paymode_id` = `m_code_value`.`id`))) AS `tran_type`,cast(`b_payments`.`payment_date` as date) AS `transDate`,'PAYMENT' AS `transType`,0 AS `dr_amt`,`b_payments`.`amount_paid` AS `cr_amount`,`b_payments`.`is_deleted` AS `flag` from (`b_payments` join `m_appuser`) where ((`b_payments`.`createdby_id` = `m_appuser`.`id`) and isnull(`b_payments`.`ref_id`) and (`b_payments`.`payment_date` <= now())) union all select distinct `b_payments`.`id` AS `transId`,`m_appuser`.`username` AS `username`,`b_payments`.`client_id` AS `client_id`,NULL AS `clientName`,NULL AS `rate`,  NULL AS `office_name`,NULL AS `paymentStatus`,NULL AS `note`,NULL AS `otp`,(select `m_code_value`.`code_value` from `m_code_value` where ((`m_code_value`.`code_id` = 11) and (`b_payments`.`paymode_id` = `m_code_value`.`id`))) AS `tran_type`,cast(`b_payments`.`payment_date` as date) AS `transDate`,'PAYMENT CANCELED' AS `transType`,abs(`b_payments`.`amount_paid`) AS `dr_amt`,0 AS `cr_amount`,`b_payments`.`is_deleted` AS `flag` from (`b_payments` join `m_appuser`) where ((`b_payments`.`is_deleted` = 1) and (`b_payments`.`ref_id` is not null) and (`b_payments`.`createdby_id` = `m_appuser`.`id`) and (`b_payments`.`payment_date` <= now())) union all select distinct `m_commission_payment`.`id` AS `transId`,`m_appuser`.`username` AS `username`,`m_commission_payment`.`client_id` AS `client_id`,`m_commission_payment`.`client_name` AS `clientName`,`m_commission_payment`.`rate` AS `rate`, (select `m_office`.`name` from `m_office` where (`m_office`.`id` = `m_commission_payment`.`office_id`)) AS `office_name`,NULL AS `paymentStatus`,`m_commission_payment`.`Remarks` AS `note`,NULL AS `otp`,(select `m_code_value`.`code_value` from `m_code_value` where ((`m_code_value`.`code_id` = 11) and (`m_commission_payment`.`paymode_id` = `m_code_value`.`id`))) AS `tran_type`,cast(`m_commission_payment`.`payment_date` as date) AS `transDate`,'COMMISSION' AS `transType`,`m_commission_payment`.`debit_amount` AS `dr_amt`,`m_commission_payment`.`credit_amount` AS `cr_amount`,`m_commission_payment`.`is_deleted` AS `flag` from (`m_commission_payment` join `m_appuser`) where ((`m_commission_payment`.`createdby_id` = `m_appuser`.`id`) and (`m_commission_payment`.`payment_date` <= now())) union all select distinct `bjt`.`id` AS `transId`,`ma`.`username` AS `username`,`bjt`.`client_id` AS `client_id`,NULL AS `clientName`,NULL AS `rate`,  NULL AS `office_name`,NULL AS `paymentStatus`,NULL AS `note`,NULL AS `otp`,'Event Journal' AS `tran_type`,cast(`bjt`.`jv_date` as date) AS `transDate`,'JOURNAL VOUCHER' AS `transType`,ifnull(`bjt`.`debit_amount`,0) AS `dr_amt`,ifnull(`bjt`.`credit_amount`,0) AS `cr_amount`,1 AS `flag` from (`b_jv_transactions` `bjt` join `m_appuser` `ma` on(((`bjt`.`createdby_id` = `ma`.`id`) and (`bjt`.`jv_date` <= now())))) union all select distinct `bdr`.`id` AS `transId`,`m_appuser`.`username` AS `username`,`bdr`.`client_id` AS `client_id`,NULL AS `clientName`,NULL AS `rate`,  NULL AS `office_name`,NULL AS `paymentStatus`,NULL AS `note`,NULL AS `otp`,`bdr`.`description` AS `tran_type`,cast(`bdr`.`transaction_date` as date) AS `transDate`,'DEPOSIT&REFUND' AS `transType`,ifnull(`bdr`.`debit_amount`,0) AS `dr_amt`,ifnull(`bdr`.`credit_amount`,0) AS `cr_amount`,`bdr`.`is_refund` AS `flag` from (`b_deposit_refund` `bdr` join `m_appuser`) where ((`bdr`.`createdby_id` = `m_appuser`.`id`) and (`bdr`.`transaction_date` <= now())) order by 1,2;

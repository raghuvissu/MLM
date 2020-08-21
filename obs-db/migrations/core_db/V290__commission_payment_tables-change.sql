Drop procedure IF EXISTS addRateCl;
DELIMITER //
create procedure addRateCl() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'rate'
     and TABLE_NAME = 'm_commission_payment'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `m_commission_payment` 
ADD COLUMN `rate` INT(20) NOT NULL DEFAULT 0 AFTER `credit_amount`;

END IF;
END //
DELIMITER ;
call addRateCl();
Drop procedure IF EXISTS addRateCl;

Drop procedure IF EXISTS addClientNameCl;
DELIMITER //
create procedure addClientNameCl() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'client_name'
     and TABLE_NAME = 'm_commission_payment'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `m_commission_payment` 
ADD COLUMN `client_name` VARCHAR(100) NULL DEFAULT NULL AFTER `client_id`;

END IF;
END //
DELIMITER ;
call addClientNameCl();
Drop procedure IF EXISTS addClientNameCl;

 ALTER VIEW `fin_trans_vw` AS select distinct `b_bill_item`.`id` AS `transId`,`m_appuser`.`username` AS `username`,`b_bill_item`.`client_id` AS `client_id`, NULL as `clientName`, NULL as `rate`, if((`b_charge`.`charge_type` = 'NRC'),'Once','Periodic') AS `tran_type`,cast(`b_bill_item`.`invoice_date` as date) AS `transDate`,'INVOICE' AS `transType`,if((`b_bill_item`.`invoice_amount` > 0),`b_bill_item`.`invoice_amount`,0) AS `dr_amt`,if((`b_bill_item`.`invoice_amount` < 0),abs(`b_bill_item`.`invoice_amount`),0) AS `cr_amt`,1 AS `flag` from ((`b_bill_item` join `m_appuser`) join `b_charge`) where ((`b_bill_item`.`createdby_id` = `m_appuser`.`id`) and (`b_bill_item`.`id` = `b_charge`.`billitem_id`) and (`b_bill_item`.`invoice_date` <= now())) union all select distinct `b_adjustments`.`id` AS `transId`,`m_appuser`.`username` AS `username`,`b_adjustments`.`client_id` AS `client_id`, NULL as `clientName`,  NULL as `rate`, (select `m_code_value`.`code_value` from `m_code_value` where ((`m_code_value`.`code_id` = 12) and (`b_adjustments`.`adjustment_code` = `m_code_value`.`id`))) AS `tran_type`,cast(`b_adjustments`.`adjustment_date` as date) AS `transdate`,'ADJUSTMENT' AS `transType`,0 AS `dr_amt`,(case `b_adjustments`.`adjustment_type` when 'CREDIT' then `b_adjustments`.`adjustment_amount` end) AS `cr_amount`,1 AS `flag` from (`b_adjustments` join `m_appuser`) where ((`b_adjustments`.`adjustment_date` <= now()) and (`b_adjustments`.`adjustment_type` = 'CREDIT') and (`b_adjustments`.`createdby_id` = `m_appuser`.`id`)) union all select distinct `b_adjustments`.`id` AS `transId`,`m_appuser`.`username` AS `username`,`b_adjustments`.`client_id` AS `client_id`, NULL as `clientName`,  NULL as `rate`, (select `m_code_value`.`code_value` from `m_code_value` where ((`m_code_value`.`code_id` = 12) and (`b_adjustments`.`adjustment_code` = `m_code_value`.`id`))) AS `tran_type`,cast(`b_adjustments`.`adjustment_date` as date) AS `transdate`,'ADJUSTMENT' AS `transType`,(case `b_adjustments`.`adjustment_type` when 'DEBIT' then `b_adjustments`.`adjustment_amount` end) AS `dr_amount`,0 AS `cr_amt`,1 AS `flag` from (`b_adjustments` join `m_appuser`) where ((`b_adjustments`.`adjustment_date` <= now()) and (`b_adjustments`.`adjustment_type` = 'DEBIT') and (`b_adjustments`.`createdby_id` = `m_appuser`.`id`)) union all select distinct `b_payments`.`id` AS `transId`,`m_appuser`.`username` AS `username`,`b_payments`.`client_id` AS `client_id`, NULL as `clientName`,  NULL as `rate`, (select `m_code_value`.`code_value` from `m_code_value` where ((`m_code_value`.`code_id` = 11) and (`b_payments`.`paymode_id` = `m_code_value`.`id`))) AS `tran_type`,cast(`b_payments`.`payment_date` as date) AS `transDate`,'PAYMENT' AS `transType`,0 AS `dr_amt`,`b_payments`.`amount_paid` AS `cr_amount`,`b_payments`.`is_deleted` AS `flag` from (`b_payments` join `m_appuser`) where ((`b_payments`.`createdby_id` = `m_appuser`.`id`) and isnull(`b_payments`.`ref_id`) and (`b_payments`.`payment_date` <= now())) union all select distinct `b_payments`.`id` AS `transId`,`m_appuser`.`username` AS `username`,`b_payments`.`client_id` AS `client_id`, NULL as `clientName`, NULL as `rate`, (select `m_code_value`.`code_value` from `m_code_value` where ((`m_code_value`.`code_id` = 11) and (`b_payments`.`paymode_id` = `m_code_value`.`id`))) AS `tran_type`,cast(`b_payments`.`payment_date` as date) AS `transDate`,'PAYMENT CANCELED' AS `transType`,abs(`b_payments`.`amount_paid`) AS `dr_amt`,0 AS `cr_amount`,`b_payments`.`is_deleted` AS `flag` from (`b_payments` join `m_appuser`) where ((`b_payments`.`is_deleted` = 1) and (`b_payments`.`ref_id` is not null) and (`b_payments`.`createdby_id` = `m_appuser`.`id`) and (`b_payments`.`payment_date` <= now())) union all select distinct `m_commission_payment`.`id` AS `transId`,`m_appuser`.`username` AS `username`,`m_commission_payment`.`client_id` AS `client_id`,`m_commission_payment`.`client_name` AS `clientName`, `m_commission_payment`.`rate` AS `rate`, (select `m_code_value`.`code_value` from `m_code_value` where ((`m_code_value`.`code_id` = 11) and (`m_commission_payment`.`paymode_id` = `m_code_value`.`id`))) AS `tran_type`,cast(`m_commission_payment`.`payment_date` as date) AS `transDate`,'COMMISSION' AS `transType`,`m_commission_payment`.`debit_amount` AS `dr_amt`,`m_commission_payment`.`credit_amount` AS `cr_amount`,`m_commission_payment`.`is_deleted` AS `flag` from (`m_commission_payment` join `m_appuser`) where ((`m_commission_payment`.`createdby_id` = `m_appuser`.`id`) and (`m_commission_payment`.`payment_date` <= now())) union all select distinct `bjt`.`id` AS `transId`,`ma`.`username` AS `username`,`bjt`.`client_id` AS `client_id`, NULL as `clientName`, NULL as `rate`,'Event Journal' AS `tran_type`,cast(`bjt`.`jv_date` as date) AS `transDate`,'JOURNAL VOUCHER' AS `transType`,ifnull(`bjt`.`debit_amount`,0) AS `dr_amt`,ifnull(`bjt`.`credit_amount`,0) AS `cr_amount`,1 AS `flag` from (`b_jv_transactions` `bjt` join `m_appuser` `ma` on(((`bjt`.`createdby_id` = `ma`.`id`) and (`bjt`.`jv_date` <= now())))) union all select distinct `bdr`.`id` AS `transId`,`m_appuser`.`username` AS `username`,`bdr`.`client_id` AS `client_id`, NULL as `clientName`, NULL as `rate`,`bdr`.`description` AS `tran_type`,cast(`bdr`.`transaction_date` as date) AS `transDate`,'DEPOSIT&REFUND' AS `transType`,ifnull(`bdr`.`debit_amount`,0) AS `dr_amt`,ifnull(`bdr`.`credit_amount`,0) AS `cr_amount`,`bdr`.`is_refund` AS `flag` from (`b_deposit_refund` `bdr` join `m_appuser`) where ((`bdr`.`createdby_id` = `m_appuser`.`id`) and (`bdr`.`transaction_date` <= now())) order by 1,2;
 
 Drop procedure IF EXISTS addReferalFlag;
DELIMITER //
create procedure addReferalFlag() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'flag'
     and TABLE_NAME = 'm_referal_master'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `m_referal_master` 
ADD COLUMN `flag` INT(2) NOT NULL DEFAULT '0' AFTER `total_amount`;

END IF;
END //
DELIMITER ;
call addReferalFlag();
Drop procedure IF EXISTS addReferalFlag;

-- Create databases
CREATE DATABASE cms_statement_dev;
CREATE DATABASE cms_statement_prod;

-- Create database service accounts ( already created with CMSCreditAccountService )
-- CREATE USER 'cms_dev_user'@'%' IDENTIFIED BY 'shd_dev66';
-- CREATE USER 'cms_prod_user'@'%' IDENTIFIED BY 'shd_prod99';

-- Database grants (only DML)
GRANT SELECT ON cms_statement_dev.* TO 'cms_dev_user'@'%';
GRANT INSERT ON cms_statement_dev.* TO 'cms_dev_user'@'%';
GRANT UPDATE ON cms_statement_dev.* TO 'cms_dev_user'@'%';
GRANT DELETE ON cms_statement_dev.* TO 'cms_dev_user'@'%';

GRANT SELECT ON cms_statement_prod.* TO 'cms_prod_user'@'%';
GRANT INSERT ON cms_statement_prod.* TO 'cms_prod_user'@'%';
GRANT UPDATE ON cms_statement_prod.* TO 'cms_prod_user'@'%';
GRANT DELETE ON cms_statement_prod.* TO 'cms_prod_user'@'%';

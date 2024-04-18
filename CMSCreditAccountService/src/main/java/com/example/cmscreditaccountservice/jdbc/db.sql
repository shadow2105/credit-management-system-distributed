-- Create databases
CREATE DATABASE cms_account_dev;
CREATE DATABASE cms_account_prod;

-- Create database service accounts
CREATE USER 'cms_dev_user'@'%' IDENTIFIED BY 'shd_dev66';
CREATE USER 'cms_prod_user'@'%' IDENTIFIED BY 'shd_prod99';

-- Database grants (only DML)
GRANT SELECT ON cms_account_dev.* TO 'cms_dev_user'@'%';
GRANT INSERT ON cms_account_dev.* TO 'cms_dev_user'@'%';
GRANT UPDATE ON cms_account_dev.* TO 'cms_dev_user'@'%';
GRANT DELETE ON cms_account_dev.* TO 'cms_dev_user'@'%';

GRANT SELECT ON cms_account_prod.* TO 'cms_prod_user'@'%';
GRANT INSERT ON cms_account_prod.* TO 'cms_prod_user'@'%';
GRANT UPDATE ON cms_account_prod.* TO 'cms_prod_user'@'%';
GRANT DELETE ON cms_account_prod.* TO 'cms_prod_user'@'%';

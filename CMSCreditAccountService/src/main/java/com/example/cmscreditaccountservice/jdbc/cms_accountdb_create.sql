create table credit_accounts (credit_limit decimal(38,2) not null, current_balance decimal(38,2) not null, interest_rate decimal(38,2) not null, created_at datetime(6), updated_at datetime(6), id BINARY(16) not null, account_number varchar(255) not null, created_by varchar(255), customer_id varchar(255) not null, updated_by varchar(255), primary key (id)) engine=InnoDB;
create table credit_cards (expiry_date date not null, credit_account_id binary(16) not null, cvv varchar(255) not null, primary key (credit_account_id)) engine=InnoDB;
alter table credit_accounts add constraint UK_g3p17blxd4kitkbln7ghg6dii unique (account_number);
alter table credit_cards add constraint UK_lt6sed710yv5k6cty1v64eiau unique (cvv);
alter table credit_cards add constraint FKq49yubfj75mm0fmm4k053wn9g foreign key (credit_account_id) references credit_accounts (id);

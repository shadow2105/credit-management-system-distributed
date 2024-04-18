create table credit_accounts (credit_limit decimal(38,2) not null, current_balance decimal(38,2) not null, interest_rate decimal(38,2) not null, created_at datetime(6), updated_at datetime(6), id BINARY(16) not null, account_number varchar(255) not null, created_by varchar(255), customer_id varchar(255) not null, updated_by varchar(255), primary key (id)) engine=InnoDB;
create table credit_statement_schedule (statement_day_of_month integer not null, credit_account_id binary(16) not null, primary key (credit_account_id)) engine=InnoDB;
create table credit_statements (amount_due decimal(38,2) not null, credit_statement_date date not null, due_date date not null, minimum_payment decimal(38,2) not null, created_at datetime(6), updated_at datetime(6), credit_account_id BINARY(16), id BINARY(16) not null, created_by varchar(255), updated_by varchar(255), primary key (id)) engine=InnoDB;
create table transactions (amount decimal(38,2) not null, closing_balance decimal(38,2) not null, type char(1) not null, post_datetime datetime(6) not null, transaction_datetime datetime(6) not null, credit_account_id BINARY(16), transaction_id binary(16) not null, description varchar(255) not null, primary key (transaction_id)) engine=InnoDB;
alter table credit_accounts add constraint UK_g3p17blxd4kitkbln7ghg6dii unique (account_number);
alter table credit_statement_schedule add constraint FK1qqhmgyq9p7a1h7sxc9sxtuog foreign key (credit_account_id) references credit_accounts (id);
alter table credit_statements add constraint FKafgnd8tonl7yfwdgqjcpprpl4 foreign key (credit_account_id) references credit_accounts (id);
alter table transactions add constraint FK90008g4qpovdymgntet75mpdb foreign key (credit_account_id) references credit_accounts (id);

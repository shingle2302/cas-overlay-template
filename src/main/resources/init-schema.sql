create table pm_table_accounts
(
    id        bigserial    not null primary key,
    username  varchar(255) not null unique,
    password  varchar(255) not null,
    firstname varchar(255),
    lastname  varchar(255),
    email     varchar(255),
    phone     varchar(255),
    enabled   boolean      not null default true,
    expires   timestamp,
    created   timestamp    not null default now(),
    modified  timestamp
);


create table pm_table_questions
(
    id       bigserial    not null primary key,
    username varchar(255) not null,
    question varchar(255) not null,
    answer   varchar(255) not null
);
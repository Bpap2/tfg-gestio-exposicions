create table obra (
                      id bigserial primary key,
                      identificador_intern varchar(64) not null unique,
                      autor varchar(255),
                      titol varchar(255) not null,
                      any_obra varchar(32),
                      tecnica varchar(255),
                      dimensions varchar(255),
                      created_at timestamp not null default now()
);

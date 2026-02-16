create table users (
                       id bigserial primary key,
                       email varchar(255) not null unique,
                       password_hash varchar(255) not null,
                       role varchar(32) not null
);

create table artworks (
                          id bigserial primary key,
                          title varchar(255) not null,
                          author varchar(255),
                          year_int int,
                          description text,
                          file_url text
);

create table exhibitions (
                             id bigserial primary key,
                             name varchar(255) not null,
                             start_date date,
                             end_date date,
                             description text
);

create table exhibition_artworks (
                                     exhibition_id bigint not null references exhibitions(id) on delete cascade,
                                     artwork_id bigint not null references artworks(id) on delete cascade,
                                     primary key (exhibition_id, artwork_id)
);

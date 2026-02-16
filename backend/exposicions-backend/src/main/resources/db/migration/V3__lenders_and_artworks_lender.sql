-- 1) Taula prestadors
create table if not exists lenders (
                                       id bigserial primary key,
                                       name text not null,
                                       code text,
                                       email text,
                                       phone text,
                                       notes text
);

-- 2) FK a artworks (1 obra -> 1 prestador)
alter table artworks
    add column if not exists lender_id bigint;

-- 3) Afegir FK només si no existeix (Postgres no té "ADD CONSTRAINT IF NOT EXISTS")
do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'fk_artworks_lender'
    ) then
alter table artworks
    add constraint fk_artworks_lender
        foreign key (lender_id) references lenders(id)
            on delete set null;
end if;
end$$;

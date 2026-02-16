alter table lenders
    add constraint uk_lenders_code unique (code);

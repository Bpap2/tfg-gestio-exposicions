-- V2__artworks_full_fields.sql

-- Per evitar errors si ja ho has canviat a mà:
ALTER TABLE artworks ALTER COLUMN title TYPE text;
ALTER TABLE artworks ALTER COLUMN author TYPE text;

ALTER TABLE artworks
    ADD COLUMN IF NOT EXISTS lender_no              integer,
    ADD COLUMN IF NOT EXISTS work_no                integer,

    ADD COLUMN IF NOT EXISTS author_name            text,
    ADD COLUMN IF NOT EXISTS author_surname         text,

    ADD COLUMN IF NOT EXISTS date_text              text,
    ADD COLUMN IF NOT EXISTS original_repro_av      text,
    ADD COLUMN IF NOT EXISTS work_type              text,
    ADD COLUMN IF NOT EXISTS technique_material     text,

    ADD COLUMN IF NOT EXISTS height                 double precision,
    ADD COLUMN IF NOT EXISTS width                  double precision,
    ADD COLUMN IF NOT EXISTS depth                  double precision,
    ADD COLUMN IF NOT EXISTS framed_dimensions      text,

    ADD COLUMN IF NOT EXISTS section                text,
    ADD COLUMN IF NOT EXISTS repro_type             text,

    ADD COLUMN IF NOT EXISTS loan_ok                text,
    ADD COLUMN IF NOT EXISTS management_notes       text,
    ADD COLUMN IF NOT EXISTS inventory_no           text,
    ADD COLUMN IF NOT EXISTS credit                 text,
    ADD COLUMN IF NOT EXISTS elements_no            integer,
    ADD COLUMN IF NOT EXISTS valuation              double precision,
    ADD COLUMN IF NOT EXISTS currency               varchar(10),

    ADD COLUMN IF NOT EXISTS pickup_address         text,
    ADD COLUMN IF NOT EXISTS pickup_city_code       text,
    ADD COLUMN IF NOT EXISTS pickup_country         text,
    ADD COLUMN IF NOT EXISTS return_address         text,
    ADD COLUMN IF NOT EXISTS return_city_code       text,
    ADD COLUMN IF NOT EXISTS return_country         text,

    ADD COLUMN IF NOT EXISTS web                    text,
    ADD COLUMN IF NOT EXISTS reviewed               text,

    ADD COLUMN IF NOT EXISTS conservation_notes     text,
    ADD COLUMN IF NOT EXISTS transport_notes        text,
    ADD COLUMN IF NOT EXISTS insurance_notes        text,

    ADD COLUMN IF NOT EXISTS packaging              text,
    ADD COLUMN IF NOT EXISTS packaging_dimensions   text,

    ADD COLUMN IF NOT EXISTS label_info             text,

    ADD COLUMN IF NOT EXISTS av_no                  text,
    ADD COLUMN IF NOT EXISTS av_support             text,

    ADD COLUMN IF NOT EXISTS current_location       text,

    ADD COLUMN IF NOT EXISTS catalog_notes          text,
    ADD COLUMN IF NOT EXISTS size_notes             text,
    ADD COLUMN IF NOT EXISTS placement              text,
    ADD COLUMN IF NOT EXISTS in_catalog             text,

    ADD COLUMN IF NOT EXISTS hi_res_image           text,
    ADD COLUMN IF NOT EXISTS entry_delivery_note    text,
    ADD COLUMN IF NOT EXISTS frame_at_cccb          text,
    ADD COLUMN IF NOT EXISTS showcase_pedestal      text,

    ADD COLUMN IF NOT EXISTS management_status      text,
    ADD COLUMN IF NOT EXISTS other_info             text,
    ADD COLUMN IF NOT EXISTS dimensions_text        text,
    ADD COLUMN IF NOT EXISTS options_text           text,
    ADD COLUMN IF NOT EXISTS def_exposed            text,

    ADD COLUMN IF NOT EXISTS image_at_cccb          boolean NOT NULL DEFAULT false,

    ADD COLUMN IF NOT EXISTS thematic_group_ok      text,
    ADD COLUMN IF NOT EXISTS sub_section_ok         text,

    ADD COLUMN IF NOT EXISTS tender                 text,
    ADD COLUMN IF NOT EXISTS insurance              text,
    ADD COLUMN IF NOT EXISTS final_format           text,

    ADD COLUMN IF NOT EXISTS ambit                  text,
    ADD COLUMN IF NOT EXISTS subambit               text;

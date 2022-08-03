-- View: public.mat_view_cities_and_facilities

-- DROP MATERIALIZED VIEW IF EXISTS public.mat_view_cities_and_facilities;

CREATE MATERIALIZED VIEW IF NOT EXISTS public.mat_view_cities_and_facilities
TABLESPACE pg_default
AS
SELECT geo.geo_id,
       geo.name AS city_name,
       geo.name_upper_case AS city_name_upper_case,
       ctry.rkst AS country_code,
       geo.country_id AS country_geo_id,
       geo.country_name,
       geo.country_name_upper_case,
       geo.geo_type,
       geo.name AS locality_name,
       geo.rkst,
       geo.rkts,
       reg.iso_territory AS region_code,
       reg.name AS region_name,
       NULL::character varying AS site_name,
    geo.olson_time_zone,
    geo.unloc,
    geo.unloc_lookup,
    geo.unloc_return
FROM geography geo
    LEFT JOIN geography reg ON geo.parent_id::text = reg.geo_id::text AND reg.geo_type::text = 'STATE/PROV'::text
    JOIN geography ctry ON geo.country_id::text = ctry.geo_id::text AND ctry.geo_type::text = 'COUNTRY'::text
WHERE geo.geo_type::text = 'CITY'::text
UNION ALL
SELECT f.geo_id,
       city.name AS city_name,
       city.name_upper_case AS city_name_upper_case,
       ctry.rkst AS country_code,
       city.country_id AS country_geo_id,
       city.country_name,
       city.country_name_upper_case,
       ft.name AS geo_type,
       f.name AS locality_name,
       f.rkst,
       f.rkts,
       reg.iso_territory AS region_code,
       reg.name AS region_name,
       f.name AS site_name,
       city.olson_time_zone,
       f.unloc,
       f.unloc_lookup,
       f.unloc_return
FROM facilities f
         JOIN geography city ON f.parent_id::text = city.geo_id::text
     LEFT JOIN geography reg ON city.parent_id::text = reg.geo_id::text AND reg.geo_type::text = 'STATE/PROV'::text
    JOIN geography ctry ON city.country_id::text = ctry.geo_id::text AND ctry.geo_type::text = 'COUNTRY'::text
    JOIN facility_types ft ON f.id::text = ft.facility_id::text
WHERE f.status::text = 'Active'::text AND city.geo_type::text = 'CITY'::text
WITH DATA;

ALTER TABLE IF EXISTS public.mat_view_cities_and_facilities
    OWNER TO psqladminun;


CREATE INDEX mat_view_country_code_idx
    ON public.mat_view_cities_and_facilities USING btree
    (country_code COLLATE pg_catalog."default")
    TABLESPACE pg_default;
CREATE INDEX mat_view_country_name_upper_idx
    ON public.mat_view_cities_and_facilities USING btree
    (country_code COLLATE pg_catalog."default")
    TABLESPACE pg_default;
CREATE INDEX mat_view_geo_name_uppercase_idx
    ON public.mat_view_cities_and_facilities USING btree
    (city_name_upper_case COLLATE pg_catalog."default" text_pattern_ops)
    TABLESPACE pg_default;
CREATE INDEX mat_view_geo_type_idx
    ON public.mat_view_cities_and_facilities USING btree
    (geo_type COLLATE pg_catalog."default")
    TABLESPACE pg_default;
CREATE INDEX mat_view_region_code_idx
    ON public.mat_view_cities_and_facilities USING btree
    (region_code COLLATE pg_catalog."default")
    TABLESPACE pg_default;
CREATE INDEX mat_view_rkst_idx
    ON public.mat_view_cities_and_facilities USING btree
    (rkst COLLATE pg_catalog."default")
    TABLESPACE pg_default;
CREATE INDEX mat_view_rkts_idx
    ON public.mat_view_cities_and_facilities USING btree
    (rkts COLLATE pg_catalog."default")
    TABLESPACE pg_default;
CREATE INDEX mat_view_unloc_idx
    ON public.mat_view_cities_and_facilities USING btree
    (unloc COLLATE pg_catalog."default")
    TABLESPACE pg_default;
CREATE INDEX mat_view_unloc_lookup_idx
    ON public.mat_view_cities_and_facilities USING btree
    (unloc_lookup COLLATE pg_catalog."default")
    TABLESPACE pg_default;
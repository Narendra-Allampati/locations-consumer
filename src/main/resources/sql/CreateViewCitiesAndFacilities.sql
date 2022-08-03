DROP VIEW IF EXISTS VIEW_CITIES_AND_FACILITIES;

CREATE VIEW VIEW_CITIES_AND_FACILITIES
AS
-- Get a list of Cities
SELECT
    geo.geo_id, geo.name AS city_name, geo.name_upper_case AS city_name_upper_case, ctry.rkst AS country_code, geo.country_id AS country_geo_id,
    geo.country_name, geo.country_name_upper_case, geo.geo_type,
    geo.name AS locality_name, geo.rkst, geo.rkts, reg.iso_territory AS region_code,
    reg.name AS region_name, null AS site_name, geo.olson_time_zone,
    geo.unloc, geo.unloc_lookup, geo.unloc_return
FROM geography geo
         LEFT OUTER JOIN geography reg
                         ON geo.parent_id = reg.geo_id
                             AND reg.geo_type = 'STATE/PROV'
         INNER JOIN geography ctry
                    ON geo.country_id = ctry.geo_id
                        AND ctry.geo_type = 'COUNTRY'
WHERE geo.geo_type = 'CITY'
UNION ALL
-- Get list of facilities
SELECT
    f.geo_id, city.name AS city_name, city.name_upper_case AS city_name_upper_case, ctry.rkst AS country_code, city.country_id AS country_geo_id,
    city.country_name, city.country_name_upper_case, ft.name AS geo_type,
    -- ftcm.facility_type_code_mapping
    f.name AS locality_name, f.rkst, f.rkts, reg.iso_territory AS region_code,
    reg.name AS region_name, f.name AS site_name, city.olson_time_zone,
    f.unloc, f.unloc_lookup, f.unloc_return
FROM facilities f
         INNER JOIN geography city
                    ON f.parent_id = city.geo_id
         LEFT OUTER JOIN geography reg
                         ON city.parent_id = reg.geo_id
                             AND reg.geo_type = 'STATE/PROV'
         INNER JOIN geography ctry
                    ON city.country_id = ctry.geo_id
                        AND ctry.geo_type = 'COUNTRY'
         INNER JOIN facility_types ft
                    ON f.id = ft.facility_id
--  INNER JOIN facility_type_code_mapping ftcm
--          ON ft.code = ftcm.facility_type_code
WHERE f.status = 'Active'
  AND city.geo_type = 'CITY'
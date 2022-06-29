CREATE TABLE GEOGRAPHY
(
    GEO_ID                                  VARCHAR (14),
    GEO_TYPE                                VARCHAR (50),
    NAME                                   	VARCHAR (512),
    NAME_UPPER_CASE                       	VARCHAR (512),
    STATUS                                 	VARCHAR (512),
    OLSON_TIME_ZONE                         VARCHAR (100),
    COUNTRY_ID                              VARCHAR (14),
    COUNTRY_NAME                            VARCHAR (512),
    COUNTRY_NAME_UPPER_CASE                 VARCHAR (512),
    PARENT_ID                               VARCHAR (14),
    PARENT_NAME                             VARCHAR (512),
    PARENT_TYPE                             VARCHAR (100),
    FIPS                             	    VARCHAR (100),
    RKST                             	    VARCHAR (100),
    RKTS                             	    VARCHAR (100),
    UNLOC                             	    VARCHAR (100),
    UNLOC_LOOKUP                            VARCHAR (100),
    UNLOC_RETURN                            VARCHAR (100),
    ISO_TERRITORY                           VARCHAR (100),
    VALID_FROM                              TIMESTAMP,
    VALID_TO                                TIMESTAMP,
    LONGITUDE                              	VARCHAR (512),
    LATITUDE                               	VARCHAR (512),
    TIME_ZONE                               VARCHAR (20),
    DAYLIGHT_SAVING_TIME                   	VARCHAR (20),
    UTC_OFFSET_MINUTES                     	VARCHAR (20),
    DAYLIGHT_SAVING_START                   TIMESTAMP,
    DAYLIGHT_SAVING_END                     TIMESTAMP,
    DAYLIGHT_SAVING_SHIFT_MINUTES    		VARCHAR (20),
    DESCRIPTION                             VARCHAR (512),
    WORKAROUND_REASON                  		VARCHAR (512),
    RESTRICTED                              VARCHAR (512),
    POSTAL_CODE_MANDATORY            		VARCHAR (20),
    STATE_PROVINCE_MANDATORY       			VARCHAR (20),
    DIALING_CODE                            VARCHAR (20),
    DIALING_CODE_DESCRIPTION            	VARCHAR (20),
    PORT_FLAG                               VARCHAR (20),
    BDA_TYPE                                VARCHAR (20),
    HSUD_NAME                            	VARCHAR (512),
    SUB_CITY_PARENT_ID                      VARCHAR (14),
    SUB_CITY_PARENT_NAME                    VARCHAR (512),
    SUB_CITY_PARENT_TYPE                    VARCHAR (100),
    CONSTRAINT GEO_ID_PK PRIMARY KEY (GEO_ID));

CREATE INDEX IF NOT EXISTS GEO_TYPE_GEO_IDX ON GEOGRAPHY USING btree (GEO_TYPE);
CREATE INDEX IF NOT EXISTS GEO_NAME_UPPERCASE_IDX ON GEOGRAPHY USING btree (NAME_UPPER_CASE text_pattern_ops ASC NULLS LAST);
CREATE INDEX IF NOT EXISTS RKTS_IDX ON GEOGRAPHY USING btree (RKTS);
CREATE INDEX IF NOT EXISTS RKST_IDX ON GEOGRAPHY USING btree (RKST);
CREATE INDEX IF NOT EXISTS UNLOC_IDX ON GEOGRAPHY USING btree (UNLOC);
CREATE INDEX IF NOT EXISTS UNLOC_LOOKUP_IDX ON GEOGRAPHY USING btree (UNLOC_LOOKUP);
CREATE INDEX IF NOT EXISTS GEO_TYPE_IDX ON GEOGRAPHY USING btree (GEO_TYPE);
CREATE INDEX IF NOT EXISTS FIPS_IDX ON GEOGRAPHY USING btree (FIPS);
CREATE INDEX IF NOT EXISTS ISO_TERRITORY_IDX ON GEOGRAPHY USING btree (ISO_TERRITORY);
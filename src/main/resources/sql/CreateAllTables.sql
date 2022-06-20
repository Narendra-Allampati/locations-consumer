DROP TABLE IF EXISTS ADDRESSES, CONTACT_DETAILS, FACILITIES, FACILITY_ALTERNATE_CODES, FACILITY_DETAILS, FACILITY_SERVICES, FACILITY_TYPE_LINKS, FACILITY_TYPES, FENCES, OPENING_HOURS, TRANSPORT_MODES, ALTERNATE_CODES, ALTERNATE_NAMES, GEO_ALTERNATE_CODE_LINKS, GEOGRAPHY, POSTAL_CODE;

CREATE TABLE GEOGRAPHY
(
    GEO_ID                                  VARCHAR (14),
    GEO_TYPE                                VARCHAR (50),
    NAME                                   	VARCHAR (512),
    STATUS                                 	VARCHAR (512),
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
    OLSON_TIME_ZONE                         VARCHAR (100),
    BDA_TYPE                                VARCHAR (20),
    HSUD_NAME                            	VARCHAR (512),
    COUNTRY_ID                              VARCHAR (14),
    COUNTRY_NAME                            VARCHAR (512),
    PARENT_ID                               VARCHAR (14),
    PARENT_NAME                             VARCHAR (512),
    PARENT_TYPE                             VARCHAR (100),
    SUB_CITY_PARENT_ID                      VARCHAR (14),
    SUB_CITY_PARENT_NAME                    VARCHAR (512),
    SUB_CITY_PARENT_TYPE                    VARCHAR (100),
    RKST                             	    VARCHAR (100),
    RKTS                             	    VARCHAR (100),
    UNLOC                             	    VARCHAR (100),
    UNLOC_LOOKUP                            VARCHAR (100),
    UNLOC_RETURN                            VARCHAR (100),
    ISO_TERRITORY                           VARCHAR (100),
    CONSTRAINT GEO_ID_PK PRIMARY KEY (GEO_ID));

CREATE TABLE FACILITIES
(
    ID                                              VARCHAR (50),
    NAME                                            VARCHAR (500),
    TYPE                                            VARCHAR (100),
    EXT_OWNED                                       VARCHAR (100),
    STATUS                                          VARCHAR (100),
    EXT_EXPOSED                                     VARCHAR (100),
    URL                                             VARCHAR (500),
    DEPARTMENT_OF_DEFENCE_ACTIVITY_ADDRESS_CODE     VARCHAR (100),
    PARENT_ID                                       VARCHAR (50),
    PARENT_NAME                                     VARCHAR (100),
    PARENT_TYPE                                     VARCHAR (100),
    CONSTRAINT FACILITY_ID_PK PRIMARY KEY (ID));

CREATE TABLE ADDRESSES
(
    ID                                  VARCHAR (50),
    FACILITY_ID                         VARCHAR (50),
    HOUSE_NUMBER                        VARCHAR (500),
    STREET                              VARCHAR (500),
    CITY                                VARCHAR (100),
    POSTAL_CODE                         VARCHAR (100),
    PO_BOX                              VARCHAR (100),
    DISTRICT                            VARCHAR (100),
    TERRITORY                           VARCHAR (100),
    COUNTRY_NAME                        VARCHAR (100),
    COUNTRY_CODE                        VARCHAR (100),
    ADDRESS_LINE2                       VARCHAR (200),
    ADDRESS_LINE3                       VARCHAR (100),
    LATITUDE                            VARCHAR (100),
    LONGITUDE                           VARCHAR (100),
    ADDRESS_QUALITY_CHECK_INDICATOR     VARCHAR (100),
    CONSTRAINT ADDRESSES_PK PRIMARY KEY (ID),
    CONSTRAINT FACILITY_FK FOREIGN KEY(FACILITY_ID)
    REFERENCES FACILITIES(ID)
    ON DELETE CASCADE);

CREATE INDEX IF NOT EXISTS ADDRESSES_IDX ON ADDRESSES (FACILITY_ID);

CREATE TABLE CONTACT_DETAILS
(
    ID                                  VARCHAR (50),
    FACILITY_ID                         VARCHAR (50),
    FIRST_NAME                          VARCHAR (100),
    LAST_NAME                           VARCHAR (100),
    JOB_TITLE                           VARCHAR (100),
    DEPARTMENT                          VARCHAR (100),
    INTERNATIONAL_DIALING_CODE_PHONE    VARCHAR (100),
    EXTENSION                           VARCHAR (100),
    PHONE_NUMBER                        VARCHAR (100),
    INTERNATIONAL_DIALING_CODE_MOBILE   VARCHAR (100),
    MOBILE_NUMBER                       VARCHAR (100),
    INTERNATIONAL_DIALING_CODE_FAX      VARCHAR (100),
    FAX_NUMBER                          VARCHAR (100),
    EMAIL_ADDRESS                       VARCHAR (100),
    VALID_THROUGH_DATE                  VARCHAR (100),
    CONSTRAINT CONTACT_DETAILS_PK PRIMARY KEY (ID),
    CONSTRAINT FACILITY_FK FOREIGN KEY(FACILITY_ID)
    REFERENCES FACILITIES(ID)
    ON DELETE CASCADE);

CREATE INDEX IF NOT EXISTS CONTACT_DETAILS_FACILITY_IDX ON CONTACT_DETAILS (FACILITY_ID);

CREATE TABLE FACILITY_ALTERNATE_CODES
(
    ID                                  UUID,
    FACILITY_ID                        	VARCHAR (14),
    CODE                             	VARCHAR (100),
    CODE_TYPE                           VARCHAR (100),
    CONSTRAINT FACILITY_ALTERNATE_CODES_PK PRIMARY KEY (ID),
    CONSTRAINT FACILITY_FK FOREIGN KEY(FACILITY_ID)
    REFERENCES FACILITIES(ID)
    ON DELETE CASCADE);

CREATE INDEX IF NOT EXISTS FACILITY_ALTERNATE_CODES_FACILITY_IDX ON FACILITY_ALTERNATE_CODES (FACILITY_ID);

CREATE TABLE FACILITY_DETAILS
(
    ID                                   VARCHAR (50),
    FACILITY_ID                          VARCHAR (50),
    WEIGHT_LIMIT_CRANE_KG                VARCHAR (20),
    WEIGHT_LIMIT_YARD_KG                 VARCHAR (100),
    VESSEL_AGENT                         VARCHAR (100),
    GPS_FLAG                             VARCHAR (100),
    GSM_FLAG                             VARCHAR (100),
    OCEAN_FREIGHT_PRICING                VARCHAR (100),
    BRAND                                VARCHAR (100),
    COMM_FACILITY_TYPE                   VARCHAR (100),
    EXPORT_ENQUIRIES_EMAIL               VARCHAR (100),
    IMPORT_ENQUIRIES_EMAIL               VARCHAR (100),
    FACILITY_FUNCTION                    VARCHAR (100),
    FACILITY_FUNCTION_DESCRIPTION        VARCHAR (100),
    INTERNATIONAL_DIAL_CODE              VARCHAR (100),
    TELEPHONE_NUMBER                     VARCHAR (100),
    CONSTRAINT FACILITY_DETAILS_PK PRIMARY KEY (ID),
    CONSTRAINT FACILITY_FK FOREIGN KEY(FACILITY_ID)
    REFERENCES FACILITIES(ID)
    ON DELETE CASCADE);

CREATE INDEX IF NOT EXISTS FACILITY_DETAILS_FACILITY_IDX ON FACILITY_DETAILS (FACILITY_ID);

CREATE TABLE FACILITY_SERVICES
(
    ID                                   VARCHAR (50),
    FACILITY_ID                          VARCHAR (50),
    SERVICE_NAME                         VARCHAR (100),
    SERVICE_CODE                         VARCHAR (100),
    SERVICE_DESCRIPTION                  VARCHAR (100),
    VALID_THROUGH_DATE                   TIMESTAMP,
    CONSTRAINT FACILITY_SERVICES_PK PRIMARY KEY (ID),
    CONSTRAINT FACILITY_FK FOREIGN KEY(FACILITY_ID)
    REFERENCES FACILITIES(ID)
    ON DELETE CASCADE);

CREATE INDEX IF NOT EXISTS FACILITY_SERVICES_IDX ON FACILITY_SERVICES (SERVICE_NAME);
CREATE INDEX IF NOT EXISTS FACILITY_SERVICES_FACILITY_IDX ON FACILITY_SERVICES (FACILITY_ID);

CREATE TABLE FACILITY_TYPE_LINKS
(
    FACILITY_ID                        	VARCHAR (14),
    OPS_FACILITY_TYPE_CODE              VARCHAR (100),
    PRIMARY KEY (FACILITY_ID, OPS_FACILITY_TYPE_CODE));

CREATE TABLE FACILITY_TYPES
(
    ID                  VARCHAR (50),
    FACILITY_ID         VARCHAR (50),
    CODE                VARCHAR (50),
    NAME                VARCHAR (100),
    MASTER_TYPE         VARCHAR (100),
    VALID_THROUGH_DATE  TIMESTAMP,
    CONSTRAINT FACILITY_TYPES_PK PRIMARY KEY (ID),
    CONSTRAINT FACILITY_FK FOREIGN KEY(FACILITY_ID)
    REFERENCES FACILITIES(ID)
    ON DELETE CASCADE);

CREATE INDEX IF NOT EXISTS FACILITY_TYPES_IDX ON FACILITY_TYPES (CODE);

CREATE INDEX IF NOT EXISTS FACILITY_TYPES_FACILITY_IDX ON FACILITY_TYPES (FACILITY_ID);

CREATE TABLE FENCES
(
    ID                VARCHAR (50),
    FACILITY_ID       VARCHAR (50),
    NAME              VARCHAR (50),
    FENCE_TYPE        VARCHAR (100),
    CONSTRAINT FENCES_PK PRIMARY KEY (ID),
    CONSTRAINT FACILITY_FK FOREIGN KEY(FACILITY_ID)
    REFERENCES FACILITIES(ID)
    ON DELETE CASCADE);

CREATE INDEX IF NOT EXISTS FENCES_IDX ON FENCES (FENCE_TYPE);
CREATE INDEX IF NOT EXISTS FENCES_FACILITY_IDX ON FENCES (FACILITY_ID);

CREATE TABLE OPENING_HOURS
(
    ID                              VARCHAR (50),
    FACILITY_ID                     VARCHAR (50),
    DAY                             VARCHAR (20),
    OPEN_TIME_HOURS                 VARCHAR (100),
    OPEN_TIME_MINUTES               VARCHAR (100),
    CLOSE_TIME_HOURS                VARCHAR (100),
    CLOSE_TIME_MINUTES              VARCHAR (100),
    CONSTRAINT OPENING_HOURS_PK PRIMARY KEY (ID),
    CONSTRAINT FACILITY_FK FOREIGN KEY(FACILITY_ID)
    REFERENCES FACILITIES(ID)
    ON DELETE CASCADE);

CREATE INDEX IF NOT EXISTS OPENING_HOURS_FACILITY_IDX ON OPENING_HOURS (FACILITY_ID);

CREATE TABLE TRANSPORT_MODES
(
    ID                             VARCHAR (50),
    FACILITY_ID                    VARCHAR (50),
    MODE_OF_TRANSPORT              VARCHAR (50),
    TRANSPORT_CODE                 VARCHAR (100),
    TRANSPORT_DESCRIPTION          VARCHAR (100),
    VALID_THROUGH_DATE             TIMESTAMP,
    CONSTRAINT TRANSPORT_MODES_PK PRIMARY KEY (ID),
    CONSTRAINT FACILITY_FK FOREIGN KEY(FACILITY_ID)
    REFERENCES FACILITIES(ID)
    ON DELETE CASCADE);

CREATE INDEX IF NOT EXISTS TRANSPORT_MODES_FACILITY_IDX ON TRANSPORT_MODES (FACILITY_ID);

CREATE TABLE ALTERNATE_CODES
(
    ID                                  UUID,
    GEO_ID								VARCHAR (100),
    CODE                             	VARCHAR (100),
    CODE_TYPE                           VARCHAR (100),
    CONSTRAINT ALTERNATE_CODES_PK PRIMARY KEY (ID),
    CONSTRAINT GEOGRAPHY_FK FOREIGN KEY(GEO_ID)
    REFERENCES GEOGRAPHY(GEO_ID)
    ON DELETE CASCADE);

CREATE INDEX IF NOT EXISTS ALTERNATE_CODES_GEO_IDX ON ALTERNATE_CODES (GEO_ID);

CREATE TABLE ALTERNATE_NAMES
(
    ID                                      UUID,
    GEO_ID									VARCHAR (100),
    NAME                              		VARCHAR (100),
    DESCRIPTION	                            VARCHAR (100),
    STATUS									VARCHAR (100),
    CONSTRAINT ALTERNATE_NAMES_PK PRIMARY KEY (ID),
    CONSTRAINT GEOGRAPHY_FK FOREIGN KEY(GEO_ID)
    REFERENCES GEOGRAPHY(GEO_ID)
    ON DELETE CASCADE);

CREATE INDEX IF NOT EXISTS ALTERNATE_NAMES_GEO_IDX ON ALTERNATE_NAMES (GEO_ID);

CREATE TABLE GEO_ALTERNATE_CODE_LINKS
(
    ID                                  UUID,
    GEO_ID                             	VARCHAR (14),
    ALTERNATE_CODE_ID                   VARCHAR (100),
    ALTERNATE_CODE_TYPE                 VARCHAR (100),
    CONSTRAINT GEO_ALTERNATE_CODE_LINKS_PK PRIMARY KEY (ID),
    CONSTRAINT GEOGRAPHY_FK FOREIGN KEY(GEO_ID)
    REFERENCES GEOGRAPHY(GEO_ID)
    ON DELETE CASCADE);

CREATE TABLE POSTAL_CODE
(
    GEO_ID                                  VARCHAR (14),
    GEO_TYPE                                VARCHAR (50),
    NAME                                   	VARCHAR (512),
    STATUS                                 	VARCHAR (512),
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
    OLSON_TIME_ZONE                         VARCHAR (100),
    BDA_TYPE                                VARCHAR (20),
    HSUD_NAME                            	VARCHAR (512),
    COUNTRY_ID                              VARCHAR (14),
    COUNTRY_NAME                            VARCHAR (512),
    PARENT_ID                               VARCHAR (14),
    PARENT_NAME                             VARCHAR (512),
    PARENT_TYPE                             VARCHAR (100),
    SUB_CITY_PARENT_ID                      VARCHAR (14),
    SUB_CITY_PARENT_NAME                    VARCHAR (512),
    SUB_CITY_PARENT_TYPE                    VARCHAR (100),
    CONSTRAINT POSTAL_CODE_ID_PK PRIMARY KEY (GEO_ID));
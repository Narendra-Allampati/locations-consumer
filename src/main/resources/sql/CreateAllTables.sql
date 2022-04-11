DROP TABLE ADDRESSES, CONTACT_DETAILS, FACILITIES, FACILITY_ALTERNATE_CODE_LINKS, FACILITY_DETAILS, FACILITY_SERVICES, FACILITY_TYPE_LINKS, FACILITY_TYPES, FENCES, OPENING_HOURS, TRANSPORT_MODES, ALTERNATE_CODES, ALTERNATE_NAMES, GEO_ALTERNATE_CODE_LINKS, GEOGRAPHY;

CREATE TABLE ADDRESSES
(
    ID                                  VARCHAR (50),
    HOUSE_NUMBER                        VARCHAR (500),
    STREET                              VARCHAR (500),
    CITY                                VARCHAR (100),
    POSTAL_CODE                         VARCHAR (100),
    PO_BOX                              VARCHAR (100),
    DISTRICT                            VARCHAR (100),
    TERRITORY                           VARCHAR (100),
    COUNTRY_NAME                        VARCHAR (100),
    COUNTRY_CODE                        VARCHAR (100),
    ADDRESS_LINE2                       VARCHAR (500),
    ADDRESS_LINE3                       VARCHAR (100),
    LATITUDE                            VARCHAR (100),
    LONGITUDE                           VARCHAR (100),
    ADDRESS_QUALITY_CHECK_INDICATOR     VARCHAR (100));

CREATE TABLE CONTACT_DETAILS
(
    ID                                  VARCHAR (50),
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
    VALID_THROUGH_DATE                  VARCHAR (100));

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
    PARENT_NAME                                     VARCHAR (100),
    PARENT_TYPE                                     VARCHAR (100));

CREATE TABLE FACILITY_ALTERNATE_CODE_LINKS
(
    FACILITY_ID                        	VARCHAR (14),
    ALTERNATE_CODE_ID                   VARCHAR (100),
    PRIMARY KEY (FACILITY_ID, ALTERNATE_CODE_ID));

CREATE TABLE FACILITY_DETAILS
(
    ID                                   VARCHAR (50),
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
    TELEPHONE_NUMBER                     VARCHAR (100));

CREATE TABLE FACILITY_SERVICES
(
    ID                                   VARCHAR (50),
    SERVICE_NAME                         VARCHAR (100),
    SERVICE_CODE                         VARCHAR (100),
    SERVICE_DESCRIPTION                  VARCHAR (100),
    VALID_THROUGH_DATE                   TIMESTAMP);

CREATE TABLE FACILITY_TYPE_LINKS
(
    FACILITY_ID                        	VARCHAR (14),
    OPS_FACILITY_TYPE_CODE              VARCHAR (100),
    PRIMARY KEY (FACILITY_ID, OPS_FACILITY_TYPE_CODE));

CREATE TABLE FACILITY_TYPES
(
    ID                  VARCHAR (50),
    CODE                VARCHAR (50),
    NAME                VARCHAR (100),
    MASTER_TYPE         VARCHAR (100),
    VALID_THROUGH_DATE  TIMESTAMP );

CREATE TABLE FENCES
(
    ID                VARCHAR (50),
    NAME              VARCHAR (50),
    FENCE_TYPE        VARCHAR (100));

CREATE TABLE OPENING_HOURS
(
    ID                              VARCHAR (50),
    DAY                             VARCHAR (20),
    OPEN_TIME_HOURS                 VARCHAR (100),
    OPEN_TIME_MINUTES               VARCHAR (100),
    CLOSE_TIME_HOURS                VARCHAR (100),
    CLOSE_TIME_MINUTES              VARCHAR (100));

CREATE TABLE TRANSPORT_MODES
(
    ID                             VARCHAR (50),
    MODE_OF_TRANSPORT              VARCHAR (50),
    TRANSPORT_CODE                 VARCHAR (100),
    TRANSPORT_DESCRIPTION          VARCHAR (100),
    VALID_THROUGH_DATE             TIMESTAMP);

CREATE TABLE ALTERNATE_CODES
(
    CODE                             	VARCHAR (100),
    CODE_TYPE                           VARCHAR (100));

CREATE TABLE ALTERNATE_NAMES
(
    ID                                      UUID,
    GEO_ID									VARCHAR (100),
    NAME                              		VARCHAR (100),
    DESCRIPTION	                            VARCHAR (100),
    STATUS									VARCHAR (100));

CREATE TABLE GEO_ALTERNATE_CODE_LINKS
(
    ID                                  UUID,
    GEO_ID                             	VARCHAR (14),
    ALTERNATE_CODE_ID                   VARCHAR (100));

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
    SUB_CITY_PARENT_TYPE                    VARCHAR (100));
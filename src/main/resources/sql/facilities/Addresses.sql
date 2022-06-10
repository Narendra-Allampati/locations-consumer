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
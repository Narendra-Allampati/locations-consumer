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
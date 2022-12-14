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
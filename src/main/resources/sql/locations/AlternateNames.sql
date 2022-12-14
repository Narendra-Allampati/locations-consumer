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
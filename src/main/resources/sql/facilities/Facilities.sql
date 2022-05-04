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
CREATE TABLE CYCLE (
  ID VARCHAR(36)   NOT NULL ,
  NAME VARCHAR(25)   NOT NULL ,
  PRIORITY INTEGER   NOT NULL ,
  DEFAULT_CYCLE INTEGER   NOT NULL   ,
PRIMARY KEY(ID)  );


CREATE UNIQUE INDEX CYCLE_NAME ON CYCLE (NAME);



CREATE TABLE LOGIN_USER (
  ID VARCHAR(36)   NOT NULL ,
  NAME VARCHAR(20)   NOT NULL ,
  PASSWORD_HASH VARCHAR(128)   NOT NULL ,
  ALGORITHM VARCHAR(15)    ,
  LANGUAGE VARCHAR(2)      ,
PRIMARY KEY(ID)  );


CREATE UNIQUE INDEX LOGIN_USER_NAME ON LOGIN_USER (NAME);



CREATE TABLE CONFIGURATION (
  ID VARCHAR(36)   NOT NULL ,
  RULE_KEY VARCHAR(15)   NOT NULL ,
  NAME VARCHAR(25)   NOT NULL   ,
PRIMARY KEY(ID)    );


CREATE UNIQUE INDEX CONFIGURATION_RULE_KEY ON CONFIGURATION (RULE_KEY);
CREATE UNIQUE INDEX CONFIGURATION_NAME ON CONFIGURATION (NAME);



CREATE TABLE BATCH_EXECUTION (
  ID VARCHAR(36)   NOT NULL ,
  NAME VARCHAR(15)   NOT NULL ,
  TIME DATETIME   NOT NULL ,
  STATE VARCHAR(15)   NOT NULL ,
  LOG_MESSAGE LONGTEXT      ,
PRIMARY KEY(ID)  );


CREATE UNIQUE INDEX BATCH_EXECUTION_NAME_TIME ON BATCH_EXECUTION (NAME, TIME);



CREATE TABLE BATCH_PARAMETER (
  NAME VARCHAR(15)   NOT NULL ,
  BATCH_PARAMETER_TYPE VARCHAR(15)   NOT NULL ,
  PARAMETER_VALUE VARCHAR(50)   NOT NULL ,
  STATE VARCHAR(15)    ,
  BATCH_EXECUTION_ID VARCHAR(36)   NOT NULL     ,
  FOREIGN KEY(BATCH_EXECUTION_ID)
    REFERENCES BATCH_EXECUTION(ID));


CREATE INDEX FK_BATCH_PARAMETER_BATCH_EXECUTION_ID ON BATCH_PARAMETER (BATCH_EXECUTION_ID);
CREATE UNIQUE INDEX BATCH_PARAMETER_NAME_BATCH_EXECUTION_ID ON BATCH_PARAMETER (NAME, BATCH_EXECUTION_ID);



CREATE TABLE DAY_GROUP (
  ID VARCHAR(36)   NOT NULL ,
  NAME VARCHAR(25)   NOT NULL ,
  READ_ONLY INTEGER   NOT NULL ,
  CYCLE_ID VARCHAR(36)      ,
PRIMARY KEY(ID)    ,
  FOREIGN KEY(CYCLE_ID)
    REFERENCES CYCLE(ID));


CREATE UNIQUE INDEX DAY_GROUP_NAME ON DAY_GROUP (NAME);
CREATE INDEX FK_CYCLE_ID ON DAY_GROUP (CYCLE_ID);



CREATE TABLE PARAMETER (
  ID VARCHAR(36)   NOT NULL ,
  PARAMETER_KEY VARCHAR(20)   NOT NULL ,
  PARAMETER_VALUE VARCHAR(50)   NOT NULL ,
  PARAMETER_TYPE VARCHAR(15)   NOT NULL ,
  CONFIGURATION_ID VARCHAR(36)   NOT NULL ,
  CYCLE_ID VARCHAR(36)      ,
PRIMARY KEY(ID)      ,
  FOREIGN KEY(CONFIGURATION_ID)
    REFERENCES CONFIGURATION(ID),
  FOREIGN KEY(CYCLE_ID)
    REFERENCES CYCLE(ID));


CREATE INDEX FK_PARAMETER_CONFIGURATION_ID ON PARAMETER (CONFIGURATION_ID);
CREATE INDEX FK_PARAMETER_CYCLE ON PARAMETER (CYCLE_ID);
CREATE UNIQUE INDEX PARAMETER_KEY_CONFIGURATION_CYCLE ON PARAMETER (PARAMETER_KEY, CONFIGURATION_ID, CYCLE_ID);



CREATE TABLE SPECIAL_DAY (
  ID VARCHAR(36)   NOT NULL ,
  DAY_TYPE VARCHAR(15)   NOT NULL ,
  DAY_VALUE INT   NOT NULL ,
  DESCRIPTION VARCHAR(25)    ,
  DAY_GROUP_ID VARCHAR(36)   NOT NULL   ,
PRIMARY KEY(ID)    ,
  FOREIGN KEY(DAY_GROUP_ID)
    REFERENCES DAY_GROUP(ID));


CREATE UNIQUE INDEX SPECIAL_DAY_TYP_VALUE ON SPECIAL_DAY (DAY_TYPE, DAY_VALUE);
CREATE INDEX FK_DAY_GROUP_ID ON SPECIAL_DAY (DAY_GROUP_ID);





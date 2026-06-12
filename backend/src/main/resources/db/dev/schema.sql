-- Subset of the Jira 10.x schema, recreated for standalone development on H2
-- (Oracle compatibility mode). Table and column names match the original Jira
-- schema so all repository SQL works unchanged against the real Oracle DB.

DROP TABLE IF EXISTS project;
DROP TABLE IF EXISTS jiraissue;
DROP TABLE IF EXISTS issuetype;
DROP TABLE IF EXISTS issuestatus;
DROP TABLE IF EXISTS priority;
DROP TABLE IF EXISTS resolution;
DROP TABLE IF EXISTS jiraaction;
DROP TABLE IF EXISTS app_user;
DROP TABLE IF EXISTS cwd_user;
DROP TABLE IF EXISTS label;
DROP TABLE IF EXISTS os_currentstep;
DROP TABLE IF EXISTS jiraworkflows;
DROP TABLE IF EXISTS workflowscheme;
DROP TABLE IF EXISTS workflowschemeentity;
DROP TABLE IF EXISTS nodeassociation;
DROP TABLE IF EXISTS issuelink;
DROP TABLE IF EXISTS issuelinktype;
DROP TABLE IF EXISTS customfield;
DROP TABLE IF EXISTS customfieldvalue;
DROP TABLE IF EXISTS fileattachment;

CREATE TABLE project (
    id            NUMBER(18,0) PRIMARY KEY,
    pname         VARCHAR2(255),
    url           VARCHAR2(255),
    lead          VARCHAR2(255),
    description   CLOB,
    pkey          VARCHAR2(255),
    pcounter      NUMBER(18,0),
    assigneetype  NUMBER(18,0),
    avatar        NUMBER(18,0),
    originalkey   VARCHAR2(255),
    projecttype   VARCHAR2(255)
);

CREATE TABLE jiraissue (
    id                   NUMBER(18,0) PRIMARY KEY,
    pkey                 VARCHAR2(255),
    issuenum             NUMBER(18,0),
    project              NUMBER(18,0),
    reporter             VARCHAR2(255),
    assignee             VARCHAR2(255),
    creator              VARCHAR2(255),
    issuetype            VARCHAR2(255),
    summary              VARCHAR2(255),
    description          CLOB,
    environment          CLOB,
    priority             VARCHAR2(255),
    resolution           VARCHAR2(255),
    issuestatus          VARCHAR2(255),
    created              TIMESTAMP,
    updated              TIMESTAMP,
    duedate              TIMESTAMP,
    resolutiondate       TIMESTAMP,
    votes                NUMBER(18,0),
    watches              NUMBER(18,0),
    timeoriginalestimate NUMBER(18,0),
    timeestimate         NUMBER(18,0),
    timespent            NUMBER(18,0),
    workflow_id          NUMBER(18,0),
    security             NUMBER(18,0),
    fixfor               NUMBER(18,0),
    component            NUMBER(18,0),
    archived             CHAR(1),
    archivedby           VARCHAR2(255),
    archiveddate         TIMESTAMP
);

CREATE TABLE issuetype (
    id          VARCHAR2(60) PRIMARY KEY,
    sequence    NUMBER(18,0),
    pname       VARCHAR2(60),
    pstyle      VARCHAR2(60),
    description CLOB,
    iconurl     VARCHAR2(255),
    avatar      NUMBER(18,0)
);

CREATE TABLE issuestatus (
    id             VARCHAR2(60) PRIMARY KEY,
    sequence       NUMBER(18,0),
    pname          VARCHAR2(60),
    description    CLOB,
    iconurl        VARCHAR2(255),
    statuscategory NUMBER(18,0)
);

CREATE TABLE priority (
    id           VARCHAR2(60) PRIMARY KEY,
    sequence     NUMBER(18,0),
    pname        VARCHAR2(60),
    description  CLOB,
    iconurl      VARCHAR2(255),
    status_color VARCHAR2(60)
);

CREATE TABLE resolution (
    id          VARCHAR2(60) PRIMARY KEY,
    sequence    NUMBER(18,0),
    pname       VARCHAR2(60),
    description CLOB,
    iconurl     VARCHAR2(255)
);

CREATE TABLE jiraaction (
    id           NUMBER(18,0) PRIMARY KEY,
    issueid      NUMBER(18,0),
    author       VARCHAR2(255),
    actiontype   VARCHAR2(255),
    actionlevel  VARCHAR2(255),
    rolelevel    NUMBER(18,0),
    actionbody   CLOB,
    created      TIMESTAMP,
    updateauthor VARCHAR2(255),
    updated      TIMESTAMP,
    actionnum    NUMBER(18,0)
);

CREATE TABLE app_user (
    id              NUMBER(18,0) PRIMARY KEY,
    user_key        VARCHAR2(255),
    lower_user_name VARCHAR2(255)
);

CREATE TABLE cwd_user (
    id                  NUMBER(18,0) PRIMARY KEY,
    directory_id        NUMBER(18,0),
    user_name           VARCHAR2(255),
    lower_user_name     VARCHAR2(255),
    active              NUMBER(9,0),
    created_date        TIMESTAMP,
    updated_date        TIMESTAMP,
    first_name          VARCHAR2(255),
    lower_first_name    VARCHAR2(255),
    last_name           VARCHAR2(255),
    lower_last_name     VARCHAR2(255),
    display_name        VARCHAR2(255),
    lower_display_name  VARCHAR2(255),
    email_address       VARCHAR2(255),
    lower_email_address VARCHAR2(255),
    external_id         VARCHAR2(255),
    credential          VARCHAR2(255)
);

CREATE TABLE label (
    id      NUMBER(18,0) PRIMARY KEY,
    fieldid NUMBER(18,0),
    issue   NUMBER(18,0),
    label   VARCHAR2(255)
);

CREATE TABLE os_currentstep (
    id          NUMBER(18,0) PRIMARY KEY,
    entry_id    NUMBER(18,0),
    step_id     NUMBER(9,0),
    action_id   NUMBER(9,0),
    owner       VARCHAR2(60),
    start_date  TIMESTAMP,
    due_date    TIMESTAMP,
    finish_date TIMESTAMP,
    status      VARCHAR2(60)
);

CREATE TABLE jiraworkflows (
    id           NUMBER(18,0) PRIMARY KEY,
    workflowname VARCHAR2(255),
    creatorname  VARCHAR2(255),
    descriptor   CLOB,
    islocked     VARCHAR2(60)
);

CREATE TABLE workflowscheme (
    id          NUMBER(18,0) PRIMARY KEY,
    name        VARCHAR2(255),
    description CLOB
);

CREATE TABLE workflowschemeentity (
    id        NUMBER(18,0) PRIMARY KEY,
    scheme    NUMBER(18,0),
    workflow  VARCHAR2(255),
    issuetype VARCHAR2(255)
);

CREATE TABLE nodeassociation (
    source_node_id     NUMBER(18,0),
    source_node_entity VARCHAR2(60),
    sink_node_id       NUMBER(18,0),
    sink_node_entity   VARCHAR2(60),
    association_type   VARCHAR2(60),
    sequence           NUMBER(9,0)
);

CREATE TABLE issuelinktype (
    id       NUMBER(18,0) PRIMARY KEY,
    linkname VARCHAR2(255),
    inward   VARCHAR2(255),
    outward  VARCHAR2(255),
    pstyle   VARCHAR2(60)
);

CREATE TABLE issuelink (
    id          NUMBER(18,0) PRIMARY KEY,
    linktype    NUMBER(18,0),
    source      NUMBER(18,0),
    destination NUMBER(18,0),
    sequence    NUMBER(18,0)
);

CREATE TABLE customfield (
    id                 NUMBER(18,0) PRIMARY KEY,
    customfieldtypekey VARCHAR2(255),
    cfname             VARCHAR2(255)
);

CREATE TABLE customfieldvalue (
    id          NUMBER(18,0) PRIMARY KEY,
    issue       NUMBER(18,0),
    customfield NUMBER(18,0),
    stringvalue VARCHAR2(255),
    numbervalue NUMBER(18,6),
    textvalue   CLOB,
    datevalue   TIMESTAMP,
    valuetype   VARCHAR2(255)
);

CREATE TABLE fileattachment (
    id            NUMBER(18,0) PRIMARY KEY,
    issueid       NUMBER(18,0),
    mimetype      VARCHAR2(255),
    filename      VARCHAR2(255),
    created       TIMESTAMP,
    filesize      NUMBER(18,0),
    author        VARCHAR2(255),
    zip           NUMBER(9,0),
    thumbnailable NUMBER(9,0)
);

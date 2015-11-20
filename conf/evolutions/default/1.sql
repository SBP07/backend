# --- !Ups

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Authentication tables
CREATE TABLE "auth_user" (
  "userID"    VARCHAR NOT NULL PRIMARY KEY,
  "firstName" VARCHAR,
  "lastName"  VARCHAR,
  "fullName"  VARCHAR,
  "email"     VARCHAR,
  "avatarURL" VARCHAR
);
CREATE TABLE "auth_logininfo" (
  "id"          BIGSERIAL NOT NULL PRIMARY KEY,
  "providerID"  VARCHAR   NOT NULL,
  "providerKey" VARCHAR   NOT NULL
);
CREATE TABLE "auth_userlogininfo" (
  "userID"      VARCHAR NOT NULL,
  "loginInfoId" BIGINT  NOT NULL
);
CREATE TABLE "auth_passwordinfo" (
  "hasher"      VARCHAR NOT NULL,
  "password"    VARCHAR NOT NULL,
  "salt"        VARCHAR,
  "loginInfoId" BIGINT  NOT NULL
);
CREATE TABLE "auth_oauth1info" (
  "id"          BIGSERIAL NOT NULL PRIMARY KEY,
  "token"       VARCHAR   NOT NULL,
  "secret"      VARCHAR   NOT NULL,
  "loginInfoId" BIGINT    NOT NULL
);
CREATE TABLE "auth_oauth2info" (
  "id"           BIGSERIAL NOT NULL PRIMARY KEY,
  "accesstoken"  VARCHAR   NOT NULL,
  "tokentype"    VARCHAR,
  "expiresin"    INTEGER,
  "refreshtoken" VARCHAR,
  "logininfoid"  BIGINT    NOT NULL
);
CREATE TABLE "auth_openidinfo" (
  "id"          VARCHAR NOT NULL PRIMARY KEY,
  "logininfoid" BIGINT  NOT NULL
);
CREATE TABLE "auth_openidattributes" (
  "id"    VARCHAR NOT NULL,
  "key"   VARCHAR NOT NULL,
  "value" VARCHAR NOT NULL
);
-- End authentication tables

CREATE TABLE tenant (
  id             UUID        NOT NULL DEFAULT uuid_generate_v1mc(),
  canonical_name VARCHAR(25) NOT NULL UNIQUE,
  name           VARCHAR(50) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE child (
  id               UUID         NOT NULL DEFAULT uuid_generate_v1mc(),
  first_name       VARCHAR(255) NOT NULL,
  last_name        VARCHAR(255) NOT NULL,

  birth_date       DATE,

  address_street   VARCHAR(255),
  address_number   VARCHAR(255),
  address_zip_code INT,
  address_city     VARCHAR(255),

  tenant_id        UUID,

  PRIMARY KEY (id)
);

CREATE TABLE crew (
  id               UUID         NOT NULL DEFAULT uuid_generate_v1mc(),
  first_name       VARCHAR(255) NOT NULL,
  last_name        VARCHAR(255) NOT NULL,
  birth_date       DATE,

  email            VARCHAR(255),

  address_street   VARCHAR(255),
  address_number   VARCHAR(255),
  address_zip_code INT,
  address_city     VARCHAR(255),

  tenant_id        UUID,

  PRIMARY KEY (id)
)

# --- !Downs

DROP TABLE child;

DROP TABLE tenant;

-- Authentication tables
DROP TABLE "auth_openidattributes";
DROP TABLE "auth_openidinfo";
DROP TABLE "auth_oauth2info";
DROP TABLE "auth_oauth1info";
DROP TABLE "auth_passwordinfo";
DROP TABLE "auth_userlogininfo";
DROP TABLE "auth_logininfo";
DROP TABLE "auth_user";
-- End authentication tables

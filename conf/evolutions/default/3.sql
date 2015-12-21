# --- !Ups
CREATE TABLE "activity_type" (
  id          UUID    NOT NULL PRIMARY KEY ,
  description VARCHAR NOT NULL,
  mnemonic    VARCHAR NOT NULL
);
# --- !Downs
DROP TABLE "activity_type";

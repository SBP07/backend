# --- !Ups
CREATE TABLE "activity_type" (
  id                    UUID    NOT NULL PRIMARY KEY DEFAULT uuid_generate_v1mc(),
  description           VARCHAR NOT NULL,
  mnemonic              VARCHAR NOT NULL,
  tenant_canonical_name VARCHAR NOT NULL
);
# --- !Downs
DROP TABLE "activity_type";

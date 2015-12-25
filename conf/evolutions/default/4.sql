# --- !Ups
CREATE TABLE "activity" (
  id                    UUID    NOT NULL PRIMARY KEY DEFAULT uuid_generate_v1mc(),
  place                 VARCHAR,
  activity_type_id      UUID    NOT NULL REFERENCES activity_type (id),
  date                  DATE    NOT NULL,
  start_time            TIMESTAMP,
  end_time              TIMESTAMP,
  tenant_canonical_name VARCHAR NOT NULL
);
# --- !Downs
DROP TABLE "activity";

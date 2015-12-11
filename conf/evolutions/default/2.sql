# --- !Ups
CREATE TABLE "child_to_contact_person" (
  child_id          UUID    NOT NULL REFERENCES child ("id"),
  contact_person_id UUID    NOT NULL REFERENCES contact_person ("id"),
  relationship      VARCHAR NOT NULL,
  PRIMARY KEY (child_id, contact_person_id)
);
# --- !Downs
DROP TABLE "child_to_contact_person";

# --- !Ups
CREATE TABLE "child_to_activity" (
  child_id       UUID NOT NULL REFERENCES child (id),
  activity_id    UUID NOT NULL REFERENCES activity (id),
  check_in_time  TIMESTAMP,
  check_out_time TIMESTAMP,
  PRIMARY KEY (child_id, activity_id)
);
# --- !Downs
DROP TABLE "child_to_activity";

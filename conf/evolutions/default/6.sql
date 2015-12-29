# --- !Ups
CREATE TABLE "crew_to_activity" (
  crew_id        UUID NOT NULL REFERENCES auth_user ("userID"),
  activity_id    UUID NOT NULL REFERENCES activity ("id"),
  check_in_time  TIMESTAMP,
  check_out_time TIMESTAMP,
  PRIMARY KEY (crew_id, activity_id)
);
# --- !Downs
DROP TABLE "crew_to_activity";

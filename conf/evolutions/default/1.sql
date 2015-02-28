# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "ACTIVITY" ("ID" BIGSERIAL NOT NULL PRIMARY KEY,"DATE" DATE,"PLACE" VARCHAR(254),"ACT_TYPE_NUM" BIGINT NOT NULL);
create table "ACTIVITY_TYPE" ("ID" BIGSERIAL NOT NULL PRIMARY KEY,"MNEMONIC" VARCHAR(254) NOT NULL,"DESCRIPTION" VARCHAR(254) NOT NULL);
create table "ANIMATORS" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"first_name" VARCHAR(254) NOT NULL,"last_name" VARCHAR(254) NOT NULL,"mobile_phone" VARCHAR(254),"landline" VARCHAR(254),"email" VARCHAR(254),"street" VARCHAR(254),"city" VARCHAR(254),"bank_account" VARCHAR(254),"year_started_volunteering" INTEGER,"is_core" BOOLEAN NOT NULL,"birthdate" DATE);
create table "CHILDREN" ("ID" BIGSERIAL NOT NULL PRIMARY KEY,"FIRST_NAME" VARCHAR(254) NOT NULL,"LAST_NAME" VARCHAR(254) NOT NULL,"MOBILE_PHONE" VARCHAR(254),"LANDLINE" VARCHAR(254),"STREET" VARCHAR(254),"CITY" VARCHAR(254),"BIRTHDATE" DATE,"MED_REC_CHECKED" DATE);
create table "child_to_activity" ("child_id" BIGINT NOT NULL,"activity_id" BIGINT NOT NULL);
alter table "child_to_activity" add constraint "child_to_activity_pk" primary key("child_id","activity_id");
alter table "ACTIVITY" add constraint "FK_ACT_TYPE" foreign key("ACT_TYPE_NUM") references "ACTIVITY_TYPE"("ID") on update NO ACTION on delete NO ACTION;
alter table "child_to_activity" add constraint "activity_fk" foreign key("activity_id") references "ACTIVITY"("ID") on update NO ACTION on delete NO ACTION;
alter table "child_to_activity" add constraint "child_fk" foreign key("child_id") references "CHILDREN"("ID") on update NO ACTION on delete NO ACTION;

# --- !Downs

alter table "child_to_activity" drop constraint "activity_fk";
alter table "child_to_activity" drop constraint "child_fk";
alter table "ACTIVITY" drop constraint "FK_ACT_TYPE";
alter table "child_to_activity" drop constraint "child_to_activity_pk";
drop table "child_to_activity";
drop table "CHILDREN";
drop table "ANIMATORS";
drop table "ACTIVITY_TYPE";
drop table "ACTIVITY";


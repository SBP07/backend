# --- !Ups

create table "animator" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"first_name" VARCHAR(254) NOT NULL,"last_name" VARCHAR(254) NOT NULL,"mobile_phone" VARCHAR(254),"landline" VARCHAR(254),"email" VARCHAR(254),"street" VARCHAR(254),"city" VARCHAR(254),"bank_account" VARCHAR(254),"year_started_volunteering" INTEGER,"is_core" BOOLEAN NOT NULL,"birthdate" DATE);
create table "child" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"first_name" VARCHAR(254) NOT NULL,"last_name" VARCHAR(254) NOT NULL,"mobile_phone" VARCHAR(254),"landline" VARCHAR(254),"street" VARCHAR(254),"city" VARCHAR(254),"birth_date" DATE,"medical_file_checked" DATE);
create table "child_to_shift" ("child_id" BIGINT NOT NULL,"shift_id" BIGINT NOT NULL);
alter table "child_to_shift" add constraint "child_to_shift_pk" primary key("child_id","shift_id");
create table "medical_file" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"first_name" VARCHAR(254) NOT NULL,"last_name" VARCHAR(254) NOT NULL,"street" VARCHAR(254) NOT NULL,"city" VARCHAR(254) NOT NULL,"blood_type" VARCHAR(254),"is_male" BOOLEAN NOT NULL,"allergic_to_dust" BOOLEAN NOT NULL,"allergic_to_face_paint" BOOLEAN NOT NULL,"allergic_to_bees" BOOLEAN NOT NULL,"other_allergies" VARCHAR(254),"has_asthma" BOOLEAN NOT NULL,"has_hay_fever" BOOLEAN NOT NULL,"has_epilepsy" BOOLEAN NOT NULL,"has_diabetes" BOOLEAN NOT NULL,"other_conditions" VARCHAR(254),"extra_information" VARCHAR(254),"birthdate" DATE);
create table "shift" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"date" DATE,"place" VARCHAR(254),"shift_type" BIGINT NOT NULL);
create table "shift_type" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"mnemonic" VARCHAR(254) NOT NULL,"description" VARCHAR(254) NOT NULL);
alter table "child_to_shift" add constraint "child_fk" foreign key("child_id") references "child"("id") on update NO ACTION on delete NO ACTION;
alter table "child_to_shift" add constraint "shift_fk" foreign key("shift_id") references "shift"("id") on update NO ACTION on delete CASCADE;
alter table "shift" add constraint "fk_shift_type" foreign key("shift_type") references "shift_type"("id") on update NO ACTION on delete NO ACTION;

# --- !Downs

alter table "shift" drop constraint "fk_shift_type";
alter table "child_to_shift" drop constraint "child_fk";
alter table "child_to_shift" drop constraint "shift_fk";
drop table "shift_type";
drop table "shift";
drop table "medical_file";
alter table "child_to_shift" drop constraint "child_to_shift_pk";
drop table "child_to_shift";
drop table "child";
drop table "animator";


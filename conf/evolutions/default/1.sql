# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table kind (
  id                        bigint not null,
  voornaam                  varchar(255),
  achternaam                varchar(255),
  gsmnummer                 varchar(255),
  thuistelefoon             varchar(255),
  straat_en_nummer          varchar(255),
  gemeente                  varchar(255),
  geboortedatum             timestamp,
  medische_fiche_in_orde    boolean,
  medische_fiche_gecontroleerd timestamp,
  constraint pk_kind primary key (id))
;

create sequence kind_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists kind;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists kind_seq;


# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table aanwezigheden (
  id                        bigint not null,
  constraint pk_aanwezigheden primary key (id))
;

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
  aanwezigheden_id          bigint,
  constraint pk_kind primary key (id))
;

create sequence aanwezigheden_seq;

create sequence kind_seq;

alter table kind add constraint fk_kind_aanwezigheden_1 foreign key (aanwezigheden_id) references aanwezigheden (id) on delete restrict on update restrict;
create index ix_kind_aanwezigheden_1 on kind (aanwezigheden_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists aanwezigheden;

drop table if exists kind;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists aanwezigheden_seq;

drop sequence if exists kind_seq;


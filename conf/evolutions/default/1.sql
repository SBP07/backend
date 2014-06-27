# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table dag (
  dag                       timestamp not null,
  constraint pk_dag primary key (dag))
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
  constraint pk_kind primary key (id))
;


create table kind_dag (
  kind_id                        bigint not null,
  dag_dag                        timestamp not null,
  constraint pk_kind_dag primary key (kind_id, dag_dag))
;
create sequence dag_seq;

create sequence kind_seq;




alter table kind_dag add constraint fk_kind_dag_kind_01 foreign key (kind_id) references kind (id) on delete restrict on update restrict;

alter table kind_dag add constraint fk_kind_dag_dag_02 foreign key (dag_dag) references dag (dag) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists dag;

drop table if exists kind_dag;

drop table if exists kind;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists dag_seq;

drop sequence if exists kind_seq;


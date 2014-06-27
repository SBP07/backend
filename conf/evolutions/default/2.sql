# --- !Ups

INSERT INTO Kind (id, voornaam, achternaam, straat_en_nummer, gemeente, geboortedatum) VALUES (1, 'Thomas', 'Toye', 'Teststraat 13', 'Teststad', NULL);
INSERT INTO Kind (id, voornaam, achternaam, straat_en_nummer, gemeente, geboortedatum) VALUES (2, 'Sander', 'Nogtest', 'Eenstraat 200', 'Nogtest', '2000-10-12 00:00:00');
INSERT INTO Kind (id, voornaam, achternaam, straat_en_nummer, gemeente, geboortedatum) VALUES (3, 'Annelies', 'Test', 'Straat 97', 'Eenstad', '2006-05-05 00:00:00');
INSERT INTO Kind (id, voornaam, achternaam, straat_en_nummer, gemeente, geboortedatum) VALUES (4, 'Marie', 'Meertest', 'Diestraat 555', 'Nogstad', '1999-03-09 00:00:00');



INSERT INTO DAG(DAG) VALUES ('2014-04-07 00:00:00');
INSERT INTO DAG(DAG) VALUES ('2014-04-08 00:00:00');
INSERT INTO DAG(DAG) VALUES ('2014-04-09 00:00:00');
INSERT INTO DAG(DAG) VALUES ('2014-04-10 00:00:00');
INSERT INTO DAG(DAG) VALUES ('2014-04-11 00:00:00');

INSERT INTO KIND_DAG(DAG_DAG, KIND_ID) VALUES ('2014-04-07 00:00:00', 1);
INSERT INTO KIND_DAG(DAG_DAG, KIND_ID) VALUES ('2014-04-08 00:00:00', 1);
INSERT INTO KIND_DAG(DAG_DAG, KIND_ID) VALUES ('2014-04-09 00:00:00', 1);
INSERT INTO KIND_DAG(DAG_DAG, KIND_ID) VALUES ('2014-04-10 00:00:00', 1);
INSERT INTO KIND_DAG(DAG_DAG, KIND_ID) VALUES ('2014-04-11 00:00:00', 1);

INSERT INTO KIND_DAG(DAG_DAG, KIND_ID) VALUES ('2014-04-07 00:00:00', 2);
INSERT INTO KIND_DAG(DAG_DAG, KIND_ID) VALUES ('2014-04-10 00:00:00', 2);
INSERT INTO KIND_DAG(DAG_DAG, KIND_ID) VALUES ('2014-04-11 00:00:00', 2);

INSERT INTO KIND_DAG(DAG_DAG, KIND_ID) VALUES ('2014-04-07 00:00:00', 3);
INSERT INTO KIND_DAG(DAG_DAG, KIND_ID) VALUES ('2014-04-08 00:00:00', 3);


# --- !Downs

DELETE FROM KIND_DAG;
DELETE FROM KIND;
DELETE FROM DAG;
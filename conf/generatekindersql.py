#!/usr/bin/python

# NOTICE
# This script is prone to all sorts of attacks. It is NOT meant to be used programmatically. It's only use case is to generate SQL that a developer should than check himself before executing it manually.
# Put otherwise: DO NOT USE IN PRODUCTION

import csv
import datetime
import time

kinderen = []
aanwezigheden = []

with open('kinderen.csv', newline='') as f:
    dialect = csv.Sniffer().sniff(f.read(1024))
    f.seek(0)
    reader = csv.reader(f, dialect)
    for row in reader:
        #print(row)
        if(row[7] == ''):
            row[7] = "NULL"
        else:
            split = row[7].split('/')
            if len(split) >= 3:
                geb = datetime.date(int(split[2]), int(split[0]), int(split[1]))
                row[7] = "'" + geb.strftime('%Y-%m-%d %H:%M:%S') + "'"
            else:
                row[7] = 'NULL'
        
        row[6] = row[6].capitalize()
        if row[6].lower() == 'beveren leie':
            row[6] = 'Beveren-Leie'

        kind = ("INSERT INTO Kind (id, voornaam, achternaam, straat_en_nummer, gemeente, geboortedatum) VALUES ({}, '{}', '{}', '{}', '{}', {});").format(row[0], row[3].capitalize(), row[2].capitalize(), row[4].capitalize(), row[6], row[7]);
        kinderen.append(kind)
        
        for i in range(0, 5):
            dag = row[8+i]
            datum = datetime.date(2014, 4, 7+i)
            datum = "'" + datum.strftime('%Y-%m-%d %H:%M:%S') + "'"
            if (dag == '1'):
                insert = ("INSERT INTO DAG_KIND(DAG_DAG, KIND_ID) VALUES ({}, {});").format(datum, row[0])
                aanwezigheden.append(insert)

for kind in kinderen:
    print(kind)

print('\n\n', '* - ' * 40, '\n\n')

for aanw in aanwezigheden:
    print(aanw)



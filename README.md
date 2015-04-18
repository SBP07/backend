# Speelsysteem

This is the system that will power Speelplein De Speelberg. Since it's created for a Dutch audience and at the moment is too specific to be helpful for others, instructions will be given only in Dutch.

## Doelen

### Minimum

- [x] Een animator moet nieuwe kinderen kunnen toevoegen
- [x] Een animator moet bestaande kinderen kunnen wijzigen
- [ ] Een animator moet bestaande kinderen kunnen verwijderen
- [x] Een animator moet nieuwe animatoren kunnen toevoegen
- [x] Een animator moet bestaande animatoren kunnen wijzigen
- [ ] Een animator moet bestaande animatoren kunnen verwijderen

### Dagdelen

- [x] Een animator moet een nieuw dagdeel kunnen aanmaken met een bepaald type (voormiddag, middag, namiddag)
- [x] Een animator moet een externe activiteit kunnen aanmaken
- [x] Een animator moet een bestaand dagdeel kunnen wijzigen
- [x] Een animator moet kunnen zien hoeveel kinderen er aanwezig waren tijdens een bepaald dagdeel
- [x] Een animator moet kunnen zien welke kinderen er aanwezig waren tijdens een bepaald dagdeel
- [ ] De lijst met alle dagdelen moet gesorteerd worden per dag, dan per type, en gegroepeerd per dag

### Aanwezigheden kinderen

- [x] Een animator moet kinderen kunnen opgeven als aanwezig voor een dagdeel
- [x] Een animator moet kunnen zien op welke dagdelen een kind aanwezig was
- [ ] Een animator moet een fiscale fiche kunnen maken voor een kind, waarop staat wanneer het kind aanwezig was en hoeveel het betaald heeft in totaal
- [x] Een animator moet kunnen aanpassen op welke dagdelen een kind aanwezig was
- [ ] Een animator moet een kind kunnen verwijderen
- 
### Aanwezigheden animatoren

- [ ] De verantwoordelijke van de dag moet animatoren kunnen opgeven als aanwezig voor een dagdeel
- [ ] De verantwoordelijke van de dag moet kunnen zien op welke dagdelen een animator aanwezig was
- [ ] De financieel verantwoordelijke moet een loonfiche kunnen maken voor een animator, waarop staat wanneer de animator aanwezig was, welk attest hij/zij heeft en hoeveel hij/zij betaald zal worden
- [ ] De verantwoordelijke van de dag moet kunnen aanpassen op welke dagdelen een animator aanwezig was
- [ ] De verantwoordelijke van de dag moet een animator kunnen verwijderen

### Medische fiches

- [x] Een animator moet kunnen zien of de medische fiche van een kind in orde is
- [x] Een animator moet kunnen veranderen of de medische fiche van een kind in orde is
- [x] Een animator moet kunnen zien wanneer de medische fiche van een kind laatst is nagekeken
- [ ] Een animator moet de medische fiche op de computer kunnen invullen en aanpassen

### Export

- [ ] De lijst van alle kinderen, met hun details en al hun aanwezigheden moet naar excel kunnen worden ge-exporteerd

## Development notes

- `~run` to watch source files and recompile on changes
- Default address and port during development are http://localhost:9000
- Postgres is used as the database in production, and H2 locally for development


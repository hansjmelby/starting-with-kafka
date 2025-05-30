

# Introduction to Kafka

## Prerequisites
You need the following to get the most out of this module
- Docker installed
- IDE of  your choise
- Lenses license (https://lenses.io/start/)
- gradle installed
- Java 20


## oppsett av miljø
* Lenses : kjør doker-compose up i lenses katalogen
* DB : Se readme i postgress katalog
* CDC : Se readme i cdc_postgres katalog


## Kode relatert 

### default state store location (windows)
C:\Users\M118946\AppData\Local\Temp\kafka-streams
### Relevante artikler
https://blog.dy.engineering/kafka-streams-and-rocksdb-in-the-space-time-continuum-and-a-little-bit-of-configuration-40edb5ee9ed7


## Oppgaver
### Enkle oppgaver
1. kjør de eksisterende kode eksemplene i kode repo
   2. sørg for at sensorLocation topic har to partisjoner. start opp to instanser av StreamJoinSample. Hva ser du? Fungerer koden?
2. lag en egen streams applikasjon som filtrerer ut alle sensorer som ikke er har id med sensor-1
3. bygg på  lag en kode som hele tiden aggregerer opp hvor mange målinger som er sendt in av sensor 1
4. bygg på 3 utvid koden til å legge de meldingene med id sensor-1 til eget topic med hva en navn du vil

### CDC 
1. sett opp postgress (se postgress katalog og readme fil)
2. sett opp CDC source (se postgress_cdc katalog)
3. Prøv deg med flere forskjellige detaljer (full sporbarhet, kun nye verdier, og nye verdier med enkel nøkkel)
 - hvilke av trategiene/configurasjonen er egnet for hva?
4. sett opp cdc sink

### mer komplekse oppgaver
1. bruk et av eksemplene i kode (sensorReadingSample), eller lag ditt eget der du publiserer resultat fra et tidsvindu over i et eget topic.
  - dersom du bruker eksemepl koden.
    - vurder om du ønsker å opprette topic manuelt (via consolle) eller la koden gjøle det for deg (default instillinger)
    - starte produsent FØR du starter konsument (om du velger å la kode opprette topic)
2. bruk connector (jdbc?) til å lage nytt innslag  databse med resultatet fra det nye topicet (vinduet)
  - er det noe forskjell om du bruker compacted topic eller ikke? 
  - hvordan ser nøkkelen til topic ut? er den brukbar?
5. 
### Vanskelige oppgaver
1. Endre miljø og kode slik at SensorLocation topic blir populert fra cdc og en db i postgres.
2. i CDC eksempel customer, lag en kode som filterer ut de hendelsene der en bruker bytter epost adresse og lag en eget topic customer_changed_email
   3. key = kundeID
   4. verdi er et objet (avro eller json, opp til deg) med verdiene ny epost adresse, timestamp
3. Finn på et eksempel (enten med noen av kilene som finnes i lenses, eller lag ditt eget) som bruker windowing


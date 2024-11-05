

# Introduction to Kafka

## Prerequisites
You need the following to get the most out of this module
- Docker installed
- IDE of  your choise
- Lenses license (https://lenses.io/start/)
- gradle installed
- Java 20


## oppsett av miljø
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

### midddel vanskelige 
1. 

### Vanskelige oppgaver
1. Endre miljø og kode slik at SensorLocation topic blir populert fra cdc og en db i postgres.
2. i CDC eksempel customer, lag en kode som filterer ut de hendelsene der en bruker bytter epost adresse og lag en eget topic customer_changed_email
   3. key = kundeID
   4. verdi er et objet (avro eller json, opp til deg) med verdiene ny epost adresse, timestamp
3. Finn på et eksempel (enten med noen av kilene som finnes i lenses, eller lag ditt eget) som bruker windowing


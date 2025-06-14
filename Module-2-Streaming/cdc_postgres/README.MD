## Før start
1. Ha en kjørende instans av Lenses Docker image
2. Docker image må ha både broker og schema registry port forwarnig satt opp

## Sette opp Source connector

#### Login inn i på http://localhost:9991. Brukernavn = admin, passord = admin

### Velg connector fra menyen
![](lenses_connector_oversikt.png).

### Fyll in nødvendge felter . Kopier fra eksempel under : kafka connector med full sporbarhet av endringer
![](lenses_cdc_config.png).


### Bekreft kjørende prosess med å se på
![](lenses_kjorende_connectors.png)

### Bekreft at det kommer meldinger på topic

## kafka connector med full sporbarhet av endringer

```
connector.class=io.debezium.connector.postgresql.PostgresConnector
publication.autocreate.mode=all_tables
database.user=postgres
database.dbname=postgres
slot.name=debezium
publication.name=dbpublication
database.server.name=postgres
plugin.name=pgoutput
database.port=5432
key.converter.schemas.enable=false
topic.prefix=db
database.hostname=192.168.50.62
database.password=***********
transforms.unwrap.drop.tombstones=true
value.converter.schemas.enable=false
name=postgresscdc
table.include.list=public.banktransaction
snapshot.mode=initial
```
#### merk nytt topic : db.public.banktransaction
se på data i topic. Merk skjema

## kafka connector med kun nye verdier
```
connector.class=io.debezium.connector.postgresql.PostgresConnector
publication.autocreate.mode=all_tables
database.user=postgres
database.dbname=postgres
slot.name=debezium
publication.name=dbpublication
database.server.name=postgres
plugin.name=pgoutput
database.port=5432
key.converter.schemas.enable=false
topic.prefix=db_data
database.hostname=192.168.50.62
database.password=pass
transforms.unwrap.drop.tombstones=true
value.converter.schemas.enable=false
name=postgresscdc_data
table.include.list=public.banktransaction
snapshot.mode=initial
transforms=unwrap
transforms.unwrap.type=io.debezium.transforms.ExtractNewRecordState
transforms.unwrap.drop.tombstones=true
```
#### merk nytt topic : db_data.public.banktransaction
se på data i topic. Merk skjema

## kafka connector med kun nye verdier og en enkelt verdi i key

```
connector.class=io.debezium.connector.postgresql.PostgresConnector
publication.autocreate.mode=all_tables
database.user=postgres
database.dbname=postgres
slot.name=debezium
publication.name=dbpublication
database.server.name=postgres
plugin.name=pgoutput
database.port=5432
key.converter.schemas.enable=false
topic.prefix=db_data_onekey
database.hostname=192.168.50.62
database.password=pass
transforms.unwrap.drop.tombstones=true
value.converter.schemas.enable=false
name=postgresscdc_data_onekey
table.include.list=public.banktransaction
snapshot.mode=initial
transforms=unwrap,ExtractKey
transforms.unwrap.type=io.debezium.transforms.ExtractNewRecordState
transforms.unwrap.drop.tombstones=true
transforms.ExtractKey.type=org.apache.kafka.connect.transforms.ExtractField$Key
transforms.ExtractKey.field=accountnumber
key.converter=org.apache.kafka.connect.json.JsonConverter
key.converter.schemas.enable=false
value.converter=org.apache.kafka.connect.json.JsonConverter
value.converter.schemas.enable=false
```
#### merk nytt topic : db_data_onekey.public.banktransaction
se på data i topic. Merk skjema

## Oppsett av JDBC Sink mot postgres

### før du starter
* Kjør CustomerAvroProducer for å få test data på topic
* Bekreft at det ligger meldinger på cutomer topic med AVRO som verdi


### sett opp ny SINK! connector
![](JDBC_Sink.png)
### sakse in eksempel på konfigurasjon under 
![](JDBC_Sink_2.png)

### kjør spørringer mot database for å bekrefte at data er lagt in i database

```sql
select * from customers
```

```
connector.class=io.confluent.connect.jdbc.JdbcSinkConnector
table.name.format=customers
connection.password=***********
topics=customer
batch.size=1
value.converter.schema.registry.url=http://127.0.0.1:8081
delete.enabled=true
auto.evolve=true
connection.user=postgres
name=customer-jdbc-sink-connector
auto.create=true
connection.url=jdbc:postgresql://192.168.50.62:5432/postgres
value.converter=io.confluent.connect.avro.AvroConverter
insert.mode=upsert
pk.mode=record_key
key.converter=org.apache.kafka.connect.storage.StringConverter
pk.fields=id
linger.ms=5000
```


### Se hva som skjer om lager en ny melding på topic der ID er samme, menny verdi (eksempelvis ny alder)
### Se hva skjer om du sender med en nøkkel og null verdi i value


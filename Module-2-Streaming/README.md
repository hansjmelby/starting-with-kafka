

# Introduction to Kafka

## Prerequisites
You need the following to get the most out of this module
- Docker installed
- IDE of  your choise
- Lenses license (https://lenses.io/start/)
- gradle installed
- Java 20

## default state store location (windows)
C:\Users\M118946\AppData\Local\Temp\kafka-streams
 ## Relevante artikler
https://blog.dy.engineering/kafka-streams-and-rocksdb-in-the-space-time-continuum-and-a-little-bit-of-configuration-40edb5ee9ed7

## Database oppsett

``` 
docker run --name user -e POSTGRES_PASSWORD=pass -p 5432:5432 -d postgres
docker run -p 80:80 -e "PGADMIN_DEFAULT_EMAIL=user@nav.no" -e "PGADMIN_DEFAULT_PASSWORD=password" -d dpage/pgadmin4



docker build -t my-postgres-cdc .
docker run -d --name my-postgres-cdc -p 5432:5432 my-postgres-cdc
docker run -p 80:80 -e "PGADMIN_DEFAULT_EMAIL=user@nav.no" -e "PGADMIN_DEFAULT_PASSWORD=password" -d dpage/pgadmin4



http://localhost/login?next=/

```

* Brukernavn : postgres
* passord    : pass
* ip adresse : din lokale ip adresse
* port       : 5432

# sql

``` sql
CREATE TABLE BankTransaction (
                                 accountNumber BIGINT NOT NULL,
                                 sum BIGINT NOT NULL,
                                 count BIGINT NOT NULL,
                                 time TIMESTAMP NOT NULL,
                                 PRIMARY KEY (accountNumber, time)
);

INSERT INTO BankTransaction (accountNumber, sum, count, time) VALUES (1234567890123456, 5000, 1, '2023-01-01 10:15:00');
INSERT INTO BankTransaction (accountNumber, sum, count, time) VALUES (1234567890123456, -1500, 2, '2023-01-02 14:30:00');
INSERT INTO BankTransaction (accountNumber, sum, count, time) VALUES (9876543210987654, 12000, 1, '2023-01-03 09:45:00');
INSERT INTO BankTransaction (accountNumber, sum, count, time) VALUES (9876543210987654, -2000, 2, '2023-01-03 12:00:00');
INSERT INTO BankTransaction (accountNumber, sum, count, time) VALUES (1122334455667788, 25000, 1, '2023-01-04 08:00:00');
INSERT INTO BankTransaction (accountNumber, sum, count, time) VALUES (1122334455667788, -5000, 2, '2023-01-05 15:00:00');
INSERT INTO BankTransaction (accountNumber, sum, count, time) VALUES (3344556677889900, 7000, 1, '2023-01-06 18:20:00');
INSERT INTO BankTransaction (accountNumber, sum, count, time) VALUES (3344556677889900, 3000, 2, '2023-01-07 10:30:00');
INSERT INTO BankTransaction (accountNumber, sum, count, time) VALUES (5566778899001122, 4500, 1, '2023-01-08 11:15:00');

```

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

## KAfka connect sink til db fra topic (jdbc sink)
```
name=customer-jdbc-sink-connector
connector.class=io.confluent.connect.jdbc.JdbcSinkConnector
topics=customer
connection.url=jdbc:postgresql://192.168.50.62:5432/postgres
connection.user=postgres
connection.password=pass
auto.create=true
auto.evolve=true
insert.mode=upsert
pk.mode=record_key
pk.fields=id
delete.enabled=true
key.converter=org.apache.kafka.connect.storage.StringConverter
value.converter=io.confluent.connect.avro.AvroConverter
value.converter.schema.registry.url=http://127.0.0.1:8081
batch.size=1
linger.ms=5000
table.name.format=customers
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

## db skripts
``` sql
CREATE TABLE BankTransaction (
accountNumber BIGINT NOT NULL,
sum BIGINT NOT NULL,
count BIGINT NOT NULL,
time TIMESTAMP NOT NULL,
PRIMARY KEY (accountNumber, time)
);


INSERT INTO BankTransaction (accountNumber, sum, count, time) VALUES (1234567890123456, 5000, 1, '2023-01-01 10:15:01');
INSERT INTO BankTransaction (accountNumber, sum, count, time) VALUES (1234567890123456, -1500, 2, '2023-01-02 14:30:01');
INSERT INTO BankTransaction (accountNumber, sum, count, time) VALUES (9876543210987654, 12000, 1, '2023-01-03 09:45:01');
INSERT INTO BankTransaction (accountNumber, sum, count, time) VALUES (9876543210987654, -2000, 2, '2023-01-03 12:00:01');
INSERT INTO BankTransaction (accountNumber, sum, count, time) VALUES (1122334455667788, 25000, 1, '2023-01-04 08:00:01');
INSERT INTO BankTransaction (accountNumber, sum, count, time) VALUES (1122334455667788, -5000, 2, '2023-01-05 15:00:01');
INSERT INTO BankTransaction (accountNumber, sum, count, time) VALUES (3344556677889900, 7000, 1, '2023-01-06 18:20:01');
INSERT INTO BankTransaction (accountNumber, sum, count, time) VALUES (3344556677889900, 3000, 2, '2023-01-07 10:30:01');
INSERT INTO BankTransaction (accountNumber, sum, count, time) VALUES (5566778899001122, 4500, 1, '2023-01-08 11:15:01');

SELECT * FROM pg_replication_slots WHERE slot_type = 'logical';
SELECT * FROM pg_create_logical_replication_slot('test_slot', 'test_decoding');
SHOW wal_level;

```


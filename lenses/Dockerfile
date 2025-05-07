FROM confluentinc/cp-kafka-connect:7.5.0
# Installer JDBC Sink Connector
RUN confluent-hub install --no-prompt confluentinc/kafka-connect-jdbc:10.7.4
# Installer Debezium PostgreSQL CDC Source Connector
RUN confluent-hub install --no-prompt debezium/debezium-connector-postgresql:2.5.4

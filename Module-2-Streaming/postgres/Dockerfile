FROM ubuntu:latest
LABEL authors="M118946"

# Bruk en offisiell PostgreSQL-baseimage
FROM debezium/postgres:16

ENV POSTGRES_PASSWORD=pass

# Start PostgreSQL-serveren
CMD ["postgres"]
FROM ubuntu:latest
LABEL authors="M118946"

# Bruk en offisiell PostgreSQL-baseimage
FROM postgres:15

# Oppdater PostgreSQL-konfigurasjonen
COPY postgresql.conf /etc/postgresql/postgresql.conf
ENV POSTGRES_PASSWORD=pass

# Start PostgreSQL-serveren
CMD ["postgres"]
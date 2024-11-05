CREATE TABLE BankTransaction (
                                 accountNumber BIGINT NOT NULL,
                                 sum BIGINT NOT NULL,
                                 count BIGINT NOT NULL,
                                 time TIMESTAMP NOT NULL,
                                 PRIMARY KEY (accountNumber, time)
);
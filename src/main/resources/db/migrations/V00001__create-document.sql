create table DOCUMENT (
    id VARCHAR(100) PRIMARY KEY,
    mimetype VARCHAR(100) not null,
    documentData BINARY not null,
    fileName VARCHAR(100) not null
);

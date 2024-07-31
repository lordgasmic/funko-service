create table funko
(
    id        int NOT NULL AUTO_INCREMENT,
    title     nvarchar(255),
    fandom    nvarchar(255),
    series_id int,
    name      nvarchar(255),
    CONSTRAINT funko_pk PRIMARY KEY (id)
);
create table funko_extras
(
    id       int NOT NULL AUTO_INCREMENT,
    funko_id int,
    text     nvarchar(255),
    CONSTRAINT funko_extras_pk PRIMARY KEY (id)
);

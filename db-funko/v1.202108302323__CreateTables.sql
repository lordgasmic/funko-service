create table funko(
    id int NOT NULL AUTO_INCREMENT,
    fandom nvarchar(255) NOT NULL,
    name nvarchar(255) NOT NULL,
    CONSTRAINT funko_pk PRIMARY KEY (id)
);

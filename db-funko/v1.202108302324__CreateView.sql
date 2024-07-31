create or replace view funko_vw as select * from funko;
grant select, insert, update, delete on web.funko_vw to 'web_appl'@'localhost';
grant select, insert, update, delete on web.funko_vw to 'web_appl'@'%';

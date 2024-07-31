create or replace view funko_vw as
select *
from funko;
grant select, insert, update, delete on web.funko_vw to 'web_appl'@'localhost';
grant select, insert, update, delete on web.funko_vw to 'web_appl'@'%';

create or replace view funko_extras_vw as
select *
from funko_extras;
grant select, insert, update, delete on web.funko_extras_vw to 'web_appl'@'localhost';
grant select, insert, update, delete on web.funko_extras_vw to 'web_appl'@'%';

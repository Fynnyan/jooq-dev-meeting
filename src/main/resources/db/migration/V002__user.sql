create extension if not exists citext;

create domain email as citext
    check ( value ~
            '^[a-zA-Z0-9.!#$%&''*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$' );

create table users
(
    id        uuid primary key,
    first_name text,
    last_name text,
    email     email
);

insert into users
values ('8b3db797-5857-4a82-8498-0782ff79a5f1', 'Mary', 'Shelly', 'm.s@hearts.com'),
       ('68c82f93-639e-42e4-a99e-4d7b2fbb45d9', 'Bram', 'Stoker', 'b.s@hearts.com'),
       ('2baa51af-e107-4f2e-84fa-2216fe186157', 'Florence', 'Stoker', 'b.s@hearts.com');


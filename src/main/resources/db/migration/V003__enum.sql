
create type status as enum ('NEW', 'ACTIVE', 'CLOSED', 'UNKNOWN');

create table org(
    id uuid primary key default gen_random_uuid(),
    name text not null,
    status status default 'NEW',
    name_localized jsonb
)


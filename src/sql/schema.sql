create table participants
(
    id         char(36),
    first_name varchar(100),
    last_name  varchar(100),
    primary key (id)
);

create table program_preferences
(
    id             int not null auto_increment,
    participant_id char(36),
    workshop_id    int,
    pref_order     int,
    primary key (id),
    foreign key (participant_id) references participants (id) on delete cascade,
    index (participant_id, workshop_id, pref_order)
);

create table event_log (
    id int not null auto_increment,
    ip_address varchar(100),
    event_time timestamp default current_timestamp,
    payload varchar(1000),
    primary key (id)
);
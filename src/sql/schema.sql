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
    present_slot_1 int not null default 1,
    present_slot_2 int not null default 1,
    present_slot_3 int not null default 1,
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

CREATE TABLE participant_registration (
    id int not null auto_increment,
    participant_id char(36) not null,
    program_id int,
    slot enum('1','2','3') not null,
    registration_time timestamp default current_timestamp,
    primary key (id),
    index (participant_id, program_id)
);

CREATE UNIQUE INDEX participant_unique_slot
    ON participant_registration (participant_id,  slot);


CREATE TABLE program (
     id INT PRIMARY KEY,
     name text NOT NULL,
     description text,
     author text,
     keywords text,
     type INT,
     maxSize INT,
     minSize INT,
     roverRecommended BOOLEAN,
     availableSlots INT,
     countinueInSlot varchar(2),
     slot1 BOOLEAN,
     slot2 BOOLEAN,
     slot3 BOOLEAN,
     act1 BOOLEAN,
     act2 BOOLEAN,
     act3 BOOLEAN
);

delimiter //
CREATE TRIGGER pr_reg_check_limit
    BEFORE INSERT
    ON participant_registration
    FOR EACH ROW
BEGIN
    IF ( SELECT COUNT(*)
         FROM participant_registration
         WHERE program_id = NEW.program_id AND slot = NEW.slot)
                                          > ( SELECT maxSize - 1
                                           FROM program
                                           WHERE id = NEW.program_id ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Maximum number of participants reached for this program.';
    END IF;
END
//
delimiter ;
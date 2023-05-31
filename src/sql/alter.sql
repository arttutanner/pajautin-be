
alter table participants drop column present_slot_1;
alter table participants drop column present_slot_2;
alter table participants drop column present_slot_3;

alter table participants add column present_slot_1 int not null default 1;
alter table participants add column present_slot_2 int not null default 1;
alter table participants add column present_slot_3 int not null default 1;

alter table program_preferences drop column present_slot_1;
alter table program_preferences drop column present_slot_2;
alter table program_preferences drop column present_slot_3;


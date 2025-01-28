create table team_request (id varchar(255) not null, content TEXT, created_at datetime(6) not null, is_accepted bit, sender_id varchar(255) not null, team_id varchar(255), primary key (id)) engine=InnoDB;
alter table team_request add constraint FKtctko3jheeky34b3lj7kmmwij foreign key (team_id) references team (id);

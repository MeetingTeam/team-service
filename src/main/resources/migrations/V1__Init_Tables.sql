create table channel (id varchar(255) not null, channel_name varchar(255), description TEXT, type enum ('CHAT_CHANNEl','VIDEOCALL_CHANNEL'), team_id varchar(255), primary key (id)) engine=InnoDB;
create table team (id varchar(255) not null, auto_add_member bit, team_name varchar(255), url_icon varchar(255), primary key (id)) engine=InnoDB;
create table team_member (id varchar(255) not null, role enum ('DEPUTY','LEADER','LEAVE','MEMBER'), user_id varchar(255) not null, team_id varchar(255), primary key (id)) engine=InnoDB;
alter table channel add constraint FK63ug4lh1q6hpxuyqhbs6xm1v8 foreign key (team_id) references team (id);
alter table team_member add constraint FK9ubp79ei4tv4crd0r9n7u5i6e foreign key (team_id) references team (id);

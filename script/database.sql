create table tuser(
    user_id                         int AUTO_INCREMENT,
    user_name                       varchar(100) unique not null,
    user_email                      varchar(512) unique not null,
    user_password                   varchar(1000) not null,
    user_img                        varchar(1000) null,
    primary key(user_id)
) engine = InnoDB;

create table tmessage(
    message_id                      int AUTO_INCREMENT,
    message_sender                  int not null,
    message_receiver                int null,
    message_group                   int null,
    message_content                 text not null,
    message_date                    timestamp default CURRENT_TIMESTAMP,
    primary key(message_id)
) engine = InnoDB;

create table tgroup(
    group_id                        int AUTO_INCREMENT primary key,

) engine = InnoDB;
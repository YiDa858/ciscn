create table fidouser
(
    id       int auto_increment
        primary key,
    username varchar(25)  not null,
    pubKey   varchar(255) not null
);

create table file
(
    id       int auto_increment
        primary key,
    location varchar(200) null
);

create table opaqueuser
(
    id         int auto_increment
        primary key,
    group_user varchar(255) not null,
    file_id    int          null
);


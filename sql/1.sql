create table JKUSER
(
  jknum     INT not null,
  name      VARCHAR(20) not null,
  password  VARCHAR(32) not null,
  signature VARCHAR(100),
  iconpath  VARCHAR(50) default ('F:/QQmsg/default_header.jpg'),
  site      VARCHAR(40),
  phone     VARCHAR(15),
  email     VARCHAR(20),
  state     INT not null,
  question  VARCHAR(50) not null,
  sex       INT,
  answer    VARCHAR(32)
)
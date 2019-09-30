create table CHATLOG
(
  srcid    INT,
  destid   INT,
  content  VARCHAR(60) not null,
  state    INT not null,
  sendtime VARCHAR(25) not null
)
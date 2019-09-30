alter table CHATLOG
  add constraint S_S foreign key (SRCID)
  references JKUSER (JKNUM);
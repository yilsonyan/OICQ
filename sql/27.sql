alter table COMMUCHATLOG
  add constraint S_S_S foreign key (SRCID)
  references JKUSER (JKNUM);

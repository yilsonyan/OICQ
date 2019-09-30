alter table FRIENDAPPLY
  add constraint S_S11 foreign key (SRCID)
  references JKUSER (JKNUM);
alter table COMMUAPPLY
  add constraint S_S31 foreign key (SRCID)
  references JKUSER (JKNUM);
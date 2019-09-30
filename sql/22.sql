alter table COMMUAPPLYRESP
  add constraint S_S41 foreign key (SRCID)
  references JKUSER (JKNUM);
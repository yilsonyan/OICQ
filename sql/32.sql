alter table FRIENDAPPLYRESP
  add constraint S_S21 foreign key (SRCID)
  references JKUSER (JKNUM);
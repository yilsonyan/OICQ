alter table COMMUAPPLYRESP
  add constraint S_S42 foreign key (DESTID)
  references JKUSER (JKNUM);
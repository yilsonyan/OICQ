alter table FRIENDAPPLYRESP
  add constraint S_S22 foreign key (DESTID)
  references JKUSER (JKNUM);
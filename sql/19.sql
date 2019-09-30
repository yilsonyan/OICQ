alter table COMMUAPPLY
  add constraint S_S32 foreign key (DESTID)
  references JKUSER (JKNUM);
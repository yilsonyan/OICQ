alter table FRIENDAPPLY
  add constraint S_S12 foreign key (DESTID)
  references JKUSER (JKNUM);

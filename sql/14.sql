alter table CHATLOG
  add constraint N_N foreign key (DESTID)
  references JKUSER (JKNUM);
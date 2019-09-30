alter table JKFILE
  add constraint J_J2 foreign key (JID)
  references JKUSER (JKNUM);
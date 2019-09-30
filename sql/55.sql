alter table UL
  add constraint U_U1 foreign key (JID)
  references JKUSER (JKNUM);
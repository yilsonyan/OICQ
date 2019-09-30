alter table UG
  add constraint U_J foreign key (JID)
  references JKUSER (JKNUM) on delete set null;
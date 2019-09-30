alter table UC
  add constraint J_J foreign key (JID)
  references JKUSER (JKNUM) on delete cascade;
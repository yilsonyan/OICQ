alter table UC
  add constraint C_C foreign key (CID)
  references COMMUNITY (CID) on delete cascade;
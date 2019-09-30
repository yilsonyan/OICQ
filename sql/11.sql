alter table CF
  add constraint C_C2 foreign key (CID)
  references COMMUNITY (CID);
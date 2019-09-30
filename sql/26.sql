alter table COMMUCHATLOG
  add constraint C_C_C foreign key (CID)
  references COMMUNITY (CID);
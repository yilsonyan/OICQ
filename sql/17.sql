alter table COMMUAPPLY
  add constraint C_C11 foreign key (CID)
  references COMMUNITY (CID);
alter table COMMUAPPLYRESP
  add constraint C_C21 foreign key (CID)
  references COMMUNITY (CID);
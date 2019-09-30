alter table UCF
  add constraint CC_C foreign key (CID)
  references COMMUNITY (CID);
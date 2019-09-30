alter table UCF
  add constraint FF_F foreign key (FID)
  references JKFILE (FID);
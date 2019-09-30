alter table CF
  add constraint F_F2 foreign key (FID)
  references JKFILE (FID);
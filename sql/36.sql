alter table JKGROUP
  add constraint G_U foreign key (OWNER)
  references JKUSER (JKNUM) on delete cascade;

alter table COMMUNITY
  add constraint U_C foreign key (OWNER)
  references JKUSER (JKNUM) on delete cascade;

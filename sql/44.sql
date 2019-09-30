alter table UCF
  add constraint UU_U foreign key (JKNUM)
  references JKUSER (JKNUM);
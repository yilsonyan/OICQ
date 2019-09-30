alter table UF
  add constraint D_U foreign key (DESID)
  references JKUSER (JKNUM);
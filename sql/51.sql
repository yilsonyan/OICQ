alter table UG
  add constraint U_G foreign key (GID)
  references JKGROUP (GID) on delete set null;
alter table UL
  add constraint L_L1 foreign key (LID)
  references COMMUCHATLOG (LID);
create table if not exists REGISTRATION (
  id bigint auto_increment primary key,
  job varchar(128) not null,
  clockedIn datetime null,
  clockedOut datetime null,
  comment varchar(128) null,
  duration double null,
  earnings double null,
  hourlyRate double null,
  lastModified datetime not null,
  tags varchar(128) null,
  totalEarningsAdjustment double null,
  totalTimeAdjustment double null,

  constraint registration_idx_job_clockedin_clockedout unique (job, clockedIn, clockedOut)
);

create index registration_idx_job on REGISTRATION (job);

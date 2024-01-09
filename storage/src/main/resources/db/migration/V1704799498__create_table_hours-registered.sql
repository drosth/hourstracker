create table hours_registered
(
    id                        bigint       not null primary key,
    job                       varchar(128) not null,
    "clockedIn"               timestamp,
    "clockedOut"              timestamp,
    duration                  double precision,
    "hourlyRate"              double precision,
    earnings                  double precision,
    comment                   varchar(128),
    tags                      varchar(128),
    "totalTimeAdjustment"     double precision,
    "totalEarningsAdjustment" double precision,
    "lastModified"            timestamp    not null
);

create index idx_hr_job on hours_registered (job);

create unique index idx_hr_job_clocked_in_out on hours_registered (job, "clockedIn", "clockedOut");


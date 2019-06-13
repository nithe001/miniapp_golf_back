-- 新增字段：球队简称（最多三个汉字）
alter table team_info add column  ti_name varchar(128) comment '球队简称（最多三个汉字）' AFTER ti_name;

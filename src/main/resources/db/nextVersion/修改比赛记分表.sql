
-- 是否上球道
alter table match_score modify column ms_is_up varchar(128);
-- 新增字段：记分规则
alter table match_score add column  ms_type int comment '记分规则：0杆差  1杆数' AFTER ms_user_name;
-- 新增字段：创建用户姓名
alter table match_score add column  ms_create_user_name varchar(128) comment '创建用户姓名' AFTER ms_create_user_id;
-- 新增字段：更新用户姓名
alter table match_score add column  ms_update_user_name varchar(128) comment '更新用户姓名' AFTER ms_update_user_id;
-- 新增字段：前后场
alter table match_score add column  ms_before_after int comment '0：前9场  1：后9场' AFTER ms_score;


alter table team_user_mapping modify column tum_user_type comment '用户类型（0：队长  1：队员 2:申请入队）';
alter table team_info modify column ti_info_open_type comment '1、公开 比赛成绩等向所有球友开放；0、封闭，比赛成绩等向队友开放';

-- 删除球队用户mapping表的字段
alter table team_user_mapping drop column tum_type;
alter table team_user_mapping drop column tum_is_valid;

/*用户信息表：新增字段cu_hospital_level 医院等级*/
ALTER TABLE `cm_user` ADD COLUMN cu_hospital_level varchar(128) COMMENT '医院等级';
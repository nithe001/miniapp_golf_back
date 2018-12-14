/*专家课件表：新增字段wec_expert_name 专家名称*/
ALTER TABLE `wx_expert_courseware` ADD COLUMN wec_expert_name varchar(128)  COMMENT '专家名称';

/*专家课件表：新增字段wec_department 科室*/
ALTER TABLE `wx_expert_courseware` ADD COLUMN wec_department varchar(128)  COMMENT '科室';

/*专家课件表：新增字段wec_position 职称*/
ALTER TABLE `wx_expert_courseware` ADD COLUMN wec_position varchar(128)  COMMENT '职称';

/*专家课件表：新增字段wec_company 单位*/
ALTER TABLE `wx_expert_courseware` ADD COLUMN wec_company varchar(512)  COMMENT '单位';

/*专家课件表：修改字段wec_ppt的类型*/
ALTER TABLE `wx_expert_courseware` ADD COLUMN wec_ppt varchar(128) COMMENT 'ppt名称';

/*专家课件表：新增字段wec_ppt_img ppt生成的图片名称，以，隔开*/
ALTER TABLE `wx_expert_courseware` ADD COLUMN wec_ppt_img varchar(2048)  COMMENT 'ppt生成的图片名称';

/*专家课件表：新增字段wec_is_del 是否删除*/
ALTER TABLE `wx_expert_courseware` ADD COLUMN wec_is_del int COMMENT '是否删除(1:是，0：否)';

/*活动日历表：新增字段wc_is_open 是否完全公开*/
ALTER TABLE `wx_calendar` ADD COLUMN wc_is_open int COMMENT '是否完全公开';

/*活动日历表：新增字段wc_is_del 是否删除*/
ALTER TABLE `wx_calendar` ADD COLUMN wc_is_del int COMMENT '是否删除(1:是，0：否)';

/*学术咨询表：新增字段wac_is_del 是否删除*/
ALTER TABLE `wx_academic_consult` ADD COLUMN wac_is_del int COMMENT '是否删除(1:是，0：否)';

/*用户表：修改字段cu_type的类型*/
ALTER TABLE `cm_user` CHANGE cu_type cu_type int COMMENT '用户类型(1:理事会，2:委员会，3：普通用户)';

/*文献表：修改字段ln_web_file_url的类型*/
ALTER TABLE `lk_news` CHANGE cu_type cu_type ln_web_file_url varchar(2048) COMMENT '医脉通指南web下载地址';

/*活动日历表：新增字段wc_expert_name主讲人*/
ALTER TABLE `wx_calendar` ADD COLUMN wc_expert_name varchar(128) COMMENT '主讲人';

/*活动日历表：新增字段wc_type 会议类型*/
ALTER TABLE `wx_calendar` ADD COLUMN wc_type varchar(128) COMMENT '会议类型';


/*2017-08-04*/
/*资讯表：新增字段ln_audio_time 音频时长*/
ALTER TABLE `lk_news` ADD COLUMN ln_audio_time varchar(128) COMMENT '音频时长';
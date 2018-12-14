/*新增字段wc_abstract 会议介绍*/
ALTER TABLE `wx_calendar` ADD COLUMN wc_abstract varchar(512)  COMMENT '会议介绍';
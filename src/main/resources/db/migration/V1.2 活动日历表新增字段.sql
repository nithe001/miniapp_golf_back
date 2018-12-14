/*活动日历表  新增字段 wc_start_time 开始时间*/
ALTER TABLE `wx_calendar` ADD COLUMN wc_start_time varchar(128) COMMENT '开始时间';

/*活动日历表  新增字段 wc_end_time 结束时间*/
ALTER TABLE `wx_calendar` ADD COLUMN wc_end_time varchar(128) COMMENT '结束时间';
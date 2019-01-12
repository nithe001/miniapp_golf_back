package com.golf.golf.db;

import javax.persistence.*;

@Entity
@Table(name = "wechat_user_info")
public class WechatUserInfo {
    private Long wuiId;
    private Long wuiUId;
    private Integer wuiSubscribe;
    private String wuiOpenid;
    private String wuiNickName;
    private String wuiRealName;
    private String wuiPhone;
    private String wuiSex;
    private String wuiCity;
    private String wuiCountry;
    private String wuiProvince;
    private String wuiLanguage;
    private String wuiHeadimgurl;
    private Long wuiSubscribeTime;
    private String wuiUnionid;
    private String wuiRemark;
    private String wuiHeadimg;
    private Integer wuiIsValid;
    private Long createTime;
    private Long updateTime;
    private String watermarkAppid;
    private String watermarkTimestamp;

    @Id
    @Column(name = "wui_id")
    public Long getWuiId() {
        return wuiId;
    }

    public void setWuiId(Long wuiId) {
        this.wuiId = wuiId;
    }

    @Basic
    @Column(name = "wui_u_id")
    public Long getWuiUId() {
        return wuiUId;
    }

    public void setWuiUId(Long wuiUId) {
        this.wuiUId = wuiUId;
    }

    @Basic
    @Column(name = "wui_subscribe")
    public Integer getWuiSubscribe() {
        return wuiSubscribe;
    }

    public void setWuiSubscribe(Integer wuiSubscribe) {
        this.wuiSubscribe = wuiSubscribe;
    }

    @Basic
    @Column(name = "wui_openid")
    public String getWuiOpenid() {
        return wuiOpenid;
    }

    public void setWuiOpenid(String wuiOpenid) {
        this.wuiOpenid = wuiOpenid;
    }

    @Basic
    @Column(name = "wui_nick_name")
    public String getWuiNickName() {
        return wuiNickName;
    }

    public void setWuiNickName(String wuiNickName) {
        this.wuiNickName = wuiNickName;
    }

    @Basic
    @Column(name = "wui_real_name")
    public String getWuiRealName() {
        return wuiRealName;
    }

    public void setWuiRealName(String wuiRealName) {
        this.wuiRealName = wuiRealName;
    }

    @Basic
    @Column(name = "wui_phone")
    public String getWuiPhone() {
        return wuiPhone;
    }

    public void setWuiPhone(String wuiPhone) {
        this.wuiPhone = wuiPhone;
    }

    @Basic
    @Column(name = "wui_sex")
    public String getWuiSex() {
        return wuiSex;
    }

    public void setWuiSex(String wuiSex) {
        this.wuiSex = wuiSex;
    }

    @Basic
    @Column(name = "wui_city")
    public String getWuiCity() {
        return wuiCity;
    }

    public void setWuiCity(String wuiCity) {
        this.wuiCity = wuiCity;
    }

    @Basic
    @Column(name = "wui_country")
    public String getWuiCountry() {
        return wuiCountry;
    }

    public void setWuiCountry(String wuiCountry) {
        this.wuiCountry = wuiCountry;
    }

    @Basic
    @Column(name = "wui_province")
    public String getWuiProvince() {
        return wuiProvince;
    }

    public void setWuiProvince(String wuiProvince) {
        this.wuiProvince = wuiProvince;
    }

    @Basic
    @Column(name = "wui_language")
    public String getWuiLanguage() {
        return wuiLanguage;
    }

    public void setWuiLanguage(String wuiLanguage) {
        this.wuiLanguage = wuiLanguage;
    }

    @Basic
    @Column(name = "wui_headimgurl")
    public String getWuiHeadimgurl() {
        return wuiHeadimgurl;
    }

    public void setWuiHeadimgurl(String wuiHeadimgurl) {
        this.wuiHeadimgurl = wuiHeadimgurl;
    }

    @Basic
    @Column(name = "wui_subscribe_time")
    public Long getWuiSubscribeTime() {
        return wuiSubscribeTime;
    }

    public void setWuiSubscribeTime(Long wuiSubscribeTime) {
        this.wuiSubscribeTime = wuiSubscribeTime;
    }

    @Basic
    @Column(name = "wui_unionid")
    public String getWuiUnionid() {
        return wuiUnionid;
    }

    public void setWuiUnionid(String wuiUnionid) {
        this.wuiUnionid = wuiUnionid;
    }

    @Basic
    @Column(name = "wui_remark")
    public String getWuiRemark() {
        return wuiRemark;
    }

    public void setWuiRemark(String wuiRemark) {
        this.wuiRemark = wuiRemark;
    }

    @Basic
    @Column(name = "wui_headimg")
    public String getWuiHeadimg() {
        return wuiHeadimg;
    }

    public void setWuiHeadimg(String wuiHeadimg) {
        this.wuiHeadimg = wuiHeadimg;
    }

    @Basic
    @Column(name = "wui_is_valid")
    public Integer getWuiIsValid() {
        return wuiIsValid;
    }

    public void setWuiIsValid(Integer wuiIsValid) {
        this.wuiIsValid = wuiIsValid;
    }

    @Basic
    @Column(name = "create_time")
    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Basic
    @Column(name = "update_time")
    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Basic
    @Column(name = "watermark_appid")
    public String getWatermarkAppid() {
        return watermarkAppid;
    }

    public void setWatermarkAppid(String watermarkAppid) {
        this.watermarkAppid = watermarkAppid;
    }

    @Basic
    @Column(name = "watermark_timestamp")
    public String getWatermarkTimestamp() {
        return watermarkTimestamp;
    }

    public void setWatermarkTimestamp(String watermarkTimestamp) {
        this.watermarkTimestamp = watermarkTimestamp;
    }

}

package com.golf.golf.db;

import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "wechat_user_info")
public class WechatUserInfo {

	// Fields

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

	// Constructors

	/** default constructor */
	public WechatUserInfo() {
	}

	/** full constructor */
	public WechatUserInfo(Long wuiUId, Integer wuiSubscribe, String wuiOpenid,
						  String wuiNickName, String wuiRealName, String wuiPhone,
						  String wuiSex, String wuiCity, String wuiCountry,
						  String wuiProvince, String wuiLanguage, String wuiHeadimgurl,
						  Long wuiSubscribeTime, String wuiUnionid, String wuiRemark,
						  String wuiHeadimg, Integer wuiIsValid, Long createTime,
						  Long updateTime, String watermarkAppid, String watermarkTimestamp) {
		this.wuiUId = wuiUId;
		this.wuiSubscribe = wuiSubscribe;
		this.wuiOpenid = wuiOpenid;
		this.wuiNickName = wuiNickName;
		this.wuiRealName = wuiRealName;
		this.wuiPhone = wuiPhone;
		this.wuiSex = wuiSex;
		this.wuiCity = wuiCity;
		this.wuiCountry = wuiCountry;
		this.wuiProvince = wuiProvince;
		this.wuiLanguage = wuiLanguage;
		this.wuiHeadimgurl = wuiHeadimgurl;
		this.wuiSubscribeTime = wuiSubscribeTime;
		this.wuiUnionid = wuiUnionid;
		this.wuiRemark = wuiRemark;
		this.wuiHeadimg = wuiHeadimg;
		this.wuiIsValid = wuiIsValid;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.watermarkAppid = watermarkAppid;
		this.watermarkTimestamp = watermarkTimestamp;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "wui_id", unique = true, nullable = false)
	public Long getWuiId() {
		return this.wuiId;
	}

	public void setWuiId(Long wuiId) {
		this.wuiId = wuiId;
	}

	@Column(name = "wui_u_id")
	public Long getWuiUId() {
		return this.wuiUId;
	}

	public void setWuiUId(Long wuiUId) {
		this.wuiUId = wuiUId;
	}

	@Column(name = "wui_subscribe")
	public Integer getWuiSubscribe() {
		return this.wuiSubscribe;
	}

	public void setWuiSubscribe(Integer wuiSubscribe) {
		this.wuiSubscribe = wuiSubscribe;
	}

	@Column(name = "wui_openid", length = 128)
	public String getWuiOpenid() {
		return this.wuiOpenid;
	}

	public void setWuiOpenid(String wuiOpenid) {
		this.wuiOpenid = wuiOpenid;
	}

	@Column(name = "wui_nick_name", length = 128)
	public String getWuiNickName() {
		return this.wuiNickName;
	}

	public void setWuiNickName(String wuiNickName) {
		this.wuiNickName = wuiNickName;
	}

	@Column(name = "wui_real_name", length = 128)
	public String getWuiRealName() {
		return this.wuiRealName;
	}

	public void setWuiRealName(String wuiRealName) {
		this.wuiRealName = wuiRealName;
	}

	@Column(name = "wui_phone", length = 128)
	public String getWuiPhone() {
		return this.wuiPhone;
	}

	public void setWuiPhone(String wuiPhone) {
		this.wuiPhone = wuiPhone;
	}

	@Column(name = "wui_sex", length = 128)
	public String getWuiSex() {
		return this.wuiSex;
	}

	public void setWuiSex(String wuiSex) {
		this.wuiSex = wuiSex;
	}

	@Column(name = "wui_city", length = 128)
	public String getWuiCity() {
		return this.wuiCity;
	}

	public void setWuiCity(String wuiCity) {
		this.wuiCity = wuiCity;
	}

	@Column(name = "wui_country", length = 128)
	public String getWuiCountry() {
		return this.wuiCountry;
	}

	public void setWuiCountry(String wuiCountry) {
		this.wuiCountry = wuiCountry;
	}

	@Column(name = "wui_province", length = 128)
	public String getWuiProvince() {
		return this.wuiProvince;
	}

	public void setWuiProvince(String wuiProvince) {
		this.wuiProvince = wuiProvince;
	}

	@Column(name = "wui_language", length = 128)
	public String getWuiLanguage() {
		return this.wuiLanguage;
	}

	public void setWuiLanguage(String wuiLanguage) {
		this.wuiLanguage = wuiLanguage;
	}

	@Column(name = "wui_headimgurl", length = 512)
	public String getWuiHeadimgurl() {
		return this.wuiHeadimgurl;
	}

	public void setWuiHeadimgurl(String wuiHeadimgurl) {
		this.wuiHeadimgurl = wuiHeadimgurl;
	}

	@Column(name = "wui_subscribe_time")
	public Long getWuiSubscribeTime() {
		return this.wuiSubscribeTime;
	}

	public void setWuiSubscribeTime(Long wuiSubscribeTime) {
		this.wuiSubscribeTime = wuiSubscribeTime;
	}

	@Column(name = "wui_unionid", length = 128)
	public String getWuiUnionid() {
		return this.wuiUnionid;
	}

	public void setWuiUnionid(String wuiUnionid) {
		this.wuiUnionid = wuiUnionid;
	}

	@Column(name = "wui_remark", length = 128)
	public String getWuiRemark() {
		return this.wuiRemark;
	}

	public void setWuiRemark(String wuiRemark) {
		this.wuiRemark = wuiRemark;
	}

	@Column(name = "wui_headimg", length = 128)
	public String getWuiHeadimg() {
		return this.wuiHeadimg;
	}

	public void setWuiHeadimg(String wuiHeadimg) {
		this.wuiHeadimg = wuiHeadimg;
	}

	@Column(name = "wui_is_valid")
	public Integer getWuiIsValid() {
		return this.wuiIsValid;
	}

	public void setWuiIsValid(Integer wuiIsValid) {
		this.wuiIsValid = wuiIsValid;
	}

	@Column(name = "create_time")
	public Long getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	@Column(name = "update_time")
	public Long getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	@Column(name = "watermark_appid", length = 128)
	public String getWatermarkAppid() {
		return this.watermarkAppid;
	}

	public void setWatermarkAppid(String watermarkAppid) {
		this.watermarkAppid = watermarkAppid;
	}

	@Column(name = "watermark_timestamp", length = 128)
	public String getWatermarkTimestamp() {
		return this.watermarkTimestamp;
	}

	public void setWatermarkTimestamp(String watermarkTimestamp) {
		this.watermarkTimestamp = watermarkTimestamp;
	}

}

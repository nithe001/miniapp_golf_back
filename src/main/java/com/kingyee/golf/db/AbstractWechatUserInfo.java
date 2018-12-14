package com.kingyee.golf.db;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * AbstractWechatUserInfo entity provides the base persistence definition of the
 * WechatUserInfo entity. @author MyEclipse Persistence Tools
 */
@MappedSuperclass
public abstract class AbstractWechatUserInfo implements java.io.Serializable {

	// Fields

	private Long id;
	private Long UId;
	private Integer subscribe;
	private String openid;
	private String nickName;
	private String sex;
	private String city;
	private String country;
	private String province;
	private String language;
	private String headimgurl;
	private Long subscribeTime;
	private String unionid;
	private String remark;
	private String headimg;
	private Integer isValid;
	private Long createTime;
	private Long updateTime;
	private String watermarkAppid;
	private String watermarkTimestamp;

	// Constructors

	/** default constructor */
	public AbstractWechatUserInfo() {
	}

	/** full constructor */
	public AbstractWechatUserInfo(Long UId, Integer subscribe, String openid,
			String nickName, String sex, String city, String country,
			String province, String language, String headimgurl,
			Long subscribeTime, String unionid, String remark, String headimg,
			Integer isValid, Long createTime, Long updateTime, String watermarkAppid, String watermarkTimestamp) {
		this.UId = UId;
		this.subscribe = subscribe;
		this.openid = openid;
		this.nickName = nickName;
		this.sex = sex;
		this.city = city;
		this.country = country;
		this.province = province;
		this.language = language;
		this.headimgurl = headimgurl;
		this.subscribeTime = subscribeTime;
		this.unionid = unionid;
		this.remark = remark;
		this.headimg = headimg;
		this.isValid = isValid;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.watermarkAppid = watermarkAppid;
		this.watermarkTimestamp = watermarkTimestamp;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "u_id")
	public Long getUId() {
		return this.UId;
	}

	public void setUId(Long UId) {
		this.UId = UId;
	}

	@Column(name = "subscribe")
	public Integer getSubscribe() {
		return this.subscribe;
	}

	public void setSubscribe(Integer subscribe) {
		this.subscribe = subscribe;
	}

	@Column(name = "openid", length = 128)
	public String getOpenid() {
		return this.openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	@Column(name = "nick_name", length = 128)
	public String getNickName() {
		return this.nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	@Column(name = "sex", length = 128)
	public String getSex() {
		return this.sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	@Column(name = "city", length = 128)
	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Column(name = "country", length = 128)
	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Column(name = "province", length = 128)
	public String getProvince() {
		return this.province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	@Column(name = "language", length = 128)
	public String getLanguage() {
		return this.language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	@Column(name = "headimgurl", length = 512)
	public String getHeadimgurl() {
		return this.headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	@Column(name = "subscribe_time")
	public Long getSubscribeTime() {
		return this.subscribeTime;
	}

	public void setSubscribeTime(Long subscribeTime) {
		this.subscribeTime = subscribeTime;
	}

	@Column(name = "unionid", length = 128)
	public String getUnionid() {
		return this.unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

	@Column(name = "remark", length = 128)
	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Column(name = "headimg", length = 128)
	public String getHeadimg() {
		return this.headimg;
	}

	public void setHeadimg(String headimg) {
		this.headimg = headimg;
	}

	@Column(name = "is_valid")
	public Integer getIsValid() {
		return this.isValid;
	}

	public void setIsValid(Integer isValid) {
		this.isValid = isValid;
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
		return watermarkAppid;
	}

	public void setWatermarkAppid(String watermarkAppid) {
		this.watermarkAppid = watermarkAppid;
	}

	@Column(name = "watermark_timestamp", length = 128)
	public String getWatermarkTimestamp() {
		return watermarkTimestamp;
	}

	public void setWatermarkTimestamp(String watermarkTimestamp) {
		this.watermarkTimestamp = watermarkTimestamp;
	}
}
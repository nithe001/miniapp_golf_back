package com.golf.golf.db;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * AbstractMatchInfo entity provides the base persistence definition of the
 * MatchInfo entity. @author MyEclipse Persistence Tools
 */
@MappedSuperclass
public abstract class AbstractMatchInfo implements java.io.Serializable {

	// Fields

	private Long id;
	private Long matchTime;
	private String title;
	private String digest;
	private String thumb;
	private String content;
	private String address;
	private Integer isOpen;
	private Integer hit;
	private Long applyEndTime;
	private String createUserName;
	private Long createUserId;
	private Long createTime;
	private String updateUserName;
	private Long updateUserId;
	private Long updateTime;

	// Constructors

	/** default constructor */
	public AbstractMatchInfo() {
	}

	/** full constructor */
	public AbstractMatchInfo(Long matchTime, String title, String digest,
			String thumb, String content, String address, Integer isOpen,
			Integer hit, Long applyEndTime, String createUserName,
			Long createUserId, Long createTime, String updateUserName,
			Long updateUserId, Long updateTime) {
		this.matchTime = matchTime;
		this.title = title;
		this.digest = digest;
		this.thumb = thumb;
		this.content = content;
		this.address = address;
		this.isOpen = isOpen;
		this.hit = hit;
		this.applyEndTime = applyEndTime;
		this.createUserName = createUserName;
		this.createUserId = createUserId;
		this.createTime = createTime;
		this.updateUserName = updateUserName;
		this.updateUserId = updateUserId;
		this.updateTime = updateTime;
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

	@Column(name = "match_time")
	public Long getMatchTime() {
		return this.matchTime;
	}

	public void setMatchTime(Long matchTime) {
		this.matchTime = matchTime;
	}

	@Column(name = "title", length = 128)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "digest", length = 512)
	public String getDigest() {
		return this.digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	@Column(name = "thumb", length = 128)
	public String getThumb() {
		return this.thumb;
	}

	public void setThumb(String thumb) {
		this.thumb = thumb;
	}

	@Column(name = "content", length = 65535)
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name = "address", length = 128)
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "is_open")
	public Integer getIsOpen() {
		return this.isOpen;
	}

	public void setIsOpen(Integer isOpen) {
		this.isOpen = isOpen;
	}

	@Column(name = "hit")
	public Integer getHit() {
		return this.hit;
	}

	public void setHit(Integer hit) {
		this.hit = hit;
	}

	@Column(name = "apply_end_time")
	public Long getApplyEndTime() {
		return this.applyEndTime;
	}

	public void setApplyEndTime(Long applyEndTime) {
		this.applyEndTime = applyEndTime;
	}

	@Column(name = "create_user_name", length = 128)
	public String getCreateUserName() {
		return this.createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	@Column(name = "create_user_id")
	public Long getCreateUserId() {
		return this.createUserId;
	}

	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}

	@Column(name = "create_time")
	public Long getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	@Column(name = "update_user_name", length = 128)
	public String getUpdateUserName() {
		return this.updateUserName;
	}

	public void setUpdateUserName(String updateUserName) {
		this.updateUserName = updateUserName;
	}

	@Column(name = "update_user_id")
	public Long getUpdateUserId() {
		return this.updateUserId;
	}

	public void setUpdateUserId(Long updateUserId) {
		this.updateUserId = updateUserId;
	}

	@Column(name = "update_time")
	public Long getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

}
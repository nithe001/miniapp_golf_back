package com.kingyee.golf.db;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * AbstractMatch entity provides the base persistence definition of the Match
 * entity. @author MyEclipse Persistence Tools
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
	private Integer isDel;
	private Integer isOpen;
	private Integer state;
	private Integer hit;
	private String createUserName;
	private Long createUserId;
	private Long createTime;
	private Long applyStartTime;
	private Long applyEndTime;

	// Constructors

	/** default constructor */
	public AbstractMatchInfo() {
	}

	/** full constructor */
	public AbstractMatchInfo(Long matchTime, String title, String digest,
							 String thumb, String content, String address, Integer isDel,
							 Integer isOpen, Integer state, Integer hit, String createUserName,
							 Long createUserId, Long createTime, Long applyStartTime,
							 Long applyEndTime) {
		this.matchTime = matchTime;
		this.title = title;
		this.digest = digest;
		this.thumb = thumb;
		this.content = content;
		this.address = address;
		this.isDel = isDel;
		this.isOpen = isOpen;
		this.state = state;
		this.hit = hit;
		this.createUserName = createUserName;
		this.createUserId = createUserId;
		this.createTime = createTime;
		this.applyStartTime = applyStartTime;
		this.applyEndTime = applyEndTime;
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

	@Column(name = "is_del")
	public Integer getIsDel() {
		return this.isDel;
	}

	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}

	@Column(name = "is_open")
	public Integer getIsOpen() {
		return this.isOpen;
	}

	public void setIsOpen(Integer isOpen) {
		this.isOpen = isOpen;
	}

	@Column(name = "state")
	public Integer getState() {
		return this.state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	@Column(name = "hit")
	public Integer getHit() {
		return this.hit;
	}

	public void setHit(Integer hit) {
		this.hit = hit;
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

	@Column(name = "apply_start_time")
	public Long getApplyStartTime() {
		return this.applyStartTime;
	}

	public void setApplyStartTime(Long applyStartTime) {
		this.applyStartTime = applyStartTime;
	}

	@Column(name = "apply_end_time")
	public Long getApplyEndTime() {
		return this.applyEndTime;
	}

	public void setApplyEndTime(Long applyEndTime) {
		this.applyEndTime = applyEndTime;
	}

}
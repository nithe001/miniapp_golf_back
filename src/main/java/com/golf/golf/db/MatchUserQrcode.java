package com.golf.golf.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * MatchUserQrcode entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "match_user_qrcode")
public class MatchUserQrcode implements java.io.Serializable {

	// Fields

	private Long muqId;
	private Long muqMatchId;
	private Long muqGroupId;
	private Long muqMatchUserId;
	private Integer muqType;
	private String muqQrcodePath;
	private Long muqCreateTime;

	// Constructors

	/** default constructor */
	public MatchUserQrcode() {
	}

	/** full constructor */
	public MatchUserQrcode(Long muqMatchId, Long muqGroupId,
			Long muqMatchUserId, Integer muqType, String muqQrcodePath,
			Long muqCreateTime) {
		this.muqMatchId = muqMatchId;
		this.muqGroupId = muqGroupId;
		this.muqMatchUserId = muqMatchUserId;
		this.muqType = muqType;
		this.muqQrcodePath = muqQrcodePath;
		this.muqCreateTime = muqCreateTime;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "muq_id", unique = true, nullable = false)
	public Long getMuqId() {
		return this.muqId;
	}

	public void setMuqId(Long muqId) {
		this.muqId = muqId;
	}

	@Column(name = "muq_match_id")
	public Long getMuqMatchId() {
		return this.muqMatchId;
	}

	public void setMuqMatchId(Long muqMatchId) {
		this.muqMatchId = muqMatchId;
	}

	@Column(name = "muq_group_id")
	public Long getMuqGroupId() {
		return this.muqGroupId;
	}

	public void setMuqGroupId(Long muqGroupId) {
		this.muqGroupId = muqGroupId;
	}

	@Column(name = "muq_match_user_id")
	public Long getMuqMatchUserId() {
		return this.muqMatchUserId;
	}

	public void setMuqMatchUserId(Long muqMatchUserId) {
		this.muqMatchUserId = muqMatchUserId;
	}

	@Column(name = "muq_type")
	public Integer getMuqType() {
		return this.muqType;
	}

	public void setMuqType(Integer muqType) {
		this.muqType = muqType;
	}

	@Column(name = "muq_qrcode_path", length = 128)
	public String getMuqQrcodePath() {
		return this.muqQrcodePath;
	}

	public void setMuqQrcodePath(String muqQrcodePath) {
		this.muqQrcodePath = muqQrcodePath;
	}

	@Column(name = "muq_create_time")
	public Long getMuqCreateTime() {
		return this.muqCreateTime;
	}

	public void setMuqCreateTime(Long muqCreateTime) {
		this.muqCreateTime = muqCreateTime;
	}

}
package com.golf.golf.db;

import com.golf.common.util.TimeUtil;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "match_rule")
public class MatchRule {

	// Fields

	private Long mrId;
	private String mrTitle;
	private String mrUrl;
	private Integer mrSort;

	// Constructors

	/** default constructor */
	public MatchRule() {
	}

	/** full constructor */
	public MatchRule(String mrTitle, String mrUrl, Integer mrSort) {
		this.mrTitle = mrTitle;
		this.mrUrl = mrUrl;
		this.mrSort = mrSort;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "mr_id", unique = true, nullable = false)
	public Long getMrId() {
		return mrId;
	}

	public void setMrId(Long mrId) {
		this.mrId = mrId;
	}


	@Column(name = "mr_title", length = 255)
	public String getMrTitle() {
		return mrTitle;
	}

	public void setMrTitle(String mrTitle) {
		this.mrTitle = mrTitle;
	}


	@Column(name = "mr_url", length = 512)
	public String getMrUrl() {
		return mrUrl;
	}

	public void setMrUrl(String mrUrl) {
		this.mrUrl = mrUrl;
	}

	@Column(name = "mr_sort")
	public Integer getMrSort() {
		return mrSort;
	}

	public void setMrSort(Integer mrSort) {
		this.mrSort = mrSort;
	}
}

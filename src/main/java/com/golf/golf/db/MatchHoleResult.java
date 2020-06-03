package com.golf.golf.db;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "match_hole_result")
public class MatchHoleResult implements java.io.Serializable {

	// Fields

	private Long mhrId;
	private Long mhrMatchId;
    private Integer mhrMatchChildId;
	private Long mhrGroupId;
	private Long mhrTeamId;
    private String mhrUserName1;
    private String mhrUserName0;
	private Integer mhrResult;
    private Integer mhrHoleLeft;
	private Integer mhrIsSubmit;


	// Constructors

	/**
	 * default constructor
	 */
	public MatchHoleResult() {
	}

	/**
	 * full constructor
	 */
	public MatchHoleResult(Long mhrId, Long mhrMatchId,Integer mhrMatchChildId, Long mhrGroupId, Long mhrTeamId,String mhrUserName1,String mhrUserName0,
						   Integer mhrResult,Integer mhrHoleLeft,Integer mhrIsSubmit) {
		this.mhrId = mhrId;
		this.mhrMatchId = mhrMatchId;
        this.mhrMatchChildId = mhrMatchChildId;
		this.mhrGroupId = mhrGroupId;
		this.mhrTeamId = mhrTeamId;
        this.mhrUserName1 = mhrUserName1;
        this.mhrUserName0 = mhrUserName0;
        this.mhrResult = mhrResult;
        this.mhrHoleLeft = mhrHoleLeft;
		this.mhrIsSubmit = mhrIsSubmit;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "mhr_id", unique = true, nullable = false)
	public Long getMhrId() {
		return this.mhrId;
	}

	public void setMhrId(Long mhrId) {
		this.mhrId = mhrId;
	}

	@Column(name = "mhr_match_id")
	public Long getMhrMatchId() {
		return mhrMatchId;
	}

	public void setMhrMatchId(Long mhrMatchId) {
		this.mhrMatchId = mhrMatchId;
	}

    @Column(name = "mhr_match_child_id")
    public Integer getMhrMatchChildId() {
        return mhrMatchChildId;
    }

    public void setMhrMatchChildId(Integer mhrMatchChildId) {
        this.mhrMatchChildId = mhrMatchChildId;
    }

	@Column(name = "mhr_group_id")
	public Long getMhrGroupId() {
		return mhrGroupId;
	}

	public void setMhrGroupId(Long mhrGroupId) {
		this.mhrGroupId = mhrGroupId;
	}

	@Column(name = "mhr_team_id")
	public Long getMhrTeamId() {
		return mhrTeamId;
	}

	public void setMhrTeamId(Long mhrTeamId) {
		this.mhrTeamId = mhrTeamId;
	}

    @Column(name = "mhr_user_name1")
    public String getMhrUserName1() {
        return mhrUserName1;
    }

    public void setMhrUserName1(String mhrUserName1) {
        this.mhrUserName1 = mhrUserName1;
    }

    @Column(name = "mhr_user_name0")
    public String getMhrUserName0() {
        return mhrUserName0;
    }

    public void setMhrUserName0(String mhrUserName0) {
        this.mhrUserName0 = mhrUserName0;
    }

    @Column(name = "mhr_result")
    public Integer getMhrResult() {
        return mhrResult;
    }

    public void setMhrResult(Integer mhrResult) {
        this.mhrResult = mhrResult;
    }

    @Column(name = "mhr_hole_left")
    public Integer getMhrHoleLeft() { return mhrHoleLeft; }

    public void setMhrHoleLeft(Integer mhrHoleLeft) { this.mhrHoleLeft = mhrHoleLeft; }

    @Column(name = "mhr_is_submit")
	public Integer getMhrIsSubmit() {
		return mhrIsSubmit;
	}

	public void setMhrIsSubmit(Integer mhrIsSubmit) {
		this.mhrIsSubmit = mhrIsSubmit;
	}
}

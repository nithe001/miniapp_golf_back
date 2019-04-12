package com.golf.golf.db;

import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "park_partition")
public class ParkPartition {
	private Long ppId;
	private Long ppPId;
	private String ppZoneName;
	private Integer ppHoleNum;
	private Integer ppHoleStandardRod;

	// Constructors

	/** default constructor */
	public ParkPartition() {
	}

	/** full constructor */
	public ParkPartition(Long ppPId, String ppZoneName, Integer ppHoleNum,
						 Integer ppHoleStandardRod) {
		this.ppPId = ppPId;
		this.ppZoneName = ppZoneName;
		this.ppHoleNum = ppHoleNum;
		this.ppHoleStandardRod = ppHoleStandardRod;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "pp_id", unique = true, nullable = false)
	public Long getPpId() {
		return this.ppId;
	}

	public void setPpId(Long ppId) {
		this.ppId = ppId;
	}

	@Column(name = "pp_p_id")
	public Long getPpPId() {
		return this.ppPId;
	}

	public void setPpPId(Long ppPId) {
		this.ppPId = ppPId;
	}

	@Column(name = "pp_zone_name", length = 128)
	public String getPpZoneName() {
		return this.ppZoneName;
	}

	public void setPpZoneName(String ppZoneName) {
		this.ppZoneName = ppZoneName;
	}

	@Column(name = "pp_hole_num")
	public Integer getPpHoleNum() {
		return this.ppHoleNum;
	}

	public void setPpHoleNum(Integer ppHoleNum) {
		this.ppHoleNum = ppHoleNum;
	}

	@Column(name = "pp_hole_standard_rod")
	public Integer getPpHoleStandardRod() {
		return this.ppHoleStandardRod;
	}

	public void setPpHoleStandardRod(Integer ppHoleStandardRod) {
		this.ppHoleStandardRod = ppHoleStandardRod;
	}

}

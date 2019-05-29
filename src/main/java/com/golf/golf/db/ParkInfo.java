package com.golf.golf.db;

import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "park_info")
public class ParkInfo implements Comparable<ParkInfo>{

	// Fields

	private Long piId;
	private String piCity;
	private String piName;
	private String piAddress;
	private String piLogo;
	private Integer piIsValid;
	private String piLng;
	private String piLat;
	private Integer piSumRod;

	// Constructors

	/** default constructor */
	public ParkInfo() {
	}

	/** full constructor */
	public ParkInfo(String piCity, String piName, String piAddress, String piLogo,
					Integer piIsValid, String piLng, String piLat, Integer piSumRod) {
		this.piCity = piCity;
		this.piName = piName;
		this.piAddress = piAddress;
		this.piLogo = piLogo;
		this.piIsValid = piIsValid;
		this.piLng = piLng;
		this.piLat = piLat;
		this.piSumRod = piSumRod;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "pi_id", unique = true, nullable = false)
	public Long getPiId() {
		return this.piId;
	}

	public void setPiId(Long piId) {
		this.piId = piId;
	}

	@Column(name = "pi_city")
	public String getPiCity() {
		return this.piCity;
	}

	public void setPiCity(String piCity) {
		this.piCity = piCity;
	}

	@Column(name = "pi_name")
	public String getPiName() {
		return this.piName;
	}

	public void setPiName(String piName) {
		this.piName = piName;
	}

	@Column(name = "pi_address")
	public String getPiAddress() {
		return piAddress;
	}

	public void setPiAddress(String piAddress) {
		this.piAddress = piAddress;
	}

	@Column(name = "pi_logo")
	public String getPiLogo() {
		return this.piLogo;
	}

	public void setPiLogo(String piLogo) {
		this.piLogo = piLogo;
	}

	@Column(name = "pi_is_valid")
	public Integer getPiIsValid() {
		return this.piIsValid;
	}

	public void setPiIsValid(Integer piIsValid) {
		this.piIsValid = piIsValid;
	}

	@Column(name = "pi_lng", length = 128)
	public String getPiLng() {
		return this.piLng;
	}

	public void setPiLng(String piLng) {
		this.piLng = piLng;
	}

	@Column(name = "pi_lat", length = 128)
	public String getPiLat() {
		return this.piLat;
	}

	public void setPiLat(String piLat) {
		this.piLat = piLat;
	}

	@Column(name = "pi_sum_rod")
	public Integer getPiSumRod() {
		return piSumRod;
	}

	public void setPiSumRod(Integer piSumRod) {
		this.piSumRod = piSumRod;
	}

	//到我的距离
	private Integer toMyDistance;
	@Transient

	public Integer getToMyDistance() {
		return toMyDistance;
	}

	public void setToMyDistance(Integer toMyDistance) {
		this.toMyDistance = toMyDistance;
	}

	//距离小的靠前排
	@Override
	public int compareTo(ParkInfo parkInfo) {
		return this.toMyDistance - parkInfo.toMyDistance;
	}
}

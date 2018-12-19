package com.golf.golf.db;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * AbstractGolfTeamInfo entity provides the base persistence definition of the
 * GolfTeamInfo entity. @author MyEclipse Persistence Tools
 */
@MappedSuperclass
public abstract class AbstractGolfTeamInfo implements java.io.Serializable {

	// Fields

	private Integer id;

	// Constructors

	/** default constructor */
	public AbstractGolfTeamInfo() {
	}

	/** full constructor */
	public AbstractGolfTeamInfo(Integer id) {
		this.id = id;
	}

	// Property accessors
	@Id
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
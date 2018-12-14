package com.kingyee.golf.db;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * AbstractTeam entity provides the base persistence definition of the Team
 * entity. @author MyEclipse Persistence Tools
 */
@MappedSuperclass
public abstract class AbstractTeam implements java.io.Serializable {

	// Fields

	private Integer id;

	// Constructors

	/** default constructor */
	public AbstractTeam() {
	}

	/** full constructor */
	public AbstractTeam(Integer id) {
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
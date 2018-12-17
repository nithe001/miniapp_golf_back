package com.golf.golf.db;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Team entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "team_info")
public class TeamInfo extends AbstractTeam implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public TeamInfo() {
	}

	/** full constructor */
	public TeamInfo(Integer id) {
		super(id);
	}

}

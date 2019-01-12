package com.golf.golf.db;

import javax.persistence.*;

@Entity
@Table(name = "team_info")
public class TeamInfo {
    private Long tiTeamId;
    private String tiTeamLogo;
    private String tiTeamName;
    private String tiAddress;
    private String tiSlogan;
    private Integer tiJoinOpenType;
    private Integer tiInfoOpenType;
    private Integer tiMatchResultAuditType;

    @Id
    @Column(name = "ti_team_id")
    public Long getTiTeamId() {
        return tiTeamId;
    }

    public void setTiTeamId(Long tiTeamId) {
        this.tiTeamId = tiTeamId;
    }

    @Basic
    @Column(name = "ti_team_logo")
    public String getTiTeamLogo() {
        return tiTeamLogo;
    }

    public void setTiTeamLogo(String tiTeamLogo) {
        this.tiTeamLogo = tiTeamLogo;
    }

    @Basic
    @Column(name = "ti_team_name")
    public String getTiTeamName() {
        return tiTeamName;
    }

    public void setTiTeamName(String tiTeamName) {
        this.tiTeamName = tiTeamName;
    }

    @Basic
    @Column(name = "ti_address")
    public String getTiAddress() {
        return tiAddress;
    }

    public void setTiAddress(String tiAddress) {
        this.tiAddress = tiAddress;
    }

    @Basic
    @Column(name = "ti_slogan")
    public String getTiSlogan() {
        return tiSlogan;
    }

    public void setTiSlogan(String tiSlogan) {
        this.tiSlogan = tiSlogan;
    }

    @Basic
    @Column(name = "ti_join_open_type")
    public Integer getTiJoinOpenType() {
        return tiJoinOpenType;
    }

    public void setTiJoinOpenType(Integer tiJoinOpenType) {
        this.tiJoinOpenType = tiJoinOpenType;
    }

    @Basic
    @Column(name = "ti_info_open_type")
    public Integer getTiInfoOpenType() {
        return tiInfoOpenType;
    }

    public void setTiInfoOpenType(Integer tiInfoOpenType) {
        this.tiInfoOpenType = tiInfoOpenType;
    }

    @Basic
    @Column(name = "ti_match_result_audit_type")
    public Integer getTiMatchResultAuditType() {
        return tiMatchResultAuditType;
    }

    public void setTiMatchResultAuditType(Integer tiMatchResultAuditType) {
        this.tiMatchResultAuditType = tiMatchResultAuditType;
    }
}

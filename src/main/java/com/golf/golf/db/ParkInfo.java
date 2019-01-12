package com.golf.golf.db;

import javax.persistence.*;

@Entity
@Table(name = "park_info")
public class ParkInfo {
    private Long piId;
    private String piName;
    private String piLogo;
    private String piAddress;
    private Integer piIsValid;
    private Long piCreateTime;
    private Long piCreateUserId;
    private String piCreateUserName;
    private Long piUpdateTime;
    private Long piUpdateUserId;
    private String piUpdateUserName;

    @Id
    @Column(name = "pi_id")
    public Long getPiId() {
        return piId;
    }

    public void setPiId(Long piId) {
        this.piId = piId;
    }

    @Basic
    @Column(name = "pi_name")
    public String getPiName() {
        return piName;
    }

    public void setPiName(String piName) {
        this.piName = piName;
    }

    @Basic
    @Column(name = "pi_logo")
    public String getPiLogo() {
        return piLogo;
    }

    public void setPiLogo(String piLogo) {
        this.piLogo = piLogo;
    }

    @Basic
    @Column(name = "pi_address")
    public String getPiAddress() {
        return piAddress;
    }

    public void setPiAddress(String piAddress) {
        this.piAddress = piAddress;
    }


    @Basic
    @Column(name = "pi_is_valid")
    public Integer getPiIsValid() {
        return piIsValid;
    }
    public void setPiIsValid(Integer piIsValid) {
        this.piIsValid = piIsValid;
    }

    @Basic
    @Column(name = "pi_create_time")
    public Long getPiCreateTime() {
        return piCreateTime;
    }

    public void setPiCreateTime(Long piCreateTime) {
        this.piCreateTime = piCreateTime;
    }

    @Basic
    @Column(name = "pi_create_user_id")
    public Long getPiCreateUserId() {
        return piCreateUserId;
    }

    public void setPiCreateUserId(Long piCreateUserId) {
        this.piCreateUserId = piCreateUserId;
    }

    @Basic
    @Column(name = "pi_create_user_name")
    public String getPiCreateUserName() {
        return piCreateUserName;
    }

    public void setPiCreateUserName(String piCreateUserName) {
        this.piCreateUserName = piCreateUserName;
    }

    @Basic
    @Column(name = "pi_update_time")
    public Long getPiUpdateTime() {
        return piUpdateTime;
    }

    public void setPiUpdateTime(Long piUpdateTime) {
        this.piUpdateTime = piUpdateTime;
    }

    @Basic
    @Column(name = "pi_update_user_id")
    public Long getPiUpdateUserId() {
        return piUpdateUserId;
    }

    public void setPiUpdateUserId(Long piUpdateUserId) {
        this.piUpdateUserId = piUpdateUserId;
    }

    @Basic
    @Column(name = "pi_update_user_name")
    public String getPiUpdateUserName() {
        return piUpdateUserName;
    }

    public void setPiUpdateUserName(String piUpdateUserName) {
        this.piUpdateUserName = piUpdateUserName;
    }
}

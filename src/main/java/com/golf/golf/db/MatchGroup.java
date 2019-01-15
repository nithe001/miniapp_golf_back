package com.golf.golf.db;

import javax.persistence.*;

@Entity
@Table(name = "match_group")
public class MatchGroup {
    private long mgId;
    private Long mgMatchId;
    private String mgGroupName;
    private Long mgCreateUserId;
    private String mgCreateUserName;
    private Long mgCreateTime;
    private Long mgUpdateUserId;
    private String mgUpdateUserName;
    private Long mgUpdateTime;

    @Id
    @Column(name = "mg_id")
    public long getMgId() {
        return mgId;
    }

    public void setMgId(long mgId) {
        this.mgId = mgId;
    }

    @Basic
    @Column(name = "mg_match_id")
    public Long getMgMatchId() {
        return mgMatchId;
    }

    public void setMgMatchId(Long mgMatchId) {
        this.mgMatchId = mgMatchId;
    }

    @Basic
    @Column(name = "mg_group_name")
    public String getMgGroupName() {
        return mgGroupName;
    }

    public void setMgGroupName(String mgGroupName) {
        this.mgGroupName = mgGroupName;
    }

    @Basic
    @Column(name = "mg_create_user_id")
    public Long getMgCreateUserId() {
        return mgCreateUserId;
    }

    public void setMgCreateUserId(Long mgCreateUserId) {
        this.mgCreateUserId = mgCreateUserId;
    }

    @Basic
    @Column(name = "mg_create_user_name")
    public String getMgCreateUserName() {
        return mgCreateUserName;
    }

    public void setMgCreateUserName(String mgCreateUserName) {
        this.mgCreateUserName = mgCreateUserName;
    }

    @Basic
    @Column(name = "mg_create_time")
    public Long getMgCreateTime() {
        return mgCreateTime;
    }

    public void setMgCreateTime(Long mgCreateTime) {
        this.mgCreateTime = mgCreateTime;
    }

    @Basic
    @Column(name = "mg_update_user_id")
    public Long getMgUpdateUserId() {
        return mgUpdateUserId;
    }

    public void setMgUpdateUserId(Long mgUpdateUserId) {
        this.mgUpdateUserId = mgUpdateUserId;
    }

    @Basic
    @Column(name = "mg_update_user_name")
    public String getMgUpdateUserName() {
        return mgUpdateUserName;
    }

    public void setMgUpdateUserName(String mgUpdateUserName) {
        this.mgUpdateUserName = mgUpdateUserName;
    }

    @Basic
    @Column(name = "mg_update_time")
    public Long getMgUpdateTime() {
        return mgUpdateTime;
    }

    public void setMgUpdateTime(Long mgUpdateTime) {
        this.mgUpdateTime = mgUpdateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MatchGroup that = (MatchGroup) o;

        if (mgId != that.mgId) return false;
        if (mgMatchId != null ? !mgMatchId.equals(that.mgMatchId) : that.mgMatchId != null) return false;
        if (mgGroupName != null ? !mgGroupName.equals(that.mgGroupName) : that.mgGroupName != null) return false;
        if (mgCreateUserId != null ? !mgCreateUserId.equals(that.mgCreateUserId) : that.mgCreateUserId != null)
            return false;
        if (mgCreateUserName != null ? !mgCreateUserName.equals(that.mgCreateUserName) : that.mgCreateUserName != null)
            return false;
        if (mgCreateTime != null ? !mgCreateTime.equals(that.mgCreateTime) : that.mgCreateTime != null) return false;
        if (mgUpdateUserId != null ? !mgUpdateUserId.equals(that.mgUpdateUserId) : that.mgUpdateUserId != null)
            return false;
        if (mgUpdateUserName != null ? !mgUpdateUserName.equals(that.mgUpdateUserName) : that.mgUpdateUserName != null)
            return false;
        if (mgUpdateTime != null ? !mgUpdateTime.equals(that.mgUpdateTime) : that.mgUpdateTime != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (mgId ^ (mgId >>> 32));
        result = 31 * result + (mgMatchId != null ? mgMatchId.hashCode() : 0);
        result = 31 * result + (mgGroupName != null ? mgGroupName.hashCode() : 0);
        result = 31 * result + (mgCreateUserId != null ? mgCreateUserId.hashCode() : 0);
        result = 31 * result + (mgCreateUserName != null ? mgCreateUserName.hashCode() : 0);
        result = 31 * result + (mgCreateTime != null ? mgCreateTime.hashCode() : 0);
        result = 31 * result + (mgUpdateUserId != null ? mgUpdateUserId.hashCode() : 0);
        result = 31 * result + (mgUpdateUserName != null ? mgUpdateUserName.hashCode() : 0);
        result = 31 * result + (mgUpdateTime != null ? mgUpdateTime.hashCode() : 0);
        return result;
    }
}

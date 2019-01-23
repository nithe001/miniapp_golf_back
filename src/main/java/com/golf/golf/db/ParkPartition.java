package com.golf.golf.db;

import javax.persistence.*;

@Entity
@Table(name = "park_partition")
public class ParkPartition {
    private Long ppId;
    private Long ppPId;
    private String ppName;
    private Integer ppHoleNum;
    private Integer ppHoleStandardRod;

    @Id
    @Column(name = "pp_id")
    public Long getPpId() {
        return ppId;
    }

    public void setPpId(Long ppId) {
        this.ppId = ppId;
    }

    @Basic
    @Column(name = "pp_p_id")
    public Long getPpPId() {
        return ppPId;
    }

    public void setPpPId(Long ppPId) {
        this.ppPId = ppPId;
    }

    @Basic
    @Column(name = "pp_name")
    public String getPpName() {
        return ppName;
    }

    public void setPpName(String ppName) {
        this.ppName = ppName;
    }

    @Basic
    @Column(name = "pp_hole_num")
    public Integer getPpHoleNum() {
        return ppHoleNum;
    }

    public void setPpHoleNum(Integer ppHoleNum) {
        this.ppHoleNum = ppHoleNum;
    }

    @Basic
    @Column(name = "pp_hole_standard_rod")
    public Integer getPpHoleStandardRod() {
        return ppHoleStandardRod;
    }

    public void setPpHoleStandardRod(Integer ppHoleStandardRod) {
        this.ppHoleStandardRod = ppHoleStandardRod;
    }

}

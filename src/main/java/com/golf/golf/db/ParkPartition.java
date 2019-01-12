package com.golf.golf.db;

import javax.persistence.*;

@Entity
@Table(name = "park_partition")
public class ParkPartition {
    private long ppId;
    private Long ppPId;
    private String ppName;
    private Integer ppHoleNum;
    private Integer ppHoleStandardRod;
    private Integer ppHoleTBlackDistance;
    private Integer ppHoleTGoldDistance;
    private Integer ppHoleTBlueDistance;
    private Integer ppHoleTWhiteDistance;
    private Integer ppHoleTRedDistance;

    @Id
    @Column(name = "pp_id")
    public long getPpId() {
        return ppId;
    }

    public void setPpId(long ppId) {
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

    @Basic
    @Column(name = "pp_hole_t_black_distance")
    public Integer getPpHoleTBlackDistance() {
        return ppHoleTBlackDistance;
    }

    public void setPpHoleTBlackDistance(Integer ppHoleTBlackDistance) {
        this.ppHoleTBlackDistance = ppHoleTBlackDistance;
    }

    @Basic
    @Column(name = "pp_hole_t_gold_distance")
    public Integer getPpHoleTGoldDistance() {
        return ppHoleTGoldDistance;
    }

    public void setPpHoleTGoldDistance(Integer ppHoleTGoldDistance) {
        this.ppHoleTGoldDistance = ppHoleTGoldDistance;
    }

    @Basic
    @Column(name = "pp_hole_t_blue_distance")
    public Integer getPpHoleTBlueDistance() {
        return ppHoleTBlueDistance;
    }

    public void setPpHoleTBlueDistance(Integer ppHoleTBlueDistance) {
        this.ppHoleTBlueDistance = ppHoleTBlueDistance;
    }

    @Basic
    @Column(name = "pp_hole_t_white_distance")
    public Integer getPpHoleTWhiteDistance() {
        return ppHoleTWhiteDistance;
    }

    public void setPpHoleTWhiteDistance(Integer ppHoleTWhiteDistance) {
        this.ppHoleTWhiteDistance = ppHoleTWhiteDistance;
    }

    @Basic
    @Column(name = "pp_hole_t_red_distance")
    public Integer getPpHoleTRedDistance() {
        return ppHoleTRedDistance;
    }

    public void setPpHoleTRedDistance(Integer ppHoleTRedDistance) {
        this.ppHoleTRedDistance = ppHoleTRedDistance;
    }

}

package com.golf.golf.bean;

import com.golf.golf.db.ParkInfo;
import com.golf.golf.db.ParkPartition;

import java.util.List;

/**
 * 比赛场地 bean
 * Created by dev on 17-2-10
 */
public class ParkInfoBean {

    private ParkInfo parkInfo;
    private List<ParkPartition> parkPartitionList;

	public ParkInfo getParkInfo() {
		return parkInfo;
	}

	public void setParkInfo(ParkInfo parkInfo) {
		this.parkInfo = parkInfo;
	}

	public List<ParkPartition> getParkPartitionList() {
		return parkPartitionList;
	}

	public void setParkPartitionList(List<ParkPartition> parkPartitionList) {
		this.parkPartitionList = parkPartitionList;
	}
}

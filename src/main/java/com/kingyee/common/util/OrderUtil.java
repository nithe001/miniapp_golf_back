package com.kingyee.common.util;

import java.util.Date;
import java.util.Random;

public class OrderUtil {
	
	/**
	 * 生成订单号
	 * @return
	 */
	public static String generateOrderNo(){
		String date = TimeUtil.dateToString(new Date(), "yyMMddHHmmss");
		int start1 = new Random().nextInt(10);
		int start2 = new Random().nextInt(10);
		int end = new Random().nextInt(10);
		return start1 + "" + start2 + date + end;
	}

	/**
	 * 生成子订单号
	 * @return
	 */
	public static String generateSubOrderNo(){
		String date = TimeUtil.dateToString(new Date(), "yyMMddHHmmss");
		int start1 = new Random().nextInt(10);
		int start2 = new Random().nextInt(10);
		int end = new Random().nextInt(10);
		int end2 = new Random().nextInt(10);
		return start1 + "" + start2 + date + end + end2;
	}
	
	public static void main(String[] args) {
		System.out.println(OrderUtil.generateOrderNo());
		System.out.println(OrderUtil.generateSubOrderNo());
	}
	
}

package com.java.helper;

public class ThreadHelper {
	
	
	/**
	 * 休眠模拟 rpc执行时间
	 * @param timeßß
	 */
	public static void sleep(long timeßß){
		try {

			Thread.currentThread().sleep(timeßß);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}

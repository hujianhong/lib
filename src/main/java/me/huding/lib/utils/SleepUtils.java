package me.huding.lib.utils;

/**
 * 
 *
 *
 * @author JianhongHu
 * @version 1.0
 * @date 2016年3月22日
 */
public class SleepUtils {
	
	/**
	 * 指数休眠
	 * 
	 * 
	 * @param paramInt 
	 */
	public static void pauseExp(int paramInt) {
		if (0 == paramInt) { return;}
		long l1 = 300L;
		long l2 = (long) (Math.pow(2.0D, paramInt) * l1);
		l2 = Math.min(l2, 20000L);
		try {Thread.sleep(l2);} catch (InterruptedException e){}
	}
	
	/**
	 * 
	 * @param num
	 * @param baseSleep
	 */
	public static void pause(int num,long baseSleep){
		if (0 == num) { return;}
		long l2 = (long) (Math.pow(2.0D, num % 5) * baseSleep);
		try {Thread.sleep(l2);} catch (InterruptedException e){}
	}
	
	/**
	 * 线程休眠 
	 * 
	 * @param sleep 毫秒时间
	 */
	public static void pause(long sleep){
		if (0 == sleep) { return;}
		try {Thread.sleep(sleep);} catch (InterruptedException e){}
	}
}

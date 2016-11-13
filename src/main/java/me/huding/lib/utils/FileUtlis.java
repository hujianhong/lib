/**
 * 
 */
package me.huding.lib.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 *
 *
 * @author JianhongHu
 * @version 1.0
 * @date 2015年12月14日
 */
public class FileUtlis {

	public static void main(String args[]) throws IOException {
		String filePath = buildLockFilePath(2);
		getFileLock(filePath);
	}

	public static String buildLockFilePath(int appID) {
		return SystemUtils.USER_DIR + File.separator + "lock" + File.separator
				+ File.separator + "app_" + appID + ".lock";
	}

	public static FileLock getFileLock(int appID) throws IOException {
		return getFileLock(buildLockFilePath(appID));
	}

	@SuppressWarnings("resource")
	public static FileLock getFileLock(String filePath) throws IOException {
		File file = new File(filePath);
		if (!file.exists()) {
			if (file.getParentFile().exists()) {
				file.createNewFile();
			} else {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
		}
		FileOutputStream fos = new FileOutputStream(file);
		FileChannel fileChannel = fos.getChannel();
		// 获取文件的锁
		FileLock fileLock = fileChannel.tryLock();
		return fileLock;
	}

	/**
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static boolean checkFileLock(int appID) throws IOException {
		return checkFileLock(buildLockFilePath(appID));
	}

	/**
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static boolean checkFileLock(String filePath) throws IOException {
		File file = new File(filePath);
		if (!file.exists()) {
			if (file.getParentFile().exists()) {
				file.createNewFile();
			} else {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			FileChannel fileChannel = fos.getChannel();
			// 获取文件的锁
			FileLock fileLock = fileChannel.tryLock();
			// 获取文件锁失败
			if (fileLock == null) {
				return false;
			}
			// 获取锁成功，这释放该锁
			else {
				fileLock.release();
				return true;
			}
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
	}
}

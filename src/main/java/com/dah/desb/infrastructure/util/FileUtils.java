package com.dah.desb.infrastructure.util;

import java.io.File;
import java.io.IOException;

import org.springframework.util.FileCopyUtils;

public class FileUtils {
	
	/*
	 * 创建目录，不管目录有多深，父级目录是否存在
	 */
	public static File createDir(String path) {
		File temp = new File(path);
		if(!temp.exists())
			temp.mkdirs();
		return temp;
	}
	
	/*
	 * 创建文件，如果已存在则先删除原来的再创建
	 * 如果父级目录不存在则自动创建
	 */
	public static File createFile(String path) throws IOException{
		File temp = new File(path);
		if(temp.exists()) {
			temp.delete();
		}
		File parent = temp.getParentFile();
		if(!parent.exists()) {
			createDir(parent.getAbsolutePath());
		}
		temp.createNewFile();
		return temp;
	}
	
	/*
	 * 删除所有子目录及文件
	 * 如果includeSelf==true则也删除目录本身，否则仅删除子目录及文件
	 */
	public static void deleteDir(String path, boolean deleteSelfToo) {
		//todo
	}
	
	/*
	 * 判断路径指向的文件是否存在
	 */
	public static boolean isFileExists(String path) {
		return new File(path).exists();
	}
	
	/*
	 * 删除文件
	 */
	public static boolean deleteFile(String path) {
		File temp = new File(path);
		if(temp.exists()){
			return temp.delete();
		} else {
			return true;
		}
	}
	
	/*
	 * 取得文件名称
	 */
	public static String getFileName(String path) {
		return new File(path).getName();
	}
	
	public static String getFileSuffix(String path) {
		String suffix = "";
		String name = getFileName(path);
		if (name.indexOf(".") > 0) {
			suffix = name.substring(name.lastIndexOf(".") + 1);
		}
		return suffix;
	}
	
	/*
	 * 拷贝文件，如果out不存在则自动创建
	 */
	public static int copyFile(File in, File out) throws IOException {
		if (!out.exists()) {
			createFile(out.getAbsolutePath());
		}
		return FileCopyUtils.copy(in, out);
	}
	
}

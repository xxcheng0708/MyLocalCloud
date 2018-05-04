package com.test.tools;

import java.util.Stack;
import com.test.ui.LoginSuccess;
/**
 * 工具类
 * @author asus
 *
 */
public class Tools {
	/**
	 * 数据库连接参数
	 */
	public static String DRIVER = "com.mysql.jdbc.Driver" ;
	public static String URL = "jdbc:mysql://127.0.0.1:3306/cloud?characterEncoding=UTF8" ;
	public static String USERNAME = "root" ; 
	public static String PASSWORD = "root" ;
	
	/**
	 * 用于存放父目录ID号的栈
	 */
	public static Stack<Integer> parentIdStack = new Stack<Integer>() ;
	/**
	 * 存储用户ID号
	 */
	public static int userId ;
	/**
	 * 复制的文件夹的ID号
	 */
	public static int cutFileId = -1 ;
	/**
	 * 复制的文件的大小
	 */
	public static long copyFileSize = 0 ;
	/**
	 * 删除文件ID号
	 */
	public static int deleteFileId = -1 ;
	/**
	 * 删除文件的大小
	 */
	public  static long deleteFileSize = 0 ;
	/**
	 * 同时运行的下载任务限制数
	 */
	public static int fileDownloadLimitNumber = 2;
	/**
	 * 同时运行的上传任务限制数
	 */
	public static int fileUploadLimitNumber = 2;
	
	public static LoginSuccess loginSuccess ;
	
}

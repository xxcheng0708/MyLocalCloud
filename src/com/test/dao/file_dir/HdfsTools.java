package com.test.dao.file_dir;

import java.io.IOException ;
import java.net.URI ;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList ;
import java.util.List ;
import java.io.File ;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;  
import org.apache.hadoop.fs.FileSystem;  
import org.apache.hadoop.fs.Path;  
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;

import com.test.db.DBManage;
import com.test.tools.Tools;

/**
 * 操作HDFS文件系统，
 * 1、创建文件夹
 * 2、删除文件（文件夹）
 * 3、上传文件
 * 4、下载文件（文件夹）
 * @author asus
 *
 */

public class HdfsTools {
	
	private DBManage dbmanage ;
	private Connection connection ;
	private PreparedStatement preparedStatement ;
	private ResultSet resultSet ;
//	public static String hdfsUri = "hdfs://192.168.204.130:9000";
	public String hdfsUri ;
	
	public HdfsTools() {
		// 构造方法，设置hadoop环境变量
		System.setProperty("hadoop.home.dir", "E:\\hadoop-2.6.0");
		hdfsUri = getHdfsUri() ;
	}
	
	
	public String getHdfsUri() {
		String hdfsIp = "" ;
		String queryHdfsNodeIp = "select hdfs_ip from hdfs where u_id = ?" ;
		
		try {
			// 数据库操作
			dbmanage = new DBManage();
			connection = dbmanage.getConnection();
			preparedStatement = dbmanage.getPreparedStatement(queryHdfsNodeIp);
			preparedStatement.setInt(1, Tools.userId) ;
			resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) {
				hdfsIp = resultSet.getString("hdfs_ip") ;
				hdfsIp = "hdfs://" + hdfsIp + ":9000" ;
			}
		}catch(Exception e) {
			e.printStackTrace() ;
		}finally {
			// 关闭连接
			if(resultSet != null) {
				try {
					resultSet.close() ;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			dbmanage.closeLink() ;
		}
		return hdfsIp ;
	}
	
	/**
	 * HDFS创建目录
	 * @param dir 目录地址
	 * @return bool
	 * @throws IOException
	 */
	public boolean mkdir(String dir) throws IOException {
		if(StringUtils.isBlank(dir)) {
			return false ;
		}
		dir = hdfsUri + dir ;
		Configuration conf = new Configuration() ;
		FileSystem fs = FileSystem.get(URI.create(dir) , conf) ;
		if(!fs.exists(new Path(dir))) {
			fs.mkdirs(new Path(dir)) ;
		}
		fs.close() ;
		return true ;
	}
	
	/**
	 * HDFS 删除目录
	 * @param dir	目录地址
	 * @return bool
	 * @throws IOException
	 */
	public boolean deleteDir(String dir) throws IOException{
		if(StringUtils.isBlank(dir)) {
			return false ;
		}
		dir = hdfsUri + dir ;
		Configuration conf = new Configuration() ;
		FileSystem fs = FileSystem.get(URI.create(dir) , conf) ;
		fs.delete(new Path(dir) , true) ;
		fs.close() ;
		return true ;
	}
	
	/**
	 * 列出目录下的所有文件及文件夹
	 * @param dir 目录地址
	 * @return	返回文件列表
	 * @throws IOException
	 */
	public List<String> listAll(String dir) throws IOException {
		if(StringUtils.isBlank(dir)) {
			return new ArrayList<String>() ;
		}
		dir = hdfsUri + dir ;
		Configuration conf = new Configuration() ;
		FileSystem fs = FileSystem.get(URI.create(dir) , conf) ;
		FileStatus[] stats = fs.listStatus(new Path(dir)) ;
		List<String> names = new ArrayList<String>() ;
		for(int i = 0 ; i < stats.length ; i ++) {
			if(stats[i].isFile()) {
				names.add(stats[i].getPath().toString()) ;
			}else if(stats[i].isDirectory()) {
				names.add(stats[i].getPath().toString()) ;
			}else {
				names.add(stats[i].getPath().toString()) ;
			}
		}
		fs.close() ;
		return names ;
	}
	
	public boolean uploadFile2Hdfs(String localFile , String hdfsFile) throws IOException {
		System.out.println("enter function >>>>>> uploadFile2Hdfs");
		System.out.println("localFile -> " + localFile + " hdfsFile -> " + hdfsFile);
		if(StringUtils.isBlank(localFile) || StringUtils.isBlank(hdfsFile)) {
			return false ;
		}
		hdfsFile = hdfsUri + hdfsFile ;
		Configuration conf = new Configuration() ;
		FileSystem fs = FileSystem.get(URI.create(hdfsFile) , conf) ;
		Path src = new Path(localFile) ;
		Path dst = new Path(hdfsFile) ;
		
		fs.copyFromLocalFile(src, dst);
		fs.close() ;
		return true ;
	}
	
	/**
	 * 删除文件下载后以crc结尾的文件
	 * @return
	 */
	public boolean deleteCRCFile(String dir) {
		File file = new File(dir) ;
		File[] fileList = file.listFiles() ;
		for(int i = 0 ; i < fileList.length ; i ++) {
			boolean isFile = fileList[i].isFile() ;
			boolean isDir = fileList[i].isDirectory() ;
			if(isFile && fileList[i].getName().endsWith(".crc")) {
				fileList[i].delete() ;
				System.out.println("delete file -> " + fileList[i].getName() + " isFile -> " + isFile);
			}else if(isDir) {
				deleteCRCFile(fileList[i].getAbsolutePath()) ;
			}
		}
		return true ;
	}
	
	/**
	 * 下载HDFS文件到本地目录，递归下载
	 * @param localPath 本地文件目录
	 * @param hdfsPath	远程文件目录
	 * @return
	 * @throws IOException
	 */
	public boolean downloadFileFromHdfs(String localPath , String hdfsPath) throws IOException {
		System.out.println("call function downloadFileFromHdfs");
		// 判断本地下载目录是否存在
		File file = new File(localPath) ;
		if(!file.exists()) {
			// 目录不存在则创建
			file.mkdirs() ;
		}
		if(StringUtils.isBlank(localPath) || StringUtils.isBlank(hdfsPath)) {
			return false ;
		}
		
		String absHdfsPath = hdfsUri + hdfsPath ;
		Configuration conf = new Configuration() ;
		FileSystem fs = FileSystem.get(URI.create(absHdfsPath) , conf) ;
		FileStatus[] stats = fs.listStatus(new Path(absHdfsPath)) ;
		
		for(int i = 0 ; i < stats.length ; i++) {
			if(stats[i].isFile()) {
				// 下载文件
				String filepath = stats[i].getPath().toString() ;
				fs.copyToLocalFile(new Path(filepath) , new Path(localPath));
			}else if(stats[i].isDirectory()) {
				// 如果是目录，递归下载目录中的剩余文件
				String nextDir = stats[i].getPath().toString() ;
				String tempDir = nextDir.substring(absHdfsPath.length() , nextDir.length()) ;
				downloadFileFromHdfs(localPath + tempDir , hdfsPath + tempDir) ;
			}
		}
		return true ;
	}
	
	public boolean downloadFile(String localPath , String hdfsPath) throws IOException {
		System.out.println("call function downloadFileFromHdfs");
		// 判断本地下载目录是否存在
		if(StringUtils.isBlank(localPath) || StringUtils.isBlank(hdfsPath)) {
			return false ;
		}
		
		String absHdfsPath = hdfsUri + hdfsPath ;
		Configuration conf = new Configuration() ;
		FileSystem fs = FileSystem.get(URI.create(absHdfsPath) , conf) ;
		FileStatus[] stats = fs.listStatus(new Path(absHdfsPath)) ;
		
		for(int i = 0 ; i < stats.length ; i++) {
			if(stats[i].isFile()) {
				// 下载文件
				String filepath = stats[i].getPath().toString() ;
				fs.copyToLocalFile(new Path(filepath) , new Path(localPath));
			}
		}
		return true ;
	}
	
	public boolean deleteHdfsFile(String hdfsPath) throws IOException {
		System.out.println("enter function deleteHdfsFile");
		if(StringUtils.isBlank(hdfsPath)) {
			return false ;
		}
		hdfsPath = hdfsUri + hdfsPath ;
		Configuration conf = new Configuration() ;
		FileSystem fs = FileSystem.get(URI.create(hdfsPath) , conf) ;
		Path path = new Path(hdfsPath) ;
		boolean isDeleted = fs.delete(path , true) ;
		fs.close() ;
		return true ;
	}
	
	/**
	 * 获取HDFS集群列表信息
	 * @return
	 * @throws IOException
	 */
	public DatanodeInfo[] getDataNodeList() throws IOException {
		Configuration conf=new Configuration();
		FileSystem fs=FileSystem.get(URI.create(hdfsUri) ,conf);
		DistributedFileSystem hdfs = (DistributedFileSystem)fs;
		DatanodeInfo[] dataNodeStats = hdfs.getDataNodeStats();
		return dataNodeStats ;
	}
	
	/*
	public static void main(String[] args) throws IOException {
		// TODO 自动生成的方法存根
		
		// 创建HDFS目录
		HdfsTools ht = new HdfsTools() ;
		boolean mkdir_res = ht.mkdir("/star/xing/cheng/") ;
		System.out.println(mkdir_res);
		
		// 删除HDFS目录
		boolean deleteDir_res = ht.deleteDir("/star/xing/cheng/") ;
		
		// 列出目录下的文件及文件夹
		List<String> list_res = ht.listAll("/star/") ;
		for(int i = 0 ; i < list_res.size() ; i ++) {
			System.out.println(list_res.get(i));
		}
		
		// 上传本地文件到HDFS
		ht.uploadFile2Hdfs("E:\\MySQL存储过程.pdf", "/star/xing/") ;
		ht.uploadFile2Hdfs("F:\\视频教程\\javascript视频\\1.1.mov", "/star/xing/") ;
		ht.uploadFile2Hdfs("E:\\MySQL存储过程.pdf", "/star/xing/") ;
		
		// 下载文件夹中的内容
		System.out.println("download file");
		boolean download_res = ht.downloadFileFromHdfs("E:\\star\\", "/star/") ;
		System.out.println(download_res);
		
		// 下载文件
		System.out.println("download file");
		boolean download_res_1 = ht.downloadFileFromHdfs("E:\\star\\", "/star/xing/MySQL存储过程.pdf") ;
		System.out.println(download_res_1);
		
		ht.deleteCRCFile("E:\\star\\") ;
		
		ht.deleteHdfsFile("/star/xing/MySQL存储过程.pdf") ;
		ht.deleteHdfsFile("/star/xing/") ;
	}
	*/
}
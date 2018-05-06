package com.test.dao.file_dir;

import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.JOptionPane;
import com.test.db.DBManage;
import com.test.tools.Tools;

public class FileDownload implements Runnable {
	private String fileBlockPath;
	private String savePath;
	private String filename;
	private int fileid;
	private File fileMkdir ;
	//解密秘钥
	private String fileblockkey = " ";
	//临时文件ID
	public int tempFileId;
	//文件大小
	public int fileTotalSize;
	// 进行文件操作
	private FileManager fileManager ;	
	// 数据库操作
	private DBManage dbmanage ;
	@SuppressWarnings("unused")
	private Connection connection ;
	private PreparedStatement preparedStatement ;
	private ResultSet resultSet ;
	
	private HdfsTools hdfsTool ;
	
	
	public FileDownload(int fileId,String filename ,String savePath) {
		fileid = fileId;
		this.savePath = savePath;
		this.filename = filename;
		this.dbmanage = new DBManage() ;
		this.fileManager = new FileManager(this.dbmanage) ;
		this.hdfsTool = new HdfsTools() ;
	}
	// 文件下载
	@SuppressWarnings("unchecked")
	public boolean download(String savePath) throws Exception {
		//设置任务状态为“下载中”
		this.fileManager.updateTempDownloadFileState(tempFileId,"下载中") ;
		
		fileMkdir = new File(savePath) ;
		fileMkdir.mkdirs();
		
		//获取key ！！！！
		String showFileBlockKey = "select * from fileblock where f_id = ?" ;
    	connection = this.dbmanage.getConnection() ;
//        	preparedStatement = this.dbmanage.getPreparedStatement(Tools.SHOW_FILE_BLOCK_KEY) ;
    	preparedStatement = this.dbmanage.getPreparedStatement(showFileBlockKey) ;
		preparedStatement.setInt(1, fileid) ;
		resultSet = preparedStatement.executeQuery() ;
		
		while(resultSet.next()) {
			String filename = resultSet.getString("b_name") ;
			String covername = resultSet.getString("b_covername") ;
			fileblockkey = Tools.loginSuccess.hdfs.getFileBlockKey(fileid);
			System.out.println("download file >>>>>> " + savePath + "/" + covername + " | " + "/" + filename);
			hdfsTool.downloadFile(savePath + "/" + covername, "/" + filename) ;
			hdfsTool.deleteCRCFile(savePath) ;
		}
		return true;
	}
	
	//临时保存路径
	public void getSavepath(){
		StringTokenizer st = new StringTokenizer(filename , ".");
		fileBlockPath = savePath + st.nextToken();
	}

	@Override
	public void run() {
		//临时文件表插入下载任务记录,设置任务状态为“准备开始”
		int parentId = this.fileManager.saveTempDownloadFile(fileid) ;
		// 计算得到上传的文件ID号
		tempFileId = -1;
		tempFileId = this.fileManager.queryDownloadFileIdByTempFileName(filename,parentId) ;
		
		getSavepath();
		try {
			if(download(fileBlockPath)){
				//设置任务状态为“解密中”
        		this.fileManager.updateTempDownloadFileState(tempFileId,"解密中") ;
				new FileBlockDecrypt(fileBlockPath , fileblockkey) ;
				//设置任务状态为“合并中”
        		this.fileManager.updateTempDownloadFileState(tempFileId,"合并中") ;
				//合并
			    new FileCombination(fileBlockPath+"\\decrypt\\" , this.fileid) ;
				/**
				 * 文件下载完成后删除文件块
				 */
				//设置任务状态为“完成”
        		this.fileManager.updateTempDownloadFileState(tempFileId,"完成") ;
				this.fileManager.deleteDirectory(fileBlockPath) ;	
				JOptionPane.showMessageDialog(null, "文件下载成功！！", "信息提示", 1);
				
			}else {
				System.out.println("文件下载任务建立失败");
				this.fileManager.updateTempDownloadFileState(tempFileId,"失败") ;
				JOptionPane.showMessageDialog(null, "文件下载任务建立失败，请重试！", "信息提示", 1);
			}
		}catch(ConnectException e) {
			this.fileManager.updateTempDownloadFileState(tempFileId,"失败") ;
			JOptionPane.showMessageDialog(null, "HDFS集群连接异常！！！请检查HDFS状态", "信息提示", 1);
			e.printStackTrace() ;
		}catch(Exception e ) {
			this.fileManager.updateTempDownloadFileState(tempFileId,"失败") ;
			e.printStackTrace() ;
		}
	}
}

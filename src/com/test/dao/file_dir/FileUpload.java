package com.test.dao.file_dir;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;

import com.test.db.DBManage;
import com.test.tools.Tools;


public class FileUpload extends HdfsTools implements Runnable{
	private File file;
	private String filePath;
	private String savePath ;
	private String filename;
	public int tempfileId;
	public Map<Integer , String > blockMap ;
	//临时工作文件夹名
	private String filefoldername;
	//加密秘钥，存入临时文件块表
	private String secretString;
	//存放文件块真实文件名
	private String[] covernamelist;
	// 用于文件管理
	public FileManager fileManager ;
	// 文件大小
	public long fileSize = 0 ;
	
	private int fileID = -1 ;
	
	// 数据库操作
	private DBManage dbmanage ;
	private Connection connection ;
	private PreparedStatement preparedStatement ;
	private ResultSet resultSet ;

	public FileUpload(String filePath , String savePath){
		file = new File(filePath);
		this.filePath = filePath;
		this.savePath = savePath ;
		this.dbmanage = new DBManage() ;
		this.fileManager = new FileManager(this.dbmanage) ;
		this.fileSize = file.length() ;
		init();
	}
	public FileUpload() {
		this.dbmanage = new DBManage() ;
		this.fileManager = new FileManager(this.dbmanage) ;
	}
	private void init(){
		filename = file.getName();
		System.out.println(filename);
		StringTokenizer st = new StringTokenizer(filename , ".");
		filefoldername = st.nextToken();
	}
	
	/**
	 * 文件块上传
	 * @param fileBlockPath 文件本地存放地址
	 * @param dstPath 远程HDFS地址
	 * @return
	 * @throws IOException
	 */
	public boolean upload(String fileBlockPath , String dstPath) throws IOException {
		//更新数据库临时文件表状态为“上传中”
    	this.fileManager.updateTempUploadFileState(tempfileId,"上传中") ; 
    	// 获取加密目录下的所有加密文件块，并逐个上传
    	File file = new File(fileBlockPath) ;
    	File[] fileList = file.listFiles() ;
    	for(int i = 0 ; i < fileList.length ; i ++) {
    		this.uploadFile2Hdfs(fileList[i].getAbsolutePath(), dstPath) ;
    	}
    	this.fileManager.updateTempUploadFileState(tempfileId,"完成") ;
    	// 文件上传成功
    	return true ;
	}
	
	public boolean saveFileBlockInfo(FileBlockEncrypt fileBlockEncrypt ,String fileBlockPath , String secretKey) {
		File file = new File(fileBlockPath) ;
		File[] fileList = file.listFiles() ;
		
		try {
			String queryTempFileUploadFileInfo = "select * from temp_upload_file where tf_id = ?" ;
        	connection = this.dbmanage.getConnection() ;
//        	preparedStatement = this.dbmanage.getPreparedStatement(Tools.QUERY_TEMP_FILE_UPLOAD_FILE_INFO) ;
        	preparedStatement = this.dbmanage.getPreparedStatement(queryTempFileUploadFileInfo) ;
			preparedStatement.setInt(1, tempfileId) ;
			resultSet = preparedStatement.executeQuery() ;
			while(resultSet.next()) {
				// 取得文件块信息
				String fileName = resultSet.getString("tf_name") ;
				String filedate = resultSet.getString("tf_date") ;
				int parentid = resultSet.getInt("parent_id") ;
				this.fileManager.saveFile(fileName, filedate,this.fileSize , parentid) ;
				// 计算得到上传的文件ID号
				fileID = this.fileManager.queryFileIdByFileName(fileName,parentid) ;
			}
			
			String saveFileBlockInfo = "insert into fileblock(f_id ,b_name , b_covername , f_key) values(?,?,?,?)" ;
			connection = this.dbmanage.getConnection() ;
//        	preparedStatement = this.dbmanage.getPreparedStatement(Tools.SAVE_FILE_BLICK_INFO) ;
        	preparedStatement = this.dbmanage.getPreparedStatement(saveFileBlockInfo) ;
        	
        	String[] coverNameList = fileBlockEncrypt.getCovernamelist() ;
        	String[] fileNameList = fileBlockEncrypt.getRondomnamelist() ;
        	for(int i = 0 ; i < coverNameList.length ; i ++) {
        		String filename = fileList[i].getName() ;
    			preparedStatement.setInt(1, fileID) ;
    			preparedStatement.setString(2, fileNameList[i]) ;
    			preparedStatement.setString(3, coverNameList[i]) ;
    			preparedStatement.setString(4, secretKey) ;
    			preparedStatement.execute() ;
        	}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
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
			this.dbmanage.closeLink() ;
		}
		return true ;
	}
	
	/**
	 * 文件上传成功后进行后续处理
	 * @return
	 */
	public boolean afterUploadSuccess(String blockUploadPath , long fileSize) {
		/**
		 * 文件上传完成后删除文件块
		 */
		this.fileManager.deleteDirectory(blockUploadPath) ;		

		try {
			String queryTempFileUploadFileInfo = "select * from temp_upload_file where tf_id = ?" ;
        	connection = this.dbmanage.getConnection() ;
//        	preparedStatement = this.dbmanage.getPreparedStatement(Tools.QUERY_TEMP_FILE_UPLOAD_FILE_INFO) ;
        	preparedStatement = this.dbmanage.getPreparedStatement(queryTempFileUploadFileInfo) ;
			preparedStatement.setInt(1, tempfileId) ;
			resultSet = preparedStatement.executeQuery() ;
			while(resultSet.next()) {
				// 取得文件块信息
				String fileName = resultSet.getString("tf_name") ;
				String filedate = resultSet.getString("tf_date") ;
				int parentid = resultSet.getInt("parent_id") ;
//				// 建立文件记录
//				this.fileManager.saveFile(fileName, filedate,this.fileSize , parentid) ;
//				// 计算得到上传的文件ID号
//				fileID = this.fileManager.queryFileIdByFileName(fileName,parentid) ;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
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
			this.dbmanage.closeLink() ;
		}
		// 更新数据库中文件夹的大小
		this.fileManager.updateFileSize(fileID , fileSize) ;
		
		//文件上传完成后，刷新界面上的文件列表
		Tools.loginSuccess.fileBottomPanel.removeAll() ;
		this.fileManager.showFileList(Tools.parentIdStack.lastElement(), Tools.userId) ;
		Tools.loginSuccess.repaint() ;
		return true ;
	}
	
	@Override
	public void run() {
		if(this.fileManager.checkFileName(filename)) {
			// 获取待上传文件的parent_id
			int parentid = Tools.parentIdStack.lastElement();	
			
			// 获取待上传文件的HDFS目录
			String hdfsPath = "/" ;
			try {
				for(int i = 0 ; i < Tools.parentIdStack.size() ; i ++) {
					int fileId = Tools.parentIdStack.get(i) ;
					
					String getFileNameById = "select f_name from file where f_id = ?" ;
					this.connection = this.dbmanage.getConnection() ;
//					this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.GET_FILE_NAME_BY_ID) ;
					this.preparedStatement = this.dbmanage.getPreparedStatement(getFileNameById) ;
					this.preparedStatement.setInt(1, fileId) ;
					this.resultSet = this.preparedStatement.executeQuery() ;
					while(this.resultSet.next()) {
						hdfsPath = hdfsPath + this.resultSet.getString("f_name") + "/";
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
			}finally {
				// 关闭连接
				if(this.resultSet != null) {
					try {
						this.resultSet.close() ;
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				this.dbmanage.closeLink() ;
			}
			System.out.println("文件对应的HDFS目录 -> " + hdfsPath);

			// 加密后的文件块存放地址（文件父目录地址 + \\test\\encrypt）
			String encryptPath = this.savePath + "\\"+filefoldername ;			
			// 获取文件上传日期
			Date filedate = new Date() ;
			if(!(new File(savePath + "\\" + filefoldername).exists())) {
				// 临时表中创建文件上传记录
				this.fileManager.saveTempUploadFile(filename, filedate.toString(),parentid) ;
				// 计算得到上传的文件ID号
				tempfileId = -1;
				tempfileId = this.fileManager.queryUploadFileIdByTempFileName(filename,parentid) ;			
				this.fileManager.fileSeperate = new FileSeperate(filePath,filefoldername ,tempfileId,savePath) ;
				if(this.fileManager.fileSeperate.seperateFile()) {
					// 加密字符串
					secretString = this.fileManager.generateSecretString() ;
					// 对分割后的文件块进行加密
					FileBlockEncrypt fileBlockEncrypt = new FileBlockEncrypt(encryptPath , secretString ,tempfileId) ;				
					if(fileBlockEncrypt.crateEncryptFile()) {
						System.out.println("文件块加密成功");
						// 文件上传
						try {
							if(!upload(encryptPath + "\\encrypt\\" , "/")) {
								JOptionPane.showMessageDialog(null, "创建文件上传任务失败，请重试！", "信息提示", 1);
							}else {
								saveFileBlockInfo(fileBlockEncrypt ,encryptPath + "\\encrypt\\" , secretString) ;
								afterUploadSuccess(encryptPath , this.fileSize) ;
								// 文件上传成功
								JOptionPane.showMessageDialog(null, "创建文件上传成功！", "信息提示", 1);
							}
						} catch (Exception e) {
							this.fileManager.updateTempUploadFileState(tempfileId,"失败") ;
							JOptionPane.showMessageDialog(null, "网络异常！！！请检查网络连接。", "信息提示", 1);
							e.printStackTrace() ;
						}
					}
				}
			}else {
				JOptionPane.showMessageDialog(null, "该文件正在上传中", "上传提示", 1);
			}
		}else {
			System.out.println("文件重名");
			JOptionPane.showMessageDialog(null, "该文件已存在", "信息提示", 1);
		}
	}
}



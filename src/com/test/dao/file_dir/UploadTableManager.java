package com.test.dao.file_dir;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.test.db.DBManage;
import com.test.tools.Tools;

public class UploadTableManager  {
	// 数据库操作
	private DBManage dbmanage ;
	private Connection connection ;
	private PreparedStatement preparedStatement ;
	private ResultSet resultSet ;
	
	public FileManager fileManager ;
	private HdfsTools hdfsTool ;
	
	public UploadTableManager() {
		this.dbmanage = new DBManage() ;
		this.fileManager = new FileManager() ;
		this.hdfsTool = new HdfsTools() ;
	}
	public boolean reupload(String filename) {
		int tempFileId = -1;
		try {
			String queryReuploadFileId = "select tf_id,tf_state from temp_upload_file where u_id = ? and tf_name = ? " ;
			
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.connection.prepareStatement(Tools.QUERY_REUPLOAD_FILE_ID) ;
			this.preparedStatement = this.connection.prepareStatement(queryReuploadFileId) ;
			this.preparedStatement.setInt(1, Tools.userId) ;
			this.preparedStatement.setString(2, filename) ;
			this.resultSet = this.preparedStatement.executeQuery() ;
			if(this.resultSet.next()) {
				tempFileId = this.resultSet.getInt("tf_id") ;
				String uploadState = this.resultSet.getString("tf_state") ;
		    	//更新数据库临时文件表状态为“上传中”
		    	this.fileManager.updateTempUploadFileState(tempFileId,"上传中") ;
				if(uploadState.equals("失败")) {
					// 删除原有记录，加入新的块上传记录
					System.out.println("失败重传，功能未实现");
				}else {
					System.out.println("该文件已上传成功或正在上传中，不需要进行重传");
				}
			}
		}catch(Exception e) {
			e.printStackTrace() ;
			return false ;
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
			
		return true ;
	}
	/**
	 * 删除上传任务
	 * @param filename 
	 * @return
	 */
	public boolean deleteUploadAssignment(String filename) {
		try {
			// 删除HDFS文件
			String deleteHdfsErrorFile = "select b_name from " +
			"(select f_id from " +
			"(select tf_name , parent_id " +
			"from temp_upload_file " +
			"where u_id = ? and tf_name = ? and tf_state = ?) a , file " +
			"where a.tf_name = file.f_name and a.parent_id = file.parent_id ) b ,fileblock " +
			"where b.f_id = fileblock.f_id" ;
			this.connection = this.dbmanage.getConnection() ;
			this.preparedStatement = this.connection.prepareStatement(deleteHdfsErrorFile) ;
			this.preparedStatement.setInt(1, Tools.userId) ;
			this.preparedStatement.setString(2, filename) ;
			this.preparedStatement.setString(3, "失败") ;
			this.resultSet = this.preparedStatement.executeQuery() ;
			while(this.resultSet.next()) {
				String blockName = this.resultSet.getString("b_name") ;
				System.out.println("删除文件 " + blockName);
				this.hdfsTool.deleteHdfsFile("/" + blockName) ;
			}
			// 删除数据库记录
			String deleteUploadAssignment = "delete from temp_upload_file where u_id = ? and tf_name = ?" ;
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.connection.prepareStatement(Tools.DELETE_UPLOAD_ASSIGNMENT) ;
			this.preparedStatement = this.connection.prepareStatement(deleteUploadAssignment) ;
			this.preparedStatement.setInt(1, Tools.userId) ;
			this.preparedStatement.setString(2, filename) ;
			this.preparedStatement.execute() ;
			
			// 获取文件上传失败后需要删除的临时HDFS文件
//			String getFilenameAndParentId = "select tf_name , parent_id from temp_upload_file where u_id = ? and tf_name = ?" ;
//			String getFileId = "select f_id from file where f_name = ? and parent_id = ?" ;
//			String getHdfsFileName = "select b_name from fileblock where f_id = ?" ;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false ;
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
		return true ;
	}
	
	/**
	 * 检查上传列表中是否有重名的任务
	 */
	public boolean checkUploadTable(String filename) {
		try {
			String checkUploadTable = "select tf_name from temp_upload_file where u_id = ?" ;
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.connection.prepareStatement(Tools.CKECK_UPLOAD_TABLE) ;
			this.preparedStatement = this.connection.prepareStatement(checkUploadTable) ;
			this.preparedStatement.setInt(1, Tools.userId) ;
			this.resultSet = this.preparedStatement.executeQuery() ;
			while(this.resultSet.next()) {
				String tempFileName = this.resultSet.getString("tf_name") ;
				if(tempFileName.equals(filename)) {
					return false ;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false ;
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
		return true ;
	}
	
	/**
	 * 在重传前删除原有块的记录
	 */
//	public boolean deleteBlockRecord(int blockId) {
//		try {
//			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.connection.prepareStatement(Tools.DELETE_BLOCK_UPLOAD_RECORD) ;
//			this.preparedStatement.setInt(1, blockId) ;
//			this.preparedStatement.execute() ;
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false ;
//		}finally {
//			// 关闭连接
//			this.dbmanage.closeLink() ;
//		}
//		return true ;
//	}

}

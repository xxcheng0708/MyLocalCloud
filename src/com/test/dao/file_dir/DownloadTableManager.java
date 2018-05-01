package com.test.dao.file_dir;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.test.db.DBManage;
import com.test.tools.Tools;

public class DownloadTableManager {
	
	// 数据库操作
	public DBManage dbmanage ;
	public Connection connection ;
	public PreparedStatement preparedStatement ;
	public ResultSet resultSet ;
	
	public FileManager fileManager ;
	
	public DownloadTableManager() {
		this.dbmanage = new DBManage() ;
		this.fileManager = new FileManager() ;
	}
	
	public boolean deleteDownloadAssignment(String filename) {
		try {
			// 删除下载任务
			String deleteDownloadAssignment = "delete from temp_download_file where u_id = ? and tf_name = ?" ;
			this.connection = this.dbmanage.getConnection() ;
			this.preparedStatement = this.connection.prepareStatement(deleteDownloadAssignment) ;
			this.preparedStatement.setInt(1, Tools.userId) ;
			this.preparedStatement.setString(2, filename) ;
			this.preparedStatement.execute() ;
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
	 * 检查下载列表中是否有重名的任务
	 */
	public boolean checkDownloadTable(String filename) {
		try {
			// 检查待上传文件是否在传输列表中
			String checkDownLoadTable = "select tf_name from temp_download_file where u_id = ?" ;
			this.connection = this.dbmanage.getConnection() ;
			this.preparedStatement = this.connection.prepareStatement(checkDownLoadTable) ;
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

}

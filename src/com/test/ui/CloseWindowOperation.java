package com.test.ui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ImageIcon;

import com.test.db.DBManage;
import com.test.tools.Tools;

public class CloseWindowOperation {
	// 数据库操作
	public DBManage dbmanage ;
	public Connection connection ;
	public PreparedStatement preparedStatement ;
	public ResultSet resultSet ;
	
	public CloseWindowOperation() {
		this.dbmanage = new DBManage() ;
		windowCloseUploadFileOperation() ;
		windowCloseDownloadFileOperation() ;
	}
	
	/**
	 * 关闭窗口时正在进行上传的文件进行的操作
	 */
	public void windowCloseUploadFileOperation() {
		try {
			String closeUploadFile = "select tf_id from temp_upload_file where u_id = ? and tf_state = ?" ;
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.CLOSE_UPLOAD_FILE) ;
			this.preparedStatement = this.dbmanage.getPreparedStatement(closeUploadFile) ;
			this.preparedStatement.setInt(1, Tools.userId) ;
			this.preparedStatement.setString(2, "上传中") ;
			this.resultSet = this.preparedStatement.executeQuery() ;
			while(this.resultSet.next()) {
				// 取得文件名
				int tempFileId = this.resultSet.getInt("tf_id") ;
				// 设置文件款为上传为失败
				String updateUploadTempFileBlockState = "update temp_upload_BLOCK set tb_state = ? where tf_id = ? and tb_state = ?" ;
//				this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.UPDATE_UPLOAD_TEMP_FILE_BLOCK_STATE) ;
				this.preparedStatement = this.dbmanage.getPreparedStatement(updateUploadTempFileBlockState) ;
				this.preparedStatement.setString(1, "上传失败") ;
				this.preparedStatement.setInt(2,tempFileId) ;
				this.preparedStatement.setString(3, "上传中") ;
				this.preparedStatement.execute() ;
				
				// 尚志文件为上传失败
				String updateUploadFileState = "update temp_upload_file set tf_state = ? where tf_id = ?" ;
//				this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.UPDATE_UPLOAD_FILE_STATE) ;
				this.preparedStatement = this.dbmanage.getPreparedStatement(updateUploadFileState) ;
				this.preparedStatement.setString(1, "上传失败") ;
				this.preparedStatement.setInt(2,tempFileId) ;
				this.preparedStatement.execute() ;
			}
		}catch(Exception e) {
			e.printStackTrace() ;
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
	}
	
	/**
	 * 关闭窗口时正在进行下载的文件进行的操作
	 */
	public void windowCloseDownloadFileOperation() {
		try {
			String closeDownloadFile = "select tf_id from temp_download_file where u_id = ? and tf_state = ?" ;
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.CLOSE_DOWNLOAD_FILE) ;
			this.preparedStatement = this.dbmanage.getPreparedStatement(closeDownloadFile) ;
			this.preparedStatement.setInt(1, Tools.userId) ;
			this.preparedStatement.setString(2, "下载中") ;
			this.resultSet = this.preparedStatement.executeQuery() ;
			while(this.resultSet.next()) {
				// 取得文件名
				int tempFileId = this.resultSet.getInt("tf_id") ;
				
				//设置文件块为下载失败
//				this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.UPDATE_DOWNLOAD_TEMP_FILE_BLOCK_STATE) ;
//				this.preparedStatement.setString(1, "下载失败") ;
//				this.preparedStatement.setInt(2,tempFileId) ;
//				this.preparedStatement.setString(3, "下载中") ;
//				this.preparedStatement.execute() ;
				
				// 设置文件为下载失败
				String updateDownloadFileState = "update temp_download_file set tf_state = ? where tf_id = ?" ;
//				this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.UPDATE_DOWNLOAD_FILE_STATE) ;
				this.preparedStatement = this.dbmanage.getPreparedStatement(updateDownloadFileState) ;
				this.preparedStatement.setString(1, "下载失败") ;
				this.preparedStatement.setInt(2,tempFileId) ;
				this.preparedStatement.execute() ;
			}
		}catch(Exception e) {
			e.printStackTrace() ;
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
	}
}

package com.test.thread;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.test.db.DBManage;
import com.test.tools.Tools;
import com.test.ui.LoginSuccess;

public class UploadListTableThread implements Runnable{
	private LoginSuccess loginSuccess ;
	// 数据库操作
	private DBManage dbmanage ;
	@SuppressWarnings("unused")
	private Connection connection ;
	private PreparedStatement preparedStatement ;
	private ResultSet resultSet ;
	
	public UploadListTableThread(LoginSuccess loginSuccess) {
		this.loginSuccess = loginSuccess ;
		this.dbmanage = new DBManage() ;
	}
	
	public int getUploadTableRows() {
		int rows = 0 ;
		try {
			// 查询列表行数
			String uploadTableRows = "select * from temp_upload_file where u_id = ?" ;
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.UPLOAD_TABLE_ROWS) ;
			this.preparedStatement = this.dbmanage.getPreparedStatement(uploadTableRows) ;
			this.preparedStatement.setInt(1, Tools.userId) ;
			this.resultSet = this.preparedStatement.executeQuery() ;
			while (this.resultSet.next()) {
				rows ++ ;
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
		return rows ;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			try {
				Thread.sleep(1000) ;
				int rows = getUploadTableRows() ;
				this.loginSuccess.uploadModel.setRowCount(rows) ;
				// 设置列表内容
				String uploadTableRows = "select * from temp_upload_file where u_id = ?" ;
				this.connection = this.dbmanage.getConnection() ;
//				this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.UPLOAD_TABLE_ROWS) ;
				this.preparedStatement = this.dbmanage.getPreparedStatement(uploadTableRows) ;
				this.preparedStatement.setInt(1, Tools.userId) ;
				this.resultSet = this.preparedStatement.executeQuery() ;
				
				int count = 0 ;
				while(this.resultSet.next()) {
					String tempFileName = this.resultSet.getString("tf_name") ;
					String tempFileState = this.resultSet.getString("tf_state") ;
					
					this.loginSuccess.uploadTable.setValueAt(tempFileName, count, 0) ;
					this.loginSuccess.uploadTable.setValueAt(tempFileState, count, 1) ;
					count ++ ;
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
}

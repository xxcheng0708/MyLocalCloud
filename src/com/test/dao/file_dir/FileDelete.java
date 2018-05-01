package com.test.dao.file_dir;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import com.test.db.DBManage;
import com.test.tools.Tools;

public class FileDelete implements Callable<Boolean>{
	// 数据库操作
	public DBManage dbmanage ;
	public Connection connection ;
	public PreparedStatement preparedStatement ;
	public ResultSet resultSet ;

	private String fileName ;
	private int parentId ;
	// 删除文件时用到的栈
	private Stack<Integer> deleteFileStack = new Stack<Integer>() ;
	
	private HdfsTools hdfsTool ;
	
	public FileDelete(String fileName , int parentId) {
		this.dbmanage = new DBManage() ;
		this.fileName = fileName ;
		this.parentId = parentId ;
		this.hdfsTool = new HdfsTools() ;
	}
	
	/**
	 * 删除文件或者文件夹
	 * filename是文件名
	 * parentId是被删除的文件的父节点ID
	 */
	public boolean delete(String filename , int parentId) {
		String filetype = "";
		int fileId = -1 ;
		/**
		 * 根据文件名和父ID查询文件爱你类型和文件ID号
		 */
		try {
			String queryFileType = "select f_id,f_type,f_size from file where u_id = ? and f_name = ? and parent_id = ?" ;
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.QUERY_FILE_TYPE) ;
			this.preparedStatement = this.dbmanage.getPreparedStatement(queryFileType) ;
			this.preparedStatement.setInt(1, Tools.userId) ;
			this.preparedStatement.setString(2, filename) ;
			this.preparedStatement.setInt(3, parentId) ;
			this.resultSet = this.preparedStatement.executeQuery() ;
			if(this.resultSet.next()) {
				filetype = this.resultSet.getString("f_type") ;
				fileId = this.resultSet.getInt("f_id") ;
			}
			// 是文件则直接删除，文件夹要继续处理
			if(!filetype.equals("") && filetype.equals("文件")) {
				// 创建删除线程，删除HDFS中的文件
				String getFileBlockNameInHdfs = "select b_name from fileblock where f_id = ?" ;
				this.connection = this.dbmanage.getConnection() ;
				this.preparedStatement = this.dbmanage.getPreparedStatement(getFileBlockNameInHdfs) ;
				this.preparedStatement.setInt(1, fileId) ;
				this.resultSet = this.preparedStatement.executeQuery() ;
				while(this.resultSet.next()) {
					String fileBlockName = this.resultSet.getString("b_name") ;
					this.hdfsTool.deleteHdfsFile("/" + fileBlockName) ;
					
					// 删除数据库文件记录
					String deleteFileRecord = "delete from file where f_id = ?" ;
					this.preparedStatement = this.dbmanage.getPreparedStatement(deleteFileRecord) ;
					this.preparedStatement.setInt(1, fileId) ;
					this.preparedStatement.execute() ;
				}
			}else {
				// 要删除的是文件夹，继续做后续处理
				if(fileId != -1) {
					deleteFileStack.add(fileId) ;
					querySubFile() ;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false ;
		} finally {
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
	 * 查询一个文件的包含的所有的子文件夹和文件
	 */
	public void querySubFile() {
		ResultSet rs = null ;
		int parentId = deleteFileStack.lastElement() ;
		try {
			String querySubFile = "select f_id,f_type,f_name from file where u_id = ? and parent_id = ?" ;
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.QUERY_SUB_FILE) ;
			this.preparedStatement = this.dbmanage.getPreparedStatement(querySubFile) ;
			this.preparedStatement.setInt(1, Tools.userId) ;
			this.preparedStatement.setInt(2, parentId) ;
			rs = this.preparedStatement.executeQuery() ;
			System.out.println("删除1");
			while(rs.next()) {
				System.out.println("删除2");
				String filetype = rs.getString("f_type") ;
				int fileId = rs.getInt("f_id") ;
				String filename = rs.getString("f_name") ;
				/**
				 * 子文件是文件夹就递归处理
				 */
				if(filetype.equals("文件夹")) {
					System.out.println("我是文件夹");
					deleteFileStack.add(fileId) ;
					querySubFile() ;
				}else {
					/**
					 * 子文件是文件就直接删除
					 */
					System.out.println("我是文件");
					delete(filename , deleteFileStack.lastElement()) ;
				}
			}
			System.out.println("删除3");
			// 关闭数据库连接
			if(rs != null) {
				rs.close() ;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
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
		try {
			int id = deleteFileStack.pop() ;
			this.resultSet = null ;
			String checkDeleteFile = "select * from file where parent_id = ?" ;
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.CHECK_DELETE_FILE) ;
			this.preparedStatement = this.dbmanage.getPreparedStatement(checkDeleteFile) ;
			this.preparedStatement.setInt(1, id) ;
			this.resultSet = this.preparedStatement.executeQuery() ;
			System.out.println(id + " 删除4 " + this.resultSet);
			if(!this.resultSet.next()) {
				System.out.println("删除5");
				String deleteFileById = "delete from file where f_id = ?" ;
				this.connection = this.dbmanage.getConnection() ;
//				this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.DELETE_FILE_BY_ID) ;
				this.preparedStatement = this.dbmanage.getPreparedStatement(deleteFileById) ;
				this.preparedStatement.setInt(1, id) ;
				this.preparedStatement.execute() ;
				System.out.println("递归结束删除文件夹");
				System.out.println("临时栈中的内容： " + deleteFileStack);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
	}


	@Override
	public Boolean call() {
		// TODO Auto-generated method stub
		return delete(fileName , parentId) ;
	}
	

}

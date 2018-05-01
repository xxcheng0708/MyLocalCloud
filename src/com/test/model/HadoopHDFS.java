package com.test.model;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.hadoop.hdfs.protocol.DatanodeInfo;

import com.test.dao.file_dir.HdfsTools;
import com.test.db.DBManage;
import com.test.tools.Tools;

public class HadoopHDFS {
	// 数据库操作
	private DBManage dbmanage ;
	private Connection connection ;
	private PreparedStatement preparedStatement ;
	private ResultSet resultSet ;
	private HdfsTools hdfsTool ;
	
	public HadoopHDFS() {
		this.dbmanage = new DBManage() ;
		this.hdfsTool = new HdfsTools() ;
	}
	
	/**
	 * 添加HDFS节点
	 * @param ip	HDFS节点IP地址
	 * @return
	 */
	public boolean addHdfsNode(String ip) {
		try {
			String addHdfsNode = "insert into hdfs (u_id ,hdfs_ip) values(?,?)" ;
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.ADD_HDFS_NODE) ;
			this.preparedStatement = this.dbmanage.getPreparedStatement(addHdfsNode) ;
			this.preparedStatement.setInt(1, Tools.userId) ;
			this.preparedStatement.setString(2, ip) ;
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
		return true;
	}
	
	/**
	 * 删除HDFS节点
	 * @return
	 */
	public boolean removeHdfsNode() {
		return false ;
	}
	
	/**
	 * 返回HDFS节点数量
	 * @return
	 */
	public int getHdfsNodes() {
		int count = 0;
		try {
			String getHdfsNodeCount = "select * from hdfs where u_id = ?" ;
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.HDFS_NODES) ;
			this.preparedStatement = this.dbmanage.getPreparedStatement(getHdfsNodeCount) ;
			this.preparedStatement.setInt(1 ,Tools.userId) ;
			this.resultSet = this.preparedStatement.executeQuery() ;
			while(this.resultSet.next()) {
				count ++ ;
				System.out.println(count);
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
		return count ;
	}
	
	/**
	 * 显示HDFS节点信息（IP）列表
	 * @return
	 * @throws IOException 
	 */
	public boolean showHdfsNodes() throws IOException {
		int count = 0;
//		try {
//			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.SHOW_HDFS_NODES) ;
//			this.preparedStatement.setInt(1, Tools.userId) ;
//			this.resultSet = this.preparedStatement.executeQuery() ;
//			while(this.resultSet.next()) {
//				String hdfsIP = this.resultSet.getString("hdfs_ip") ;
//				
//				Tools.loginSuccess.hdfsNodesTable.setValueAt(hdfsIP, count, 0) ;
//				count ++ ;
//				System.out.println(hdfsIP);
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false ;
//		} finally {
//			// 关闭连接
//			if(this.resultSet != null) {
//				try {
//					this.resultSet.close() ;
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			this.dbmanage.closeLink() ;
//		}
		
		DatanodeInfo[] dataNodeStats = hdfsTool.getDataNodeList() ;
		for(int i = 0 ; i < dataNodeStats.length ; i ++) {
			System.out.println("DataNode_"+i+"_Name:"+dataNodeStats[i].getHostName());
			System.out.println(dataNodeStats[i].getBlockPoolUsed());
			System.out.println(dataNodeStats[i].getBlockPoolUsedPercent());
			System.out.println(dataNodeStats[i].getCapacity() / (1024*1024));
			System.out.println(dataNodeStats[i].getDfsUsed() / (1024*1024));
			System.out.println(dataNodeStats[i].getDfsUsedPercent());
			String ipAddr = dataNodeStats[i].getIpAddr() ;
			String hostName = dataNodeStats[i].getHostName();
			String dfsUsed = (long)(dataNodeStats[i].getDfsUsed() / (1024*1024)) + " M" ;
			String dfsCapacity = (long)(dataNodeStats[i].getCapacity() / (1024*1024)) + " M" ;
			String dfsPercent = dataNodeStats[i].getDfsUsedPercent() + " %";
			String nodeType = "DataNode" ;
			Tools.loginSuccess.hdfsNodesTable.setValueAt(ipAddr, i, 0) ;
			Tools.loginSuccess.hdfsNodesTable.setValueAt(hostName, i, 1) ;
			Tools.loginSuccess.hdfsNodesTable.setValueAt(dfsUsed, i, 2) ;
			Tools.loginSuccess.hdfsNodesTable.setValueAt(dfsCapacity, i, 3) ;
			Tools.loginSuccess.hdfsNodesTable.setValueAt(dfsPercent, i, 4) ;
			Tools.loginSuccess.hdfsNodesTable.setValueAt(nodeType, i, 5) ;
		}
		return true ;
	}

/**
 * 获取文件块解密秘钥
 * @return
 * @throws Exception 
 */
public String getFileBlockKey(int fileid) {
	String fileblockkey = " ";
	try {
		String showFileBlockKey = "select * from fileblock where f_id = ?" ;
		this.connection = this.dbmanage.getConnection() ;
//		this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.SHOW_FILE_BLOCK_KEY) ;
		this.preparedStatement = this.dbmanage.getPreparedStatement(showFileBlockKey) ;
		this.preparedStatement.setInt(1, fileid) ;
		this.resultSet = this.preparedStatement.executeQuery() ;
		if(this.resultSet.next()){
			fileblockkey = this.resultSet.getString("f_key") ;
		}
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	finally {
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

	return fileblockkey;
}

/**
 * 获取文件块大小
 * @return
 * @throws Exception 
 */
public int getFileSize(int fileid)  {
	int filesize = 0;
	try {
		String showFileSize = "select * from file where f_id = ?" ;
		this.connection = this.dbmanage.getConnection() ;
//		this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.SHOW_FILE_SIZE) ;
		this.preparedStatement = this.dbmanage.getPreparedStatement(showFileSize) ;
		this.preparedStatement.setInt(1, fileid) ;
		this.resultSet = this.preparedStatement.executeQuery() ;
		if(this.resultSet.next()){
			filesize = this.resultSet.getInt("f_size") ;
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

	return filesize;
}

/**
 * 更新HDFS节点信息
 * @return
 * @throws IOException 
 * @throws Exception 
 */
public boolean refreshHdfsNodes() throws IOException {
//	int count = 0;
//	boolean refreshHdfsNodes = true ;
//	List<String> hdfsNodelist = new ArrayList<String>();
//
//	try {
//		this.connection = this.dbmanage.getConnection() ;
//		this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.SHOW_HDFS_NODES) ;
//		this.preparedStatement.setInt(1, Tools.userId) ;
//		this.resultSet = this.preparedStatement.executeQuery() ;
//		while(this.resultSet.next()) {
//			String hdfsIP = this.resultSet.getString("hdfs_ip") ;
//			System.out.println(hdfsIP);
//			hdfsNodelist.add(hdfsIP);
//			count ++ ;
//		}
//		System.out.println("count " + count);
//		
//		if(!refreshHdfsNodes) {
//			return refreshHdfsNodes ;
//		}
//	} catch (SQLException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//		return false ;
//	} catch (InterruptedException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//		return false ;
//	} catch (ExecutionException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//		return false ;
//	} catch (Exception e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//		return false ;
//	} finally {
//		// 关闭连接
//		if(this.resultSet != null) {
//			try {
//				this.resultSet.close() ;
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		this.dbmanage.closeLink() ;
//	}
	showHdfsNodes() ;
	return true;
	}
}
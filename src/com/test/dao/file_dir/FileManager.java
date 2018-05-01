package com.test.dao.file_dir;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import com.test.db.DBManage;
import com.test.tools.Tools;
import com.test.ui.FilePanel;
/**
 * 文件操作管理类
 * @author asus
 *
 */
public class FileManager {
	// 文件分割
	public FileSeperate fileSeperate ;
	// 文件合并
	public FileCombination fileCombination ;
	// 删除文件时用到的栈
	private Stack<Integer> deleteFileStack = new Stack<Integer>() ;
	
	// 数据库操作
	public DBManage dbmanage ;
	public Connection connection ;
	public PreparedStatement preparedStatement ;
	public ResultSet resultSet ;
	public HdfsTools hdfsTool ;

	public FileManager(DBManage dbmanage) {
		this.dbmanage = dbmanage ;
		this.hdfsTool = new HdfsTools() ;
	}
	public FileManager() {
		this.dbmanage = new DBManage() ;
		this.hdfsTool = new HdfsTools() ;
	}
	// 显示文件列表
	public void showFile(Icon icon , String fileName) {
		Tools.loginSuccess.filePanel = new FilePanel(icon , fileName) ;
		Tools.loginSuccess.fileBottomPanel.add(Tools.loginSuccess.filePanel) ;
		Tools.loginSuccess.repaint() ;
	}
	// 显示文件列表
	public void showFileList(int parentId , int userId) {
		try {
			String showFile = "select * from file where parent_id = ? and u_id = ? order by f_type DESC" ;
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.SHOW_FILE) ;
			this.preparedStatement = this.dbmanage.getPreparedStatement(showFile) ;
			this.preparedStatement.setInt(1, parentId) ;
			this.preparedStatement.setInt(2, userId) ;
			this.resultSet = this.preparedStatement.executeQuery() ;
			while(this.resultSet.next()) {
				// 取得文件名
				String fileName = this.resultSet.getString("f_name") ;
				String filetype = this.resultSet.getString("f_type") ;
				// 显示文件图标
				if(filetype.equals("文件")) {
					this.showFile(new ImageIcon("image/file.png") , fileName) ;
				}else if(filetype.equals("文件夹")) {
					this.showFile(new ImageIcon("image/dir.png") , fileName) ;
				}
			}
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
	// 创建文件夹
	public void createDir() {
		//随机产生1000以内的命名规则
		String filename  = "新建文件夹"+ (int)(Math.random()*1000);
		Date date = new Date();
		String type = "文件夹";
		long size = 0 ;
		//随机产生的文件名若重名，则继续随机产生，知道不重名为止
		while (!checkFileName(filename)) {
			filename  = "新建文件夹"+ (int)(Math.random()*1000);
		}
		try {
			String createDir = "insert into File (u_id , f_name , f_date , f_type , f_size , parent_id) values(? , ? , ? , ? , ? , ?)" ;
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.CREATE_DIR) ;
			this.preparedStatement = this.dbmanage.getPreparedStatement(createDir) ;
			this.preparedStatement.setInt(1, Tools.userId) ;
			this.preparedStatement.setString(2, filename) ;
			this.preparedStatement.setString(3, date.toString()) ;
			this.preparedStatement.setString(4, type) ;
			this.preparedStatement.setLong(5, size) ;
			this.preparedStatement.setInt(6, Tools.parentIdStack.lastElement()) ;
			this.preparedStatement.execute() ;
			// 新建的文件夹在界面上显示出来
			Tools.loginSuccess.filePanel = new FilePanel(new ImageIcon("image/dir.png") , filename) ;
			Tools.loginSuccess.fileBottomPanel.add(Tools.loginSuccess.filePanel) ;
			Tools.loginSuccess.repaint();
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
	}
	
	/**
	 * 检查文件是否重名,通过文件类型和文件名查询
	 */
	public boolean checkFileName(String filename) {
		try {
			String checkFileName = "select * from File where u_id = ? and f_name = ? and parent_id = ?" ;
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.CHECK_FILE_NAME) ;
			this.preparedStatement = this.dbmanage.getPreparedStatement(checkFileName) ;
			this.preparedStatement.setInt(1, Tools.userId) ;
			this.preparedStatement.setString(2, filename) ;
			this.preparedStatement.setInt(3, Tools.parentIdStack.lastElement()) ;
			this.resultSet = this.preparedStatement.executeQuery() ;
			while(this.resultSet.next()) {
				return false ;
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
	 * 检查文件是否重名,通过文件ID号查询
	 */
	public boolean checkPasteFileName() {
		try {
			String checkPasteFileName = "select * from File where f_name = (select f_name from File where f_id = ?) and u_id = ? and parent_id = ?" ;
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.CHECK_PASTE_FILE_NAME) ;
			this.preparedStatement = this.dbmanage.getPreparedStatement(checkPasteFileName) ;
			this.preparedStatement.setInt(1, Tools.cutFileId) ;
			this.preparedStatement.setInt(2, Tools.userId) ;
			this.preparedStatement.setInt(3, Tools.parentIdStack.lastElement()) ;
			this.resultSet = this.preparedStatement.executeQuery() ;
			if(this.resultSet.next()) {
				JOptionPane.showMessageDialog(null, "该文件夹已存在，不能粘贴", "警告信息", 1);
				return false ;
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
	 * 打开文件夹
	 */
	public boolean openDir(String filename) {
		try {
			String queryFileId = "select * from File where u_id = ? and f_name = ? and parent_id = ?" ;
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.QUERY_FILE_ID) ;
			this.preparedStatement = this.dbmanage.getPreparedStatement(queryFileId) ;
			this.preparedStatement.setInt(1, Tools.userId) ;
			this.preparedStatement.setString(2, filename) ;
			this.preparedStatement.setInt(3, Tools.parentIdStack.lastElement()) ;
			this.resultSet = this.preparedStatement.executeQuery() ;
			int fileId = -1;
			String filetype = null ;
			if(this.resultSet.next()) {
				fileId = this.resultSet.getInt("f_id") ;
				filetype = this.resultSet.getString("f_type") ;
			}
			// 双击打开文件夹
			if(filetype != null && filetype.equals("文件夹")) {
				Tools.parentIdStack.add(fileId) ;
				Tools.loginSuccess.fileBottomPanel.removeAll() ;
				this.showFileList(Tools.parentIdStack.lastElement(), Tools.userId) ;
				Tools.loginSuccess.fileBottomPanel.repaint() ;
				// 设置文件路径
				String context = Tools.loginSuccess.search.getText() ;
				Tools.loginSuccess.search.setText(context + "\\" + filename) ;
			}else if(filetype != null){
				// 双击文件进行下载
				int res = JOptionPane.showConfirmDialog(null, "确定下载该文件吗？\n(文件存放地址为：E:\\SafeCloudFiles)", "下载提示", JOptionPane.OK_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE);
				if(res == JOptionPane.OK_OPTION) {
					if(new DownloadTableManager().checkDownloadTable(filename)) {
						FileDownload filedownload = new FileDownload(fileId,filename,"E:\\SafeCloudFiles\\downloadtemp\\");
						new Thread(filedownload).start();
					}else {
    					JOptionPane.showMessageDialog(null , "该文件已在下载列表中！！！","下载提示" , 1);
					}
				}else {
					System.out.println("不下载");
				}
				return false ;
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
	 * 剪切文件夹
	 */
	public boolean cutFile(String filename) {
		try {
			String queryFileId = "select * from File where u_id = ? and f_name = ? and parent_id = ?" ;
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.QUERY_FILE_ID) ;
			this.preparedStatement = this.dbmanage.getPreparedStatement(queryFileId) ;
			this.preparedStatement.setInt(1, Tools.userId) ;
			this.preparedStatement.setString(2, filename) ;
			this.preparedStatement.setInt(3, Tools.parentIdStack.lastElement()) ;
			this.resultSet = this.preparedStatement.executeQuery() ;
			if(this.resultSet.next()) {
				Tools.cutFileId = this.resultSet.getInt("f_id") ;
				Tools.copyFileSize = this.resultSet.getLong("f_size") ;
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
	 * 粘贴文件夹
	 */
	public boolean pasteFile() {
		if(checkPasteFileName()) {
			try {
				// 剪切的文件存在
				if(Tools.cutFileId == -1 ) {
					System.out.println("粘贴的文件不存在");
					return false ;
				}else if(Tools.cutFileId == Tools.parentIdStack.lastElement() ){
					JOptionPane.showMessageDialog(null, "目标文件夹是原文件夹的子文件夹", "警告信息", 1);
					System.out.println("不能剪切粘贴自己");
					return false ;
				}else {
					String pasteFile = "update File set parent_id = ? where f_id = ?" ;
					this.connection = this.dbmanage.getConnection() ;
//					this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.PASTE_FILE) ;
					this.preparedStatement = this.dbmanage.getPreparedStatement(pasteFile) ;
					this.preparedStatement.setInt(1, Tools.parentIdStack.lastElement()) ;
					this.preparedStatement.setInt(2, Tools.cutFileId) ;
					this.preparedStatement.executeUpdate() ;	
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
		}
		return true ;
	}
	
	/**
	 * 文件重命名
	 */
	public boolean rename(String new_filename , String old_filename) {
		if(checkFileName(new_filename)) {
			try {
				String renameFile = "update file set f_name = ? where u_id = ? and parent_id = ? and f_name = ?" ;
				this.connection = this.dbmanage.getConnection() ;
//				this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.RENAME_FILE) ;
				this.preparedStatement = this.dbmanage.getPreparedStatement(renameFile) ;
				this.preparedStatement.setString(1, new_filename) ;
				this.preparedStatement.setInt(2, Tools.userId) ;
				this.preparedStatement.setInt(3, Tools.parentIdStack.lastElement()) ;
				this.preparedStatement.setString(4, old_filename) ;
				this.preparedStatement.executeUpdate() ;
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
		}else {
			JOptionPane.showMessageDialog(null, "该文件夹已存在", "警告信息", 1);
			return false ;
		}
		return true ;
	}
	
	/*
	 * 根据文件名搜索文件
	 * filename 文件名
	 * 通过文件名查询与之匹配的文件，并查处文件名，文件类型，文件父节点ID，并将父节点ID入栈
	 */
	public Map<String,String> searchFiles(String filename) {
		Map<String,String> files = new HashMap<String,String>() ;
		try {
			String searchFile = "select f_name,f_type,parent_id from file where u_id = ? and f_name = ?" ;
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.SEARCH_FILE) ;
			this.preparedStatement = this.dbmanage.getPreparedStatement(searchFile) ;
			this.preparedStatement.setInt(1, Tools.userId) ;
			this.preparedStatement.setString(2, filename) ;
			this.resultSet = this.preparedStatement.executeQuery() ;
			// 查到该文件，记录文件名，类型，父节点ID
			if(this.resultSet.next()) {
				String name = this.resultSet.getString("f_name") ;
				String type = this.resultSet.getString("f_type") ;
				int id = this.resultSet.getInt("parent_id") ;
				// 多次点击查询同一个文件时使用
				if(Tools.parentIdStack.lastElement() != id) {
					Tools.parentIdStack.add(id) ;
				}
				files.put(name, type) ;
			}else {
				// 查询失败是使用，用于返回上一级目录
				if(Tools.parentIdStack.lastElement() != -1) {
					Tools.parentIdStack.add(-1) ;
				}
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
		// 返回查询到的文件的名字、类型键值对
		return files ;
	}
	
	/**
	 * 通过文件名查询文件ID号
	 */
	public int queryFileIdByFileName(String filename ,int parentid) {
		int fileId = -1;
		try {
			String queryFileNameByFileId = "select f_id from File where u_id = ? and f_name = ? and parent_id = ?" ;
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.QUERY_FILE_ID_BY_FILE_NAME) ;
			this.preparedStatement = this.dbmanage.getPreparedStatement(queryFileNameByFileId) ;
			this.preparedStatement.setInt(1, Tools.userId) ;
			this.preparedStatement.setString(2, filename) ;
			this.preparedStatement.setInt(3, parentid) ;
//			Tools.preparedStatement.setInt(3, Tools.parentIdStack.lastElement()) ;
			this.resultSet = this.preparedStatement.executeQuery() ;
			if(this.resultSet.next()) {
				fileId = this.resultSet.getInt("f_id") ;
			}
		}catch(Exception ee) {
			ee.printStackTrace() ;
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
		return fileId ;
	}
	
	
	/** 
	  * 删除文件，可以是文件或文件夹 
	  *  
	  * @param fileName 
	  *            要删除的文件名 
	  * @return 删除成功返回true，否则返回false 
	  */  
	 public boolean delete(String fileName) {  
		 File file = new File(fileName);  
		 if (!file.exists()) {  
			 System.out.println("删除文件失败:" + fileName + "不存在！");  
			 return false;  
		 } else {  
			 if (file.isFile())  
				 return deleteFile(fileName);  
			 else  
				 return deleteDirectory(fileName);  
		}  
	 }  
	  
	 /** 
	  * 删除单个文件 
	  *  
	  * @param fileName 
	  *            要删除的文件的文件名 
	  * @return 单个文件删除成功返回true，否则返回false 
	  */  
	 public boolean deleteFile(String fileName) {  
		 File file = new File(fileName);  
		 // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除  
		 if (file.exists() && file.isFile()) {  
			 if (file.delete()) {  
				 System.out.println("删除单个文件" + fileName + "成功！");  
				 return true;  
			 } else {  
				 System.out.println("删除单个文件" + fileName + "失败！");  
				 return false;  
			 }  
		 } else {  
			 System.out.println("删除单个文件失败：" + fileName + "不存在！");  
			 return false;  
		 }  
	 }  
	  
	 /** 
	  * 删除目录及目录下的文件 
	  *  
	  * @param dir 
	  *            要删除的目录的文件路径 
	  * @return 目录删除成功返回true，否则返回false 
	  */  
	 public boolean deleteDirectory(String dir) {  
		 // 如果dir不以文件分隔符结尾，自动添加文件分隔符  
		 if (!dir.endsWith(File.separator))
			 dir = dir + File.separator;  
		 File dirFile = new File(dir);  
		 // 如果dir对应的文件不存在，或者不是一个目录，则退出  
		 if ((!dirFile.exists()) || (!dirFile.isDirectory())) {  
			 System.out.println("删除目录失败：" + dir + "不存在！");  
			 return false;  
		 }  
		 boolean flag = true;  
		 // 删除文件夹中的所有文件包括子目录  
		 File[] files = dirFile.listFiles();  
		 for (int i = 0; i < files.length; i++) {  
			 // 删除子文件  
			 if (files[i].isFile()) {  
				 flag = deleteFile(files[i].getAbsolutePath());  
				 if (!flag)  
					 break;  
			 }  
			 // 删除子目录  
			 else if (files[i].isDirectory()) {  
				 flag = deleteDirectory(files[i].getAbsolutePath());  
				 if (!flag)  
					 break;  
			 }  
		 }  
		 if (!flag) {  
			 System.out.println("删除目录失败！");  
			 return false;  
		 }  
		 // 删除当前目录  
		 if (dirFile.delete()) {  
			 System.out.println("删除目录" + dir + "成功！");  
			 return true;  
		 } else {  
			 return false;  
		 }  
	 }
	
	
	
	
	/**
	 * 随机产生加密用的字符串
	 */
	 public String generateSecretString(){
			String str = "" ;
			String base = "abcdefghijklmnopqrstuvwxyzABCDEGHIJKLMNOPQRSTUVWSYZ0123456789"; 

		   SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
		   String currentDate = df.format(new Date());// new Date()为获取当前系统时间
			
			System.out.println(currentDate);
			Random random = new Random(); 
			
			int fileNameLength = random.nextInt(base.length()) + 1;
			System.out.println(fileNameLength);
			while(fileNameLength < 20) {
				fileNameLength = random.nextInt(base.length()) + 1;
				System.out.println(fileNameLength);
			}
			
			StringBuffer sb = new StringBuffer(); 
		    for (int i = 0; i < fileNameLength; i++) {
		        int number = random.nextInt(base.length());
		        sb.append(base.charAt(number));
		        }
		    str = sb.toString() + currentDate; 
		    return str ;
		}
	
	/*
	 * 存储文件记录
	 */
	public boolean saveFile(String filename ,String filedate , long filesize , int parentid) {
		try {
			String saveFile = "insert into file (u_id,f_name,f_date,f_type,f_size,parent_id) values(?,?,?,?,?,?)" ;
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.SAVE_FILE) ;
			this.preparedStatement = this.dbmanage.getPreparedStatement(saveFile) ;
			this.preparedStatement.setInt(1, Tools.userId) ;
			this.preparedStatement.setString(2, filename) ;
			this.preparedStatement.setString(3, filedate) ;
			this.preparedStatement.setString(4, "文件") ;
			this.preparedStatement.setLong(5, filesize) ;
			this.preparedStatement.setInt(6, parentid) ;
//			Tools.preparedStatement.setInt(6, Tools.parentIdStack.lastElement()) ;
			this.preparedStatement.execute() ;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// 关闭数据库连接
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
	
	/*
	 * 存储文件块记录
	 */
	public boolean saveFileBlock(int fileId ,String fileBlockName ,String covername,int netDiskId , String secretKey) {
		try {
			String saveFileBlock = "insert into fileblock (f_id,b_name,b_covername,netdisk_id,f_key) values(?,?,?,?,?)" ;
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.SAVE_FILE_BLOCK) ;
			this.preparedStatement = this.dbmanage.getPreparedStatement(saveFileBlock) ;
			this.preparedStatement.setInt(1, fileId) ;
			this.preparedStatement.setString(2, fileBlockName) ;
			this.preparedStatement.setString(3, covername) ;
			this.preparedStatement.setInt(4, netDiskId) ;
			this.preparedStatement.setString(5, secretKey) ;
			this.preparedStatement.execute() ;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// 关闭数据库连接
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
	 * 查看文件属性
	 */
	public String getProperty(String filename) {
		String file_date = "" ;
		String file_type = "" ;
		long file_size = 0 ;
		try {
			String getFileProperty = "select * from file where u_id = ? and f_name = ? and parent_id = ?" ;
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.GET_FILE_PROPERTY) ;
			this.preparedStatement = this.dbmanage.getPreparedStatement(getFileProperty) ;
			this.preparedStatement.setInt(1, Tools.userId) ;
			this.preparedStatement.setString(2, filename) ;
			this.preparedStatement.setInt(3, Tools.parentIdStack.lastElement()) ;
			this.resultSet = this.preparedStatement.executeQuery() ;
			
			if(this.resultSet.next()) {
				file_date = this.resultSet.getString("f_date") ;
				file_type = this.resultSet.getString("f_type") ;
				file_size = this.resultSet.getLong("f_size") ; 
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "" ;
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
		return "文件名：" + filename + "\n创建日期：" + file_date + "\n文件类型：" + file_type + "\n大小：" + (int)file_size/(1024*1024) + "M" ;
	}
	
	/**
	 * 通过文件名查询临时上传文件ID号
	 */
	public int queryUploadFileIdByTempFileName(String filename ,int parentid) {
		int fileId = -1;
		try {
			String queryUploadFileIdByTempFileName = "select tf_id from temp_upload_file where u_id = ? and tf_name = ? and parent_id = ?" ;
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.QUERY_UPLOAD_FILE_ID_BY_TEMP_FILE_NAME) ;
			this.preparedStatement = this.dbmanage.getPreparedStatement(queryUploadFileIdByTempFileName) ;
			this.preparedStatement.setInt(1, Tools.userId) ;
			this.preparedStatement.setString(2, filename) ;
			this.preparedStatement.setInt(3, parentid) ;
//			Tools.preparedStatement.setInt(3, Tools.parentIdStack.lastElement()) ;
			this.resultSet = this.preparedStatement.executeQuery() ;
			if(this.resultSet.next()) {
				fileId = this.resultSet.getInt("tf_id") ;
			}
		}catch(Exception ee) {
			ee.printStackTrace() ;
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
		return fileId ;
	}
	
	/**
	 * 通过文件名查询临时下载文件ID号
	 */
	public int queryDownloadFileIdByTempFileName(String filename ,int parentid) {
		int fileId = -1;
		try {
			String queryDownloadFileIdByTempFileName = "select tf_id from temp_download_file where u_id = ? and tf_name = ? and parent_id = ?" ;
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.QUERY_DOWNLOAD_FILE_ID_BY_TEMP_FILE_NAME) ;
			this.preparedStatement = this.dbmanage.getPreparedStatement(queryDownloadFileIdByTempFileName) ;
			this.preparedStatement.setInt(1, Tools.userId) ;
			this.preparedStatement.setString(2, filename) ;
			this.preparedStatement.setInt(3, parentid) ;
//			Tools.preparedStatement.setInt(3, Tools.parentIdStack.lastElement()) ;
			this.resultSet = this.preparedStatement.executeQuery() ;
			if(this.resultSet.next()) {
				fileId = this.resultSet.getInt("tf_id") ;
			}
		}catch(Exception ee) {
			ee.printStackTrace() ;
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
		return fileId ;
	}
	
	/*
	 * 临时表中创建文件上传记录
	 */
	public boolean saveTempUploadFile(String filename ,String filedate , int parentid) {
		try {
			String saveTempUploadFile = "insert into temp_upload_file (u_id,tf_name,tf_date,tf_tasktype,tf_type,parent_id,tf_state) values(?,?,?,?,?,?,?)" ;
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.SAVE_TEMP_UPLOAD_FILE) ;
			this.preparedStatement = this.dbmanage.getPreparedStatement(saveTempUploadFile) ;
			this.preparedStatement.setInt(1, Tools.userId) ;
			this.preparedStatement.setString(2, filename) ;
			this.preparedStatement.setString(3, filedate) ;
			this.preparedStatement.setString(4, "上传任务") ;
			this.preparedStatement.setString(5, "文件") ;
			this.preparedStatement.setInt(6, parentid) ;
			this.preparedStatement.setString(7, "准备开始") ;
			this.preparedStatement.execute() ;
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
	
	/*
	 * 临时文件表插入下载任务记录
	 */
	public int saveTempDownloadFile(int fileid) {
		String f_name = "";
		long f_size = 0;
		int parent_id = 0;
		//获取文件信息
		try {
			String queryFile = "select f_name,f_size,parent_id from file where f_id = ? " ;
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.QUERY_FILE) ;
			this.preparedStatement = this.dbmanage.getPreparedStatement(queryFile) ;
			this.preparedStatement.setInt(1, fileid) ;
			this.resultSet = this.preparedStatement.executeQuery() ;
			while(this.resultSet.next()) {
				f_name = this.resultSet.getString("f_name") ;
				f_size = this.resultSet.getInt("f_size") ;
				parent_id = this.resultSet.getInt("parent_id") ;
			}
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
		System.out.println("查询到的下载文件的信息是： " + f_name + " " + f_size + " " + parent_id);
		//临时表插入记录
		Date filedownloaddate = new Date() ;
		try {
			String saveTempDownloadFile = "insert into temp_download_file (u_id,tf_name,tf_date,tf_tasktype,tf_type,parent_id,tf_state) values(?,?,?,?,?,?,?)" ;
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.SAVE_TEMP_DOWNLOAD_FILE) ;
			this.preparedStatement = this.dbmanage.getPreparedStatement(saveTempDownloadFile) ;
			this.preparedStatement.setInt(1, Tools.userId) ;
			this.preparedStatement.setString(2, f_name) ;
			this.preparedStatement.setString(3, filedownloaddate.toString()) ;
			this.preparedStatement.setString(4, "下载任务") ;
			this.preparedStatement.setString(5, "文件") ;
			this.preparedStatement.setInt(6, parent_id) ;
			this.preparedStatement.setString(7, "准备开始") ;
			this.preparedStatement.execute() ;
		} catch (Exception e) {
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
		return parent_id ;
	}
	
	/**
	 * 更新上传临时文件状态
	 */
	public void updateTempUploadFileState(int tempfileId, String state) {
		System.out.println("文件ID " + tempfileId + " 状态 " + state);
		try {
			String updateUploadTempFileState = "update temp_upload_file set tf_state = ? where tf_id = ?" ;
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.UPDATE_UPLOAD_TEMP_FILE_STATE) ;
			this.preparedStatement = this.dbmanage.getPreparedStatement(updateUploadTempFileState) ;
			this.preparedStatement.setString(1, state) ;
			this.preparedStatement.setInt(2, tempfileId) ;
			this.preparedStatement.executeUpdate() ;
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
	}
	
	/**
	 * 更新下载临时文件状态
	 */
	public void updateTempDownloadFileState(int tempfileId, String state) {
		try {
			String updateDownloadTempFileState = "update temp_download_file set tf_state = ? where tf_id = ?" ;
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.UPDATE_DOWNLOAD_TEMP_FILE_STATE) ;
			this.preparedStatement = this.dbmanage.getPreparedStatement(updateDownloadTempFileState) ;
			this.preparedStatement.setString(1, state) ;
			this.preparedStatement.setInt(2, tempfileId) ;
			this.preparedStatement.executeUpdate() ;
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
	}
	
	/**
	 * 更新临时文件上传百分比
	 */
//	public void updateTempUploadFilePercent(int tempfileid, String percent) {
//		try {
//			String updateUploadTempFilePercent = "update temp_upload_file set tf_percent = ? where tf_id = ?" ;
//			this.connection = this.dbmanage.getConnection() ;
////			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.UPDATE_UPLOAD_TEMP_FILE_PERCENT) ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(updateUploadTempFilePercent) ;
//			this.preparedStatement.setString(1, percent) ;
//			this.preparedStatement.setInt(2, tempfileid) ;
//			this.preparedStatement.executeUpdate() ;
//			} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			} finally{
//				// 关闭连接
//				if(this.resultSet != null) {
//					try {
//						this.resultSet.close() ;
//					} catch (SQLException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				this.dbmanage.closeLink() ;
//			}
//	}
	
	/**
	 * 更新临时文件下载百分比
	 */
//	public void updateTempDownloadFilePercent(int tempfileid, String percent) {
//		try {
//			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.UPDATE_DOWNLOAD_TEMP_FILE_PERCENT) ;
//			this.preparedStatement.setString(1, percent) ;
//			this.preparedStatement.setInt(2, tempfileid) ;
//			this.preparedStatement.executeUpdate() ;
//			} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			} finally{
//				// 关闭连接
//				if(this.resultSet != null) {
//					try {
//						this.resultSet.close() ;
//					} catch (SQLException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				this.dbmanage.closeLink() ;
//			}
//	}
	
	/**
	 * 更新文件夹的大小，包括其中的子文件夹和文件
	 */
	public void updateFileSize(int fileId ,long filesize) {
		int parentId = -1 ;
		try {
			String queryParentIdByFileId = "select f_size,parent_id from file where f_id = ?" ;
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.QUERY_PARENT_ID_BY_FILE_ID) ;
			this.preparedStatement = this.dbmanage.getPreparedStatement(queryParentIdByFileId) ;
			this.preparedStatement.setInt(1, fileId) ;
			this.resultSet = this.preparedStatement.executeQuery() ;
			if(this.resultSet.next()) {
				parentId = this.resultSet.getInt("parent_id") ; 
				if(parentId == 0) {
					
				}else {
					long parentsize = 0 ;
					// 获取父文件的大小
					this.connection = this.dbmanage.getConnection() ;
//					this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.QUERY_PARENT_ID_BY_FILE_ID) ;
					this.preparedStatement = this.dbmanage.getPreparedStatement(queryParentIdByFileId) ;
					this.preparedStatement.setInt(1, parentId) ;
					this.resultSet = this.preparedStatement.executeQuery() ;
					if(this.resultSet.next()) {
						parentsize = this.resultSet.getLong("f_size") ;
						// 更新父文件的大小
						String updateFileSize = "update file set f_size = ? where f_id = ?" ;
						this.connection = this.dbmanage.getConnection() ;
//						this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.UPDATE_FILE_SIZE) ;
						this.preparedStatement = this.dbmanage.getPreparedStatement(updateFileSize) ;
						this.preparedStatement.setLong(1, parentsize + filesize) ;
						this.preparedStatement.setInt(2, parentId) ;
						this.preparedStatement.executeUpdate() ;
					}
					// 递归更新
					updateFileSize(parentId , filesize) ;
				}
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
	}
	
	/**
	 * 通过文件名查询文件ID号和文件大小
	 */
	public void queryFileIdAndSizeByName(String filename , int parentId) {
		try {
			String queryFileIdAndSizeByName = "select f_id,f_size,parent_id from file where u_id = ? and f_name = ? and parent_id = ?" ;
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.QUERY_FILE_ID_AND_SIZE_BY_NAME) ;
			this.preparedStatement = this.dbmanage.getPreparedStatement(queryFileIdAndSizeByName) ;
			this.preparedStatement.setInt(1, Tools.userId) ;
			this.preparedStatement.setString(2, filename) ;
			this.preparedStatement.setInt(3, parentId) ;
			this.resultSet = this.preparedStatement.executeQuery() ;
			if(this.resultSet.next()) {
				Tools.deleteFileId = this.resultSet.getInt("f_id") ;
				Tools.deleteFileSize = this.resultSet.getLong("f_size") ;
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
	}



/**
 * 通过临时文件ID查询大小
 */
public int getTempFileSize(int tempfileid) {
	int size = 0;
	try {
		String queryTempFileSize = "select tf_size from temp_download_file where tf_id = ? " ;
		this.connection = this.dbmanage.getConnection() ;
//		this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.QUERY_TEMP_FILE_SIZE) ;
		this.preparedStatement = this.dbmanage.getPreparedStatement(queryTempFileSize) ;
		this.preparedStatement.setInt(1, tempfileid) ;
		this.resultSet = this.preparedStatement.executeQuery() ;
		if(this.resultSet.next()) {
			size = this.resultSet.getInt("tf_size") ;
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
	return size;
}
/**
 * 通过临时文件ID查询文件名
 */
public String getTempFileName(int tempfileid) {
	String name = " ";
	try {
		String queryTempFileName = "select tf_name from temp_download_file where tf_id = ? " ;
		this.connection = this.dbmanage.getConnection() ;
//		this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.QUERY_TEMP_FILE_NAME) ;
		this.preparedStatement = this.dbmanage.getPreparedStatement(queryTempFileName) ;
		this.preparedStatement.setInt(1, tempfileid) ;
		this.resultSet = this.preparedStatement.executeQuery() ;
		if(this.resultSet.next()) {
			name = this.resultSet.getString("tf_name") ;
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
	return name;
}
/**
 * 通过临时文件ID查询文件名
 */
public String getTempUploadFileName(int tempfileid) {
	String name = " ";
	try {
		String queryTempUploadFileName = "select tf_name from temp_upload_file where tf_id = ? " ;
		this.connection = this.dbmanage.getConnection() ;
//		this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.QUERY_TEMP_UPLOADFILE_NAME) ;
		this.preparedStatement = this.dbmanage.getPreparedStatement(queryTempUploadFileName) ;
		this.preparedStatement.setInt(1, tempfileid) ;
		this.resultSet = this.preparedStatement.executeQuery() ;
		if(this.resultSet.next()) {
			name = this.resultSet.getString("tf_name") ;
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
	return name;
}


}
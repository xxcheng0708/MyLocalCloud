package com.test.dao.file_dir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.test.db.DBManage;
import com.test.tools.Tools;
/**
 * 文件分割类
 * @author asus
 *
 */
public class FileSeperate {
	private String fileName ;
	private String fileDictionary ;
	private String savePath ;
	private int fileBlockNum ;
	@SuppressWarnings("unused")
	private long fileSize ;
	private long[] fileBlockSizes ;
	private File file ;
	private File fileMkdir ;
	private String foldername;
	private int tempfileid;
	// 文件操作
	private FileManager fileManager ;
	
	// 数据库操作
	private DBManage dbmanage ;
	@SuppressWarnings("unused")
	private Connection connection ;
	private PreparedStatement preparedStatement ;
	private ResultSet resultSet ;
	
	public FileSeperate() {
		this.dbmanage = new DBManage() ;
		this.fileManager = new FileManager(this.dbmanage) ;
	}
	/**
	 * @param fileDictionary
	 * 			要分割的文件的存放地址
	 */			
	public FileSeperate(String fileDictionary,String foldernam , int tempfileid , String savePath) {
		this.foldername = foldernam; 	
		this.dbmanage = new DBManage() ;
		this.fileManager = new FileManager(this.dbmanage) ;
		this.tempfileid = tempfileid ;
		this.savePath = savePath ;
		fileSeperateInit(fileDictionary) ;
	}
	/**
	 * 文件分割初始化
	 * @param fileDictionary
	 * 			要分割的文件的存放地址
	 */
	public void fileSeperateInit(String fileDictionary) {
		this.fileDictionary = fileDictionary ;
		this.file = new File(fileDictionary) ;
//		this.parentDictionary = "E:\\SafeCloudFiles\\uploadtemp" ;
		this.fileName = this.getFileName() ;
		this.fileSize = this.getFileSize() ;
		this.fileBlockNum = this.getFileBlockNum() ;
		//该目录用于存放分割后的文件块
//		fileMkdir = new File(this.parentDictionary + "\\"+foldername) ;
		fileMkdir = new File(this.savePath + "\\"+foldername) ;
		fileMkdir.mkdirs() ;
	}
	
	/**
	 * 获取文件名
	 * @return
	 */
	public String getFileName() {
		return this.file.getName() ;
	}
	/**
	 * 获取文件大小
	 */
	public long getFileSize() {
		return this.file.length() ;
	}
	/**
	 * 计算文件块数
	 */
	public int getFileBlockNum() {
		int count = 0;
		try {
			String getHdfsNodes = "select * from hdfs where u_id = ?" ;
			this.connection = this.dbmanage.getConnection() ;
//			this.preparedStatement = this.dbmanage.getPreparedStatement(Tools.HDFS_NODES) ;
			this.preparedStatement = this.dbmanage.getPreparedStatement(getHdfsNodes) ;
			this.preparedStatement.setInt(1 ,Tools.userId) ;
			this.resultSet = this.preparedStatement.executeQuery() ;
			while(this.resultSet.next()) {
				count ++ ;
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
	 * 计算每块的大小
	 */
	public long[] getFileBlockSize() {
		int count = (int)this.fileBlockNum ;
		long[] fileBlockSizes = new long[count] ;
		
		System.out.println("文件大小：" + getFileSize());
		/**
		 * 正好分完
		 */
		if(this.fileBlockNum != 0) {
			if(getFileSize() % getFileBlockNum() == 0 ) {
				for (int i =0 ; i < count ;i++ ) {
					fileBlockSizes[i] = getFileSize() / getFileBlockNum() ;
				}
			}else {
				// 分不完全
				for(int i = 0 ; i < count - 1 ; i++ ) {
					fileBlockSizes[i] = getFileSize() / getFileBlockNum() ;
				}
				fileBlockSizes[count - 1] = getFileSize() - (count-1) * ( getFileSize() / getFileBlockNum() ) ;
			}
			for(int i=0;i<fileBlockSizes.length ;i++) {
				System.out.println("文件块大小：" + fileBlockSizes[i]);
			}
		}

		return fileBlockSizes ;
	}
	/**
	 * 生成文件块的名称
	 */
	public String getFileBlockName(int blockCount) {
		return this.savePath + "\\"+foldername+"\\" + this.fileName + ".part" + blockCount ;
	}
	
	/**
	 * 文件分块写操作
	 */
	  public boolean writeFile(String fileDictionary ,String fileBlockName,long fileBlockSize[],long beginPos)//往硬盘写文件
	  {
	 
	    RandomAccessFile raf=null;
	    FileOutputStream fos=null;
	    byte[] bt=new byte[1024];
	    long writeByte=0;
	    int len=0;
	    try
	    {
	      raf = new RandomAccessFile(fileDictionary,"r");
	      raf.seek(beginPos);
	      fos = new FileOutputStream(fileBlockName);
	      for(int i = 0 ; i< fileBlockSize.length ; i ++) {
		      while((len=raf.read(bt))>0)
		      {       
		        if(writeByte<fileBlockSize[i])//如果当前块还没有写满
		        {
		          writeByte=writeByte+len;
		          if(writeByte<=fileBlockSize[i])
		            fos.write(bt,0,len);
		          else
		          {
		            len=len-(int)(writeByte-fileBlockSize[i]);
		            fos.write(bt,0,len);
		          }
		        }       
		      }
	      }


	      fos.close();
	      raf.close();
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	      try
	      {
	        if(fos!=null)
	          fos.close();
	        if(raf!=null)
	          raf.close();
	      }
	      catch(Exception f)
	      {
	        f.printStackTrace();
	      }
	      return false;
	    }
	    return true;
	  }
	
	/**
	 * 文件分块
	 */
	public boolean seperateFile() {
		System.out.println("enter function >>>>>> seperateFile");
		//更新临时文件状态为“文件分块中”
		if(this.fileBlockNum != 0) {
			this.fileManager.updateTempUploadFileState(tempfileid,"分块中") ;
			this.fileBlockSizes = getFileBlockSize() ;
		    long writeSize=0;//每次写入的字节
		    long writeTotal=0;//已经写了的字节
		    String FileCurrentNameAndPath = null;
		    System.out.println("文件分块中......");
		    for(int i=0 ; i< this.fileBlockNum ; i++)
		    {
		      if(i<this.fileBlockNum)
		        writeSize = fileBlockSizes[i];//取得每一次要写入的文件大小
		      if(this.fileBlockNum == 1)
//		        FileCurrentNameAndPath = this.fileDictionary + ".bak";
		    	  FileCurrentNameAndPath = this.savePath + "//"+foldername+"//" + this.fileName + ".part" ;	      
		      else
		        FileCurrentNameAndPath=getFileBlockName(i) ;
		      if(!writeFile(this.fileDictionary , FileCurrentNameAndPath , fileBlockSizes , writeTotal))//循环往硬盘写文件
		      {
		    	  System.out.println("文件分块失败");
		    	  return false;
		      }
		      writeTotal=writeTotal+writeSize;
		    }
		    System.out.println("文件分块完成");
		    return true;
		}else {
			JOptionPane.showMessageDialog(null, "请添加HDFS节点！！！", "上传提示", 1);
			return false ;
		}
		

	}
}

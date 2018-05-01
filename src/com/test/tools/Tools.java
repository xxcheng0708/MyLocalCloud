package com.test.tools;

import java.util.Stack;
import com.test.ui.LoginSuccess;
/**
 * 工具类
 * @author asus
 *
 */
public class Tools {
	/**
	 * 数据库连接参数
	 */
	public static String DRIVER = "com.mysql.jdbc.Driver" ;
	public static String URL = "jdbc:mysql://127.0.0.1:3306/cloud?characterEncoding=UTF8" ;
	public static String USERNAME = "root" ; 
	public static String PASSWORD = "root" ;
	
	/**
	 * HDFS节点管理数据库操作语句
	 */
	// 添加HDFS节点
//	public static String ADD_HDFS_NODE = "insert into hdfs (u_id ,hdfs_ip) values(?,?)" ;
//	// 显示HDFS节点列表
//	public static String SHOW_HDFS_NODES = "select * from hdfs where u_id = ?" ;
//	//计算HDFS节点个数
//	public static String HDFS_NODES = "select * from hdfs where u_id = ?" ;
//	// 查询文件块秘钥
//	public static String SHOW_FILE_BLOCK_KEY = "select * from fileblock where f_id = ?" ;
//	// 查询文件块大小
//	public static String SHOW_FILE_SIZE = "select * from file where f_id = ?" ;
	/**
	 * 文件管理数据库操作语句
	 */
	//注册
//	public static String REGISTER = "insert into User (u_name , u_password , u_problem , u_answer) values(? , ? , ? , ?)" ;
//	//登陆
//	public static String LOGIN = "select u_password from User where u_name =?" ;
//	// 注册验证用户名
//	public static String VALIDATE_USERNAME = "select * from User where u_name = ?";
//	//根据用户ID号查询密码
//	public static String QUERY_PAASWORD_BY_USER_ID = "select u_password from user where u_id = ?" ;
//	//修改密码
//	public static String CHANGE_PAASWORD = "update user set u_password = ? where u_id = ?" ;
//	// 找回密码
//	public static String FIND_PASSWORD = "update user set u_password = ? where u_name = ? and u_problem = ? and u_answer = ?" ;
//	// 读取在当前perant_id下的所有文件并显示在界面上
//	public static String SHOW_FILE = "select * from file where parent_id = ? and u_id = ? order by f_type DESC" ;
//	// 根据用户名查询用户id 号
//	public static String QUERY_ID_BY_NAME = "select u_id from User where u_name = ? " ;
//	// 新建文件夹
//	public static String CREATE_DIR = "insert into File (u_id , f_name , f_date , f_type , f_size , parent_id) values(? , ? , ? , ? , ? , ?)" ;
//	// 检查文件是否重名
//	public static String CHECK_FILE_NAME = "select * from File where u_id = ? and f_name = ? and parent_id = ?" ;
//	// 根据用户ID，文件名 ， 父目录ID查找对应文件的ID
//	public static String QUERY_FILE_ID = "select * from File where u_id = ? and f_name = ? and parent_id = ?" ;
//	//通过文件名查询文件ID号
//	public static String QUERY_FILE_ID_BY_FILE_NAME = "select f_id from File where u_id = ? and f_name = ? and parent_id = ?" ;
//	//通过临时文件名查询上传文件ID号
//	public static String QUERY_UPLOAD_FILE_ID_BY_TEMP_FILE_NAME = "select tf_id from temp_upload_file where u_id = ? and tf_name = ? and parent_id = ?" ;
//	//通过临时文件名查询下载文件ID号
//	public static String QUERY_DOWNLOAD_FILE_ID_BY_TEMP_FILE_NAME = "select tf_id from temp_download_file where u_id = ? and tf_name = ? and parent_id = ?" ;
//	
//	// 打开文件夹，显示里面的内容
//	public static String OPEN_DIR = "select f_name from File where parent_id = (select f_id from File where u_id = ? and f_name = ? and parent_id = ?)" ;
//	// 粘贴文件夹
//	public static String PASTE_FILE = "update File set parent_id = ? where f_id = ?" ;
//	// 通过文件夹ID号查询文件名
//	public static String CHECK_PASTE_FILE_NAME = "select * from File where f_name = (select f_name from File where f_id = ?) and u_id = ? and parent_id = ?" ;
//	// 文件重命名
//	public static String RENAME_FILE = "update file set f_name = ? where u_id = ? and parent_id = ? and f_name = ?" ;
//	// 查看文件ID号和文件类型
//	public static String QUERY_FILE_TYPE = "select f_id,f_type,f_size from file where u_id = ? and f_name = ? and parent_id = ?" ;
//	// 文件删除
//	public static String DELETE_FILE = "delete from file where u_id = ? and f_name = ? and parent_id = ?" ;
//	// 检查文件删除
//	public static String CHECK_DELETE_FILE = "select * from file where parent_id = ?" ;
//	// 通过文件id删除文件
//	public static String DELETE_FILE_BY_ID = "delete from file where f_id = ?" ;
//	// 查询子文件、文件夹
//	public static String QUERY_SUB_FILE = "select f_id,f_type,f_name from file where u_id = ? and parent_id = ?" ;
//	// 查看文件属性
//	public static String GET_FILE_PROPERTY = "select * from file where u_id = ? and f_name = ? and parent_id = ?" ;
//	// 根据文件名搜索文件
//	public static String SEARCH_FILE = "select f_name,f_type,parent_id from file where u_id = ? and f_name = ?" ;
//	// 更新文件夹的大小
//	public static String UPDATE_FILE_SIZE = "update file set f_size = ? where f_id = ?" ;
//	// 根据文件ID号查询父ID号和文件大小
//	public static String QUERY_PARENT_ID_BY_FILE_ID = "select f_size,parent_id from file where f_id = ?" ;
//	// 根据文件名查询文件Id和文件大小
//	public static String QUERY_FILE_ID_AND_SIZE_BY_NAME = "select f_id,f_size,parent_id from file where u_id = ? and f_name = ? and parent_id = ?" ;
//	// 文件块下载策略
//	public static String DOWNLOAD_STRATEGY = "select * from fileblock where f_id = ?" ;
	
	/**
	 * 文件上传成功后，同步更新file表和fileblock表中的内容
	 */
	// 保存文件记录
//	public static String SAVE_FILE = "insert into file (u_id,f_name,f_date,f_type,f_size,parent_id) values(?,?,?,?,?,?)" ; 
//	// 保存文件块记录
//	public static String SAVE_FILE_BLOCK = "insert into fileblock (f_id,b_name,b_covername,netdisk_id,f_key) values(?,?,?,?,?)" ;
//	
	/**
	 * 临时文件表/文件块表操作
	 */
	// 查询临时文件大小
//	public static String QUERY_TEMP_FILE_SIZE = "select tf_size from temp_download_file where tf_id = ? "  ;
//	// 查询临时文件名
//	public static String QUERY_TEMP_FILE_NAME = "select tf_name from temp_download_file where tf_id = ? "  ;
//	public static String QUERY_TEMP_UPLOADFILE_NAME = "select tf_name from temp_upload_file where tf_id = ? "  ;	
//	// 查询临时文件块信息
//	public static String QUERY_TEMP_FILE_UPLOAD_FILE_INFO =  "select * from temp_upload_file where tf_id = ?" ;
//	// 创建文件上传任务，并保存至数据库
//	public static String SAVE_TEMP_UPLOAD_FILE = "insert into temp_upload_file (u_id,tf_name,tf_date,tf_tasktype,tf_type,parent_id,tf_state) values(?,?,?,?,?,?,?)" ; 
//	// 创建文件下载任务，并保存至数据库
//	public static String SAVE_TEMP_DOWNLOAD_FILE = "insert into temp_download_file (u_id,tf_name,tf_date,tf_tasktype,tf_type,parent_id,tf_state) values(?,?,?,?,?,?,?)" ; 
//	
//	// 更新上传临时文件状态为“文件分块中”
//	public static String UPDATE_UPLOAD_TEMP_FILE_STATE = "update temp_upload_file set tf_state = ? where tf_id = ?" ;
//	// 更新下载临时文件状态为“文件分块中”
//	public static String UPDATE_DOWNLOAD_TEMP_FILE_STATE = "update temp_download_file set tf_state = ? where tf_id = ?" ;
//	
//	// 更新上传临时文件上传百分比
//	public static String UPDATE_UPLOAD_TEMP_FILE_PERCENT = "update temp_upload_file set tf_percent = ? where tf_id = ?" ;
//	// 更新下载临时文件上传百分比
//	public static String UPDATE_DOWNLOAD_TEMP_FILE_PERCENT = "update temp_download_file set tf_percent = ? where tf_id = ?" ;
//	
//	// 查询正在上传的文件的列表行数
//	public static String UPLOAD_TABLE_ROWS = "select * from temp_upload_file where u_id = ?" ;
//	// 查询下载列表的文件的行数
//	public static String DOWNLOAD_TABLE_ROWS = "select * from temp_download_file where u_id = ?" ;
//	// 查询正在下载的文件的列表行数
//	public static String DOWNLOADING_FILE_NUMBER = "select * from temp_download_file where u_id = ? and tf_state = ?" ;
//	// 查询正在上传的文件的列表行数
//	public static String UPLOADING_FILE_NUMBER = "select * from temp_upload_file where u_id = ? and tf_state = ?" ;
//	// 查询文件名
//	public static String QUERY_DOWNLOAD_TEMP_FILE_NAME = "select * from temp_download_file where u_id = ? and tf_id = ?" ;
//	public static String QUERY_UPLOAD_TEMP_FILE_NAME = "select * from temp_upload_file where u_id = ? and tf_id = ?" ;	
//	// 查询文件信息
//	public static String QUERY_FILE = "select f_name,f_size,parent_id from file where f_id = ? " ;

	/**
	 * BlockDeleteManager sql
	 */
//	public static String QUERY_BLOCK_INFO = "select netdisk_id,b_covername from fileblock where f_id = ?" ;
	/**
	 * 删除上传任务列表
	 */
//	public static String DELETE_UPLOAD_ASSIGNMENT = "delete from temp_upload_file where u_id = ? and tf_name = ?" ;
	/**
	 * 删除下载任务列表
	 */
//	public static String DELETE_DOWNLOAD_ASSIGNMENT = "delete from temp_download_file where u_id = ? and tf_name = ?" ;
	/**
	 * 失败重传
	 */
	// 查询失败的块的ID号和上传的状态（确认是否上产失败）
//	public static String QUERY_REUPLOAD_FILE_ID = "select tf_id,tf_state from temp_upload_file where u_id = ? and tf_name = ? " ;
	
	/**
	 * 重传完成后更新文件表
	 */
	// 获取临时文件表中的信息
//	public static String QUERY_TEMP_FILE_INFO = "select * from temp_upload_file where tf_id = ?" ;
	
	/**
	 * 失败重下
	 */
	// 查询失败的块的ID号和下载的状态（确认是否下载失败）
//	public static String QUERY_REDOWNLOAD_FILE_ID = "select tf_id,tf_state from temp_download_file where u_id = ? and tf_name = ? " ;
	/**
	 * 检查上传下载列表中是否有重复的任务
	 */
	// 检查上传列表
//	public static String 	CKECK_UPLOAD_TABLE = "select tf_name from temp_upload_file where u_id = ?" ;
//	// 检查下载列表
//	public static String 	CKECK_DOWNLOAD_TABLE = "select tf_name from temp_download_file where u_id = ?" ;
//	
	/**
	 * 关闭窗口的后续操作
	 */
	// 关闭正在上传的临时文件
//	public static String CLOSE_UPLOAD_FILE = "select tf_id from temp_upload_file where u_id = ? and tf_state = ?" ;
//	// 更新正在上传的临时文件的状态
//	public static String UPDATE_UPLOAD_FILE_STATE = "update temp_upload_file set tf_state = ? where tf_id = ?" ;
//	// 更新正在上传的临时文件块的状态
//	public static String UPDATE_UPLOAD_TEMP_FILE_BLOCK_STATE = "update temp_upload_BLOCK set tb_state = ? where tf_id = ? and tb_state = ?" ;
//		
//	// 关闭正在下载的临时文件
//	public static String CLOSE_DOWNLOAD_FILE = "select tf_id from temp_download_file where u_id = ? and tf_state = ?" ;
//	// 更新正在下载的临时文件的状态
//	public static String UPDATE_DOWNLOAD_FILE_STATE = "update temp_download_file set tf_state = ? where tf_id = ?" ;
//	
	
	// 获取下载到的文件大小
//	public static String GET_DOWNLOAD_FILE_SIZE = "select tf_size from temp_download_file where tf_id = ?" ;
		
	/**
	 * 用于存放父目录ID号的栈
	 */
	public static Stack<Integer> parentIdStack = new Stack<Integer>() ;
	/**
	 * 存储用户ID号
	 */
	public static int userId ;
	/**
	 * 复制的文件夹的ID号
	 */
	public static int cutFileId = -1 ;
	/**
	 * 复制的文件的大小
	 */
	public static long copyFileSize = 0 ;
	/**
	 * 删除文件ID号
	 */
	public static int deleteFileId = -1 ;
	/**
	 * 删除文件的大小
	 */
	public  static long deleteFileSize = 0 ;
	/**
	 * 同时运行的下载任务限制数
	 */
	public static int fileDownloadLimitNumber = 2;
	/**
	 * 同时运行的上传任务限制数
	 */
	public static int fileUploadLimitNumber = 2;
	
	public static LoginSuccess loginSuccess ;
	
	
	// 2018-04-30
//	public static String GET_FILE_NAME_BY_ID = "select f_name from file where f_id = ?" ;
	// 查询文件上传状态
//	public static String QUERY_UPLOAD_TEMP_FILE_STATE = "select tf_state from temp_upload_file where tf_id = ?" ;
	// 文件块信息保存到数据库
//	public static String SAVE_FILE_BLICK_INFO = "insert into fileblock(f_id ,b_name , b_covername , f_key) values(?,?,?,?)" ;
	
}

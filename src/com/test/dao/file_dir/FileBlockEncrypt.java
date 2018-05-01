package com.test.dao.file_dir;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
/**
 * 加密类
 * @author asus
 *
 */
public class FileBlockEncrypt {
	/**
	 * 伪装文件名
	 */
	private String[] covernamelist; 
	/**
	 * 随机串文件名
	 */
	private String[] randomnamelist; 
	/**
	 * 待加密的文件路径
	 */
	private String filePath ;
	/**
	 * 加密后文件的存放路径
	 */
    public String outPath[] ; 
    /**
     * 给路径的文件夹中包含的文件
     */
    private String fileBlockPath[] ;
	/**
	 * 加密秘钥字符串
	 */
	@SuppressWarnings("unused")
	private String encryptKey ;
	/**
	 * 临时文件ID
	 */
	private int tempfileid ;
	/**
	 * 根据加密秘钥字符串得到的加密秘钥
	 */
	private Key key =null;
	/**
	 * 加密对象
	 */
	private Cipher fileBlockEncrypt ;
	/**
	 * 根据给定的文件路径创建一个File对象
	 */
	private File file ;
	/**
	 * 用于创建一个目录存放加密后的文件块
	 */
	private File fileMkdir ;
	
	/**
	 * 进行文件操作
	 */
	private FileManager fileManager ;
	
	/**
	 * 构造方法
	 * @param filePath 文件地址
	 * @param encryptKey 加密秘钥
	 */
	public FileBlockEncrypt(String filePath ,String encryptKey ,int tempfileid) {
		this.filePath = filePath ;
		this.encryptKey = encryptKey ;
		this.tempfileid = tempfileid ;
		this.fileManager = new FileManager() ;
		init() ;
		getKey(encryptKey) ;
		initCipher() ;
	}
	/**
	 * 变量初始化
	 */
	public void init() {
		// 根据文件地址创建File对象
		file = new File(this.filePath) ;
		// 获取filePath下面的文件列表
		fileBlockPath = file.list() ;
		// 加密后的文件块存放地址
		outPath = new String[fileBlockPath.length] ;
		// 创建加密后的文件块存放目录，在当前文件块下建立encrypt文件夹
		fileMkdir = new File(filePath + "\\encrypt") ;
		// 创建目录
		fileMkdir.mkdirs() ;
	}
	/**
	 * 产生加密秘钥
	 * @param encryptKey 加密秘钥
	 * @return
	 */
	public Key getKey(String encryptKey) {
		byte[] keyByte = encryptKey.getBytes() ;
		byte[] tempByte = new byte[8] ;
        for (int i = 0; i < tempByte.length && i < keyByte.length; i++) {  
        	tempByte[i] = keyByte[i];  
        }  
        
        /**
         * 产生加密秘钥
         */
        this.key = new SecretKeySpec(tempByte, "DES");  
        return this.key;  
	}
	/**
	 * Cipher对象初始化
	 */
	public void initCipher() {
		try {
			fileBlockEncrypt = Cipher.getInstance("DES") ;
			fileBlockEncrypt.init(Cipher.ENCRYPT_MODE, this.key) ;
		} catch (InvalidKeyException e) {  
            e.printStackTrace();  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
        }  
		
	}
	
    /** 
     * 加密文件并且记录加密后的文件路径 
     * */  
    public boolean crateEncryptFile() {  
    	//更新数据库临时文件表状态为“加密中”
    	this.fileManager.updateTempUploadFileState(tempfileid,"加密中") ;
    	// 加密后的文件爱你保存路径
        String path = null;  
        /**
         * 岁搜友filePath中制定的文件轮流进行假面操作
         */     
        covernamelist = new String[fileBlockPath.length];
        randomnamelist = new String[fileBlockPath.length];        
        for (int i = 0; i < fileBlockPath.length; i++) {  
            try {
            	String covername = this.fileManager.generateSecretString() ;
            	System.out.println(fileBlockPath[i] + ".bin" + i);
            	covernamelist[i] = fileBlockPath[i] + ".bin" + i;
            	randomnamelist[i] = covername;
            	path = filePath + "\\encrypt\\" + covername;
                encrypt(filePath + "\\" + fileBlockPath[i], path);  
                // 存放加密后的文件存放路径
                outPath[i] = path;  
                System.out.println(fileBlockPath[i]+"加密完成，加密后的文件是:"+outPath[i]);  
            } catch (Exception e) {  
                e.printStackTrace();  
                return false ;
            }  
        }  
        System.out.println("=========================加密完成=======================");  
        return true ;
    } 
    /** 
     * 加密文件的核心 
     *  
     * @param file 
     *            要加密的文件 
     * @param destFile 
     *            加密后存放的文件名 
     *            将文件加密后输出到制定的文件夹中
     */  
    public void encrypt(String file, String destFile) throws Exception {  
        InputStream is = new FileInputStream(file);  
        OutputStream out = new FileOutputStream(destFile);  
  
        CipherInputStream cis = new CipherInputStream(is, fileBlockEncrypt);  
        byte[] buffer = new byte[1024];  
        int r;  
        while ((r = cis.read(buffer)) > 0) {  
            out.write(buffer, 0, r);  
        }  
        cis.close();  
        is.close();  
        out.close();  
    } 
    
    public String[] getCovernamelist(){
    	return this.covernamelist;
    }
    
    public String[] getRondomnamelist(){
    	return this.randomnamelist;
    }
}

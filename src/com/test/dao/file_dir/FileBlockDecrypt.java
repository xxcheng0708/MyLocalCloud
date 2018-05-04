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
 * 解密类
 * @author asus
 *
 */
public class FileBlockDecrypt {
	/**
	 * 下载下来的加密文件块存放的位置
	 */
	private String filePath ;
	/**
	 * 解密后的文件块存放的位置
	 */
	private String outPath[] ;
	/**
	 * 给定路径的文件夹中包含的文件
	 */
	private String fileBlockPath[] ;
	
	/**
	 * 解密对象
	 */
	private Cipher fileBlockDecrypt ;
	/**
	 * 解密秘钥字符串
	 */
	@SuppressWarnings("unused")
	private String decryptKey ;
	/**
	 * 根据解密秘钥字符串得到的解密秘钥
	 */
	private Key key ;
	
	
	/**
	 * 根据给定的文件路径创建一个File对象
	 */
	private File file ;
	/**
	 * 用于创建一个目录存放解密后的文件块
	 */
	private File fileMkdir ;
	
	/**
	 * 构造方法
	 * @param filePath 文件路径
	 * @param decryptKey 解密秘钥
	 */
	public FileBlockDecrypt(String filePath ,String decryptKey ) {
		this.filePath = filePath ;
		this.decryptKey = decryptKey ;
		init() ;
		getKey(decryptKey) ;
		initCipher() ;
		crateDecryptFile() ;
	}
	/**
	 * 变量初始化
	 */
	public void init() {
		file = new File(this.filePath) ;
		fileBlockPath = file.list() ;
		outPath = new String[fileBlockPath.length] ;
		fileMkdir = new File(filePath + "\\decrypt") ;
		fileMkdir.mkdir() ;
	}
    /**
     * 初始化解密模式
     */
	public void initCipher() {
		try {
			fileBlockDecrypt = Cipher.getInstance("DES") ;
			fileBlockDecrypt.init(Cipher.DECRYPT_MODE, this.key) ;
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 产生解密秘钥
	 * @param decryptKey 解密秘钥
	 * @return
	 */
	public Key getKey(String decryptKey) {
		byte[] keyByte = decryptKey.getBytes() ;
		byte[] tempByte = new byte[8] ;
        for (int i = 0; i < tempByte.length && i < keyByte.length; i++) {  
        	tempByte[i] = keyByte[i];  
        }  
        /**
         * 产生解密秘钥
         */
        this.key = new SecretKeySpec(tempByte, "DES");  
        return this.key;  
	}
    /** 
     * 解密文件并且记录解密后的文件路径 
     * */ 
    private void crateDecryptFile() {  
    	// 解密后的文件的保存路径
        String path = null;  
        /**
         * 搜索filePath中指定的文件轮流进行解密操作
         */
        for (int i = 0; i < fileBlockPath.length; i++) {  
            try {
            	path = filePath + "\\decrypt\\" + fileBlockPath[i].substring(0 ,fileBlockPath[i].lastIndexOf(".")) ;  
            	System.out.println(path);
                decrypt(filePath + "\\" + fileBlockPath[i], path);  
                // 存放加密后的文件存放路径
                outPath[i] = path;  
                System.out.println(fileBlockPath[i]+"解密完成，解密后的文件是:"+outPath[i]);  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
        System.out.println("==========================解密完成=======================");  
          
    }
    /**
     * 解密文件
     * @param file 文件地址
     * @param destFile 解密后文件的存放地址
     * @throws Exception
     */
    public void decrypt(String file ,String destFile) throws Exception {  
        InputStream is = new FileInputStream(file);  
        OutputStream out = new FileOutputStream(destFile) ;
        
        CipherInputStream cis = new CipherInputStream(is, fileBlockDecrypt);  
        byte[] buffer = new byte[1024];  
        int r;  
        System.out.println("file " + file + " testFile " + destFile);
        while ((r = cis.read(buffer)) > 0) {  
            out.write(buffer, 0, r);  
        }  
        cis.close();  
        is.close();  
        out.close();
    }  

}

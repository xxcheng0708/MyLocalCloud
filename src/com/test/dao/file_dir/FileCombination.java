package com.test.dao.file_dir;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.StringTokenizer;
/**
 * 文件块合并类
 * @author asus
 *
 */
public class FileCombination {

	String saveDictionary=null;//拆分文件存放的目录
	String[] separatedFiles;//存放所有拆分文件名
	String[][] separatedFilesAndSize;//存放所有拆分文件名及分件大小
	int fileBlockNum=0;//确定文件个数
	String fileRealName="";//据拆分文件名确定现在原文件名
	// 空的午餐构造方法
	public FileCombination() {
		
	}
	/**
	 * @param saveDictionary
	 * 				
	 */
	public FileCombination(String saveDictionary)
	{
		this.saveDictionary = saveDictionary ;
		getFileAttribute() ;
		combinationFile() ;
	}
	/**
	 *分析文件块，得到文件名
	 */
    private String getRealName(String fileName)
    {
    	StringTokenizer st = new StringTokenizer(fileName , ".");
    	return st.nextToken()+"."+st.nextToken();
    }
    /**
     * 获取文件大小
     */
    private long getFileSize(String fileName)
    {
    	fileName = saveDictionary + fileName;
    	return (new File(fileName).length());
    }
    /**
     * 生成一些属性，做初使化
     * @param drictory 拆分文件目录
     */
    private void getFileAttribute()
    {
    	//定位到saveDictionary目录下
    	File file=new File(this.saveDictionary);
    	// 根据saveDictionary目录下文件块的长度定义数组
    	separatedFiles=new String[file.list().length];
    	// 获取saveDictionary目录下所有的文件块的名字
    	separatedFiles=file.list();
    	//依文件数目动态生成二维数组，包括文件名和文件大小
    	//第一维装文件名，第二维为该文件的字节大小
    	separatedFilesAndSize=new String[separatedFiles.length][2];
    	// 文件块按名排序
    	Arrays.sort(separatedFiles);
    	fileBlockNum=separatedFiles.length;//当前文件夹下面有多少个文件
    	for(int i=0;i<fileBlockNum;i++)
    	{
    		separatedFilesAndSize[i][0]=separatedFiles[i];//文件名
    		separatedFilesAndSize[i][1]=String.valueOf(getFileSize(separatedFiles[i]));//文件大上
    	}
    	fileRealName=getRealName(separatedFiles[fileBlockNum-1]);//取得文件分隔前的原文件名
    }
    /**
     * 合并文件：利用随机文件读写
     * @return true为成功合并文件
     */
    private boolean combinationFile()
    {
    	RandomAccessFile raf=null;
    	long alreadyWrite=0;
    	FileInputStream fis=null;
    	int len=0;
    	byte[] bt=new byte[1024];
    	try
    	{
//    		raf = new RandomAccessFile(this.saveDictionary+fileRealName,"rw");
    		File file = new File("E:\\SafeCloudFiles\\") ;
    		if(!file.exists()) {
    			file.mkdirs() ;
    		}
//    		System.out.println("this.saveDictionary " + this.saveDictionary);
    		raf = new RandomAccessFile("E:\\SafeCloudFiles\\"+fileRealName,"rw");
    		for(int i=0;i<fileBlockNum;i++)
    		{
    			raf.seek(alreadyWrite);
    			fis=new FileInputStream(this.saveDictionary+separatedFilesAndSize[i][0]);
    			while((len=fis.read(bt))>0)
    			{
    				raf.write(bt,0,len);
    			}
    			fis.close();
    			alreadyWrite=alreadyWrite+Long.parseLong(separatedFilesAndSize[i][1]);
    		}
    		raf.close();     
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    		try
    		{
    			if(raf!=null)
    				raf.close();
    			if(fis!=null)
    				fis.close();
    		}
    		catch (IOException f)
    		{
    			f.printStackTrace();
    		}
    		System.out.println("文件合并失败");
    		return false;
    	}
    	System.out.println("文件合并完成");
    	return true;
    }
}

package com.test.thread;

import java.io.IOException;
import com.test.model.HadoopHDFS;

public class HdfsNodesListThread implements Runnable {
	private HadoopHDFS hdfs ;
	
	public HdfsNodesListThread() {
		this.hdfs = new HadoopHDFS() ;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			hdfs.refreshHdfsNodes() ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

package com.test.event;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.JOptionPane;
import org.json.JSONException;
import org.json.JSONObject;
import com.test.model.ApiTest;
import com.test.tools.Tools;
import com.test.ui.AddHdfsNode;
/**
 * 添加网盘事件类
 * @author asus
 *
 */
public class AddHdfsEvent extends MouseAdapter {
	//获取添加网盘类的引用
	private AddHdfsNode addHdfsNode ;
	public AddHdfsEvent(AddHdfsNode addHdfsNode) {
		this.addHdfsNode = addHdfsNode ;
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == this.addHdfsNode.testHdfs) {
			// 点击测试按钮
			JOptionPane.showMessageDialog(null, "测试一下", "信息提示", 1);
		}
		if(e.getSource() == this.addHdfsNode.confirm) {
			// 将集群节点信息写入数据库
			String ip = this.addHdfsNode.thdfsIP.getText() ;
			if(Tools.loginSuccess.hdfs.addHdfsNode(ip)) {
				JOptionPane.showMessageDialog(null, "HDFS节点添加成功", "信息提示", 1);
			}
		}
		if(e.getSource() == this.addHdfsNode.cancle) {
			this.addHdfsNode.setVisible(false) ;
		}
	}

}

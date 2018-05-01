package com.test.event;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.test.dao.file_dir.FileManager;
import com.test.thread.DownloadListTableThread;
import com.test.thread.UploadListTableThread;
import com.test.tools.Tools;
import com.test.ui.FindPassword;
import com.test.ui.LoginFrame;
import com.test.ui.LoginSuccess;
import com.test.ui.Register;
/**
 * 登录验证事件类
 * @author asus
 *
 */
public class LoginEvent extends MouseAdapter {
	/**
	 * 获取登录界面的引用
	 */
	private LoginFrame loginFrame ;
	public LoginEvent(LoginFrame loginFrame) {
		this.loginFrame = loginFrame ;
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// 注册
		// TODO Auto-generated method stub
		if(e.getSource() == this.loginFrame.register) {
			this.loginFrame.registerFrame = new Register() ;
		}else if (e.getSource() == this.loginFrame.login) {
			//登录验证成功
			try {
				if(this.loginFrame.user.login(this.loginFrame)) {
					// 根据用户名计算唯一 的用户ID号
					Tools.userId = this.loginFrame.user.queryIdByName(this.loginFrame.tusername.getText()) ;
					System.out.println("用户ID号： " + Tools.userId);
					if(Tools.userId != -1) {
						// 初始化登陆成功界面
						Tools.loginSuccess = new LoginSuccess() ;
						//隐藏登录窗口
						this.loginFrame.setVisible(false) ;
						// 初始化父目录栈，初始值为0，表示在根目录
						Tools.parentIdStack.add(0) ;
						//第一个参数设置为0表示初始时显示的是根目录下的文件，第二个参数是根据用户名查询到的用户ID号
						new FileManager().showFileList(0 , Tools.userId) ;
						new Thread(new UploadListTableThread(Tools.loginSuccess)).start() ;
						new Thread(new DownloadListTableThread(Tools.loginSuccess)).start() ;
						// 显示HDFS节点信息
						Tools.loginSuccess.hdfs.showHdfsNodes() ;
					}
				}
			}catch(Exception ee) {
				ee.printStackTrace() ;
			}
		}else  if(e.getSource() == this.loginFrame.forgotPassword){
			// 显示找回密码界面
			this.loginFrame.findPassword = new FindPassword() ;
		}
	}
}

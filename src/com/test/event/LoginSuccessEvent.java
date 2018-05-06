package com.test.event;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.test.dao.file_dir.DownloadTableManager;
import com.test.dao.file_dir.FileManager;
import com.test.dao.file_dir.FileUpload;
import com.test.dao.file_dir.UploadTableManager;
import com.test.thread.HdfsNodesListThread;
import com.test.tools.Tools;
import com.test.ui.AddHdfsNode;
import com.test.ui.ChangePassword;
import com.test.ui.CloseWindowOperation;
import com.test.ui.LoginFrame;
/**
 * 登陆成功事件类
 * @author asus
 *
 */
public class LoginSuccessEvent extends MouseAdapter implements ActionListener , WindowListener {
	// 文件管理
	private FileManager fileManager ;
	// 保存选中的列表的行数和列数
	private int row ;
	@SuppressWarnings("unused")
	private int column ;
	/**
	 * 文件上传、下载列表管理
	 */
	private UploadTableManager uploadTableManager ;
	private DownloadTableManager downloadTableManager ;
	
	public LoginSuccessEvent() {
		this.fileManager = new FileManager() ;
		this.uploadTableManager = new UploadTableManager() ;
		this.downloadTableManager = new DownloadTableManager() ;
	}
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		/**
		 * HDFS节点信息部分
		 * 添加HDFS节点
		 */
		if(e.getSource() == Tools.loginSuccess.addHdfsNode) {
			//添加选择文件控件
			new AddHdfsNode() ;
		}
		/**
		 * 显示用户的HDFS节点列表
		 */
		if(e.getSource() == Tools.loginSuccess.myHdfsNode) {
			new Thread(new HdfsNodesListThread()).start() ;
		}
		
		/**
		 * 文件管理部分
		 * 返回上一级目录，通过栈操作
		 */
		if(e.getSource() == Tools.loginSuccess.goBack) {
			if(Tools.parentIdStack.size() > 1) {
				Tools.parentIdStack.pop() ;
				Tools.loginSuccess.fileBottomPanel.removeAll() ;
				this.fileManager.showFileList(Tools.parentIdStack.lastElement(), Tools.userId) ;
				Tools.loginSuccess.repaint() ;
				// 返回时更新文件地址栏
				int index = Tools.loginSuccess.search.getText().lastIndexOf("\\") ;
				Tools.loginSuccess.search.setText(Tools.loginSuccess.search.getText().substring(0 , index)) ;
			}
		}
		/**
		 * 在当前目录下创建新文件夹
		 */
		if(e.getSource() == Tools.loginSuccess.createDir) {
			this.fileManager.createDir() ;
		}
		/**
		 * 选择要上传的文件
		 */
		if(e.getSource() == Tools.loginSuccess.upload) {
			Tools.loginSuccess.fileChooserUpload.showDialog(Tools.loginSuccess , "上传到网盘");
		}

		/**
		 * 从根目录显示文件列表，包括文件夹、文件
		 */
		if(e.getSource() == Tools.loginSuccess.myFiles) {
			// 清空父目录ID号栈
			Tools.parentIdStack.removeAllElements() ;
			// 移除界面上原有的文件
			Tools.loginSuccess.fileBottomPanel.removeAll() ;
			// 初始化父目录栈，初始值为0，表示在根目录
			Tools.parentIdStack.add(0) ;
			//第一个参数设置为0表示初始时显示的是根目录下的文件，第二个参数是根据用户名查询到的用户ID号
			this.fileManager.showFileList(0 , Tools.userId) ;
			// 地址栏设置为空
			Tools.loginSuccess.search.setText("") ;
			// 刷新界面
			Tools.loginSuccess.repaint() ;
		}
	}
	// 添加鼠标右击事件
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getButton()==MouseEvent.BUTTON3) {
			/**
			 *  进行文件粘贴
			 */
			if(e.getSource() == Tools.loginSuccess.fileBottomPanel ) {
				Tools.loginSuccess.mouseRightCilck(e.getX() , e.getY()) ;
			}
			/**
			 * 上传下载列表鼠标右键功能部分
			 */
			if(e.getSource() == Tools.loginSuccess.uploadTable) {
				Point p = e.getPoint(); 
				row = Tools.loginSuccess.uploadTable.rowAtPoint(p); 
				column = Tools.loginSuccess.uploadModel.getColumnCount() ; 
				System.out.println("row -> " + row + " column -> " + column);
				int selectRow = Tools.loginSuccess.uploadTable.getSelectedRow() ;
				System.out.println("selectRow ->" + selectRow);
				if (selectRow != -1) {
					Tools.loginSuccess.mouseRightClickUploadTable(e.getX() , e.getY()) ;
				}
			}
			if(e.getSource() == Tools.loginSuccess.downloadTable) {
				Point p = e.getPoint(); 
				row = Tools.loginSuccess.downloadTable.rowAtPoint(p); 
				column = Tools.loginSuccess.downloadModel.getColumnCount() ; 
				
				int selectRow = Tools.loginSuccess.downloadTable.getSelectedRow() ;
				if (selectRow != -1) {
					Tools.loginSuccess.mouseRightClickDownloadTable(e.getX() , e.getY()) ;
				}
			}
		}
	}
	
	/**
	 * 进行文件分割、加密、上传操作
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		// 通过目录选择上传文件的操作
		if(e.getSource() == Tools.loginSuccess.fileChooserUpload) {
			if(e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
				// 获取到要上传的文件地址
				String filePath = Tools.loginSuccess.fileChooserUpload.getSelectedFile().toString() ;
				if(this.uploadTableManager.checkUploadTable(filePath.substring(filePath.lastIndexOf("\\") + 1))) {
    				FileUpload fileUpload = new FileUpload(filePath , "E:\\SafeCloudFiles\\uploadtemp") ;
    				new Thread(fileUpload).start();
				}else {
					JOptionPane.showMessageDialog(null , "该文件已在上传列表中！！！","上传提示" , 1);
				}
			}
		}
		
		// 文件剪切粘贴操作
		if(e.getActionCommand() == "粘贴") {
			// 文件粘贴
			this.fileManager.pasteFile() ;
			Tools.loginSuccess.fileBottomPanel.removeAll() ;
			this.fileManager.showFileList(Tools.parentIdStack.lastElement(), Tools.userId) ;
			Tools.loginSuccess.repaint() ;
			// 更新文件夹大小
			this.fileManager.updateFileSize(Tools.cutFileId, Tools.copyFileSize) ;
			Tools.cutFileId = -1 ;
			Tools.copyFileSize = 0 ;
		}
		
		/**
		 * 上传列表操作
		 */
//		if(e.getSource() == Tools.loginSuccess.reUpload) {
//			String filename = (String)Tools.loginSuccess.uploadModel.getValueAt(row, 0) ;
//			String state = (String)Tools.loginSuccess.uploadModel.getValueAt(row, 2) ;
//			if(state.equals("上传失败")) {
//				new Thread(new ReUpload(filename)).start() ;
//			}else {
//				JOptionPane.showMessageDialog(null, "文件已上传成功，无需重新上传！", "信息提示", 1);
//			}
//		}
		if(e.getSource() == Tools.loginSuccess.deleteUploadAssignment) {
			System.out.println("删除上传任务");
			String filename = (String)Tools.loginSuccess.uploadModel.getValueAt(row, 0) ;
			String fileParentName = filename.substring(0, filename.indexOf(".")) ;
			System.out.println("父目录 " + fileParentName);
			if(this.uploadTableManager.deleteUploadAssignment(filename)) {
				this.uploadTableManager.fileManager.deleteDirectory("E:\\SafeCloudFiles\\uploadtemp\\" +fileParentName) ;
			}
		}
		/**
		 * 下载列表操作
		 */
//		if(e.getSource() == Tools.loginSuccess.reDownload) {
//			String filename = (String)Tools.loginSuccess.downloadModel.getValueAt(row, 0) ;
//			String state = (String)Tools.loginSuccess.downloadModel.getValueAt(row, 2) ;
//			if(state.equals("下载失败")) {
//				new Thread(new ReDownload(filename)).start() ;
//			}else {
//				JOptionPane.showMessageDialog(null, "文件已下载成功，无需重新下载！", "信息提示", 1);
//			}
//		} 
		if(e.getSource() == Tools.loginSuccess.deleteDownloadAssignment) {
			System.out.println("删除下载任务");
			String filename = (String)Tools.loginSuccess.downloadModel.getValueAt(row, 0) ;
			
			String fileParentName = filename.substring(0, filename.indexOf(".")) ;
			System.out.println("父目录 " + fileParentName);
			if(this.downloadTableManager.deleteDownloadAssignment(filename)) {
				this.downloadTableManager.fileManager.deleteDirectory("E:\\SafeCloudFiles\\downloadtemp\\" +fileParentName) ;
			}
		}
		
		/**
		 * 菜单栏事件处理
		 */
		if(e.getSource() == Tools.loginSuccess.useritem) {
			System.out.println("用户信息");
		}
		if(e.getSource() == Tools.loginSuccess.pswditem) {
			System.out.println("修改密码");
			@SuppressWarnings("unused")
			ChangePassword changePassword = new ChangePassword() ;
		}
		if(e.getSource() == Tools.loginSuccess.logoutitem) {
			System.out.println("注销");
			Tools.loginSuccess.setVisible(false) ;
			@SuppressWarnings("unused")
			LoginFrame loginFrame = new LoginFrame() ;
		}
	}
	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
	}
	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == Tools.loginSuccess) {
			new CloseWindowOperation() ;
		}
		System.exit(0) ;
	}
	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
	}
}

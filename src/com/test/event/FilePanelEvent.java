package com.test.event;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import com.sun.corba.se.impl.orbutil.closure.Future;
import com.test.dao.file_dir.FileDelete;
import com.test.dao.file_dir.FileManager;
import com.test.tools.Tools;
import com.test.ui.FilePanel;
import com.test.ui.ShowMessage;
/**
 * 文件夹、文件事件类
 * @author asus
 *
 */
public class FilePanelEvent extends MouseAdapter implements ActionListener,KeyListener {
	String old_filename = "" ;
	String new_filename = "" ;
	private FilePanel filePanel ;
	// 进行文件操作
	private FileManager fileManager ;
	public FilePanelEvent(FilePanel filePanel) {
		this.filePanel = filePanel ;
		this.fileManager = new FileManager() ;
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		//双击鼠标打开文件
		if(e.getSource() == this.filePanel) {
			if(e.getClickCount() == 2) {
				String filename = this.filePanel.fileName.getText() ;
				this.fileManager.openDir(filename) ;
			}
		}
	}
	/**
	 * 鼠标进入文件区域
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == this.filePanel) {
			// 鼠标进入文件夹区域，显示阴影
			if(this.filePanel.filePic.getIcon().toString().equals("image/dir.png" )) {
				this.filePanel.filePic.setIcon(new ImageIcon("image/dir2.png")) ;
				this.filePanel.setBackground(new Color(208,228,252)) ;
				this.filePanel.fileName.setBackground(new Color(196,221,252)) ;
				String filename = this.filePanel.fileName.getText() ;
				String fileInfo = this.fileManager.getProperty(filename) ;
				Tools.loginSuccess.fileInfoPanel.removeAll() ;
				Tools.loginSuccess.fileInfoPanel.setBackground(new Color(241,245,251)) ;
				
				JTextArea showFileInfo = new JTextArea(fileInfo) ;
				showFileInfo.setBackground(new Color(241,245,251)) ;
				showFileInfo.setFont(new Font("宋体" ,Font.PLAIN , 12)) ;
				Tools.loginSuccess.fileInfoPanel.add(showFileInfo) ;
				Tools.loginSuccess.repaint() ;
			}
			// 鼠标进入文件区域，显示阴影
			if(this.filePanel.filePic.getIcon().toString().equals("image/file.png" )) {
				this.filePanel.filePic.setIcon(new ImageIcon("image/file2.png")) ;
				this.filePanel.setBackground(new Color(208,228,252)) ;
				this.filePanel.fileName.setBackground(new Color(196,221,252)) ;
				String filename = this.filePanel.fileName.getText() ;
				String fileInfo = this.fileManager.getProperty(filename) ;
				Tools.loginSuccess.fileInfoPanel.removeAll() ;
				Tools.loginSuccess.fileInfoPanel.setBackground(new Color(241,245,251)) ; ;
				
				JTextArea showFileInfo = new JTextArea(fileInfo) ;
				showFileInfo.setBackground(new Color(241,245,251)) ;
				showFileInfo.setFont(new Font("宋体" ,Font.PLAIN , 12)) ;
				Tools.loginSuccess.fileInfoPanel.add(showFileInfo) ;
				Tools.loginSuccess.repaint() ;
			}
		}
	}
	/**
	 * 鼠标退出文件区域
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == this.filePanel) {
			// 鼠标退出文件夹区域，去除阴影
			if(this.filePanel.filePic.getIcon().toString().equals("image/dir2.png" )) {
				this.filePanel.filePic.setIcon(new ImageIcon("image/dir.png")) ;
				this.filePanel.setBackground(new Color(252,252,252)) ;
				this.filePanel.fileName.setBackground(new Color(252,252,252)) ;
				Tools.loginSuccess.fileInfoPanel.removeAll() ;
				Tools.loginSuccess.fileInfoPanel.setBackground(Color.white) ;
				Tools.loginSuccess.repaint() ;
			}
			// 鼠标退出文件区域，去除阴影
			if(this.filePanel.filePic.getIcon().toString().equals("image/file2.png" )) {
				this.filePanel.filePic.setIcon(new ImageIcon("image/file.png")) ;
				this.filePanel.setBackground(new Color(252,252,252)) ;
				this.filePanel.fileName.setBackground(new Color(252,252,252)) ;
				Tools.loginSuccess.fileInfoPanel.removeAll() ;
				Tools.loginSuccess.fileInfoPanel.setBackground(Color.white) ;
				Tools.loginSuccess.repaint() ;
			}
		}
	}

	// 添加鼠标右击事件
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.isPopupTrigger()) {
			this.filePanel.mouseRightClick(e.getX() , e.getY()) ;
		}
	}
	/**
	 * 鼠标右击对文件进行的操作
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getActionCommand() == "剪切") {
			// 文件剪切
			this.fileManager.cutFile(this.filePanel.fileName.getText()) ;
			// 更新文件大小
			this.fileManager.updateFileSize(Tools.cutFileId, -Tools.copyFileSize) ;
		}
		if(e.getActionCommand() == "粘贴") {
			// 文件粘贴
			this.fileManager.pasteFile() ;
			Tools.loginSuccess.fileBottomPanel.removeAll() ;
			this.fileManager.showFileList(Tools.parentIdStack.lastElement(), Tools.userId) ;
			Tools.loginSuccess.repaint() ;
			// 更新文件大小
			this.fileManager.updateFileSize(Tools.cutFileId, Tools.copyFileSize) ;
			Tools.cutFileId = -1 ;
			Tools.copyFileSize = 0 ;
		}

		if(e.getActionCommand() == "重命名") {
			old_filename = this.filePanel.fileName.getText() ;
			System.out.println("old  " + old_filename);
			this.filePanel.fileName.requestFocus() ;
			this.filePanel.fileName.setEditable(true) ;
			this.filePanel.fileName.addKeyListener(this) ;
			if(old_filename.contains(".")) {
				int index = old_filename.lastIndexOf(".") ;
				this.filePanel.fileName.setSelectionStart(0) ;
				this.filePanel.fileName.setSelectionEnd(index) ;
			}else {
				this.filePanel.fileName.setSelectionStart(0) ;
				this.filePanel.fileName.setSelectionEnd(old_filename.length()) ;
			}
		}
		if(e.getActionCommand() == "删除") {
			int res = JOptionPane.showConfirmDialog(null, "确定删除该文件吗？", "删除提示", JOptionPane.OK_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE);
			if(res == JOptionPane.OK_OPTION) {
				String filename = this.filePanel.fileName.getText() ;
				this.fileManager.queryFileIdAndSizeByName(filename, Tools.parentIdStack.lastElement()) ;
				this.fileManager.updateFileSize(Tools.deleteFileId, -Tools.deleteFileSize) ;
				Tools.deleteFileId = -1 ;
				Tools.deleteFileSize = 0 ;
				int parentId = Tools.parentIdStack.lastElement() ;
				
				/**
				 * 文件删除（解决卡顿的问题）
				 */
				FileDelete fileDeleteManager = new FileDelete(filename , parentId) ;
				FutureTask<Boolean> task = new FutureTask<Boolean>(fileDeleteManager) ;
				new Thread(task).start() ;
				
				try {
					if(task.get()) {
						Tools.loginSuccess.fileBottomPanel.removeAll() ;
						this.fileManager.showFileList(Tools.parentIdStack.lastElement(), Tools.userId) ;
						Tools.loginSuccess.repaint() ;
					}
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}else if(res == JOptionPane.CANCEL_OPTION) {
				
			}
		}
		if(e.getActionCommand() == "文件属性") {
			String filename = this.filePanel.fileName.getText() ;
			new ShowMessage(this.fileManager.getProperty(filename)) ;
		}
	}
	/**
	 * 重命名后按Enter键确认提交
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		int key = e.getKeyCode() ;
		switch(key) {
		case KeyEvent.VK_ENTER:
			new_filename = this.filePanel.fileName.getText() ;
			// 文件名未改动，不操作
			if(old_filename.equals(new_filename)) {
				System.out.println("名字没变");
				this.filePanel.fileName.setEditable(false) ;
			}else {
				// 更新文件名
				System.out.println("new : " + new_filename);
				if(new_filename.contains(".") 
						|| new_filename.contains("\\")
						|| new_filename.contains("|")
						|| new_filename.contains("\"")
						|| new_filename.contains("'") 
						|| new_filename.contains("”")
						|| new_filename.contains("’")
						|| new_filename.contains("*")
						|| new_filename.contains(":") 
						|| new_filename.contains("/")
						|| new_filename.contains("?")
				) {
					this.filePanel.fileName.setText(old_filename) ;
					this.filePanel.fileName.setEditable(false) ;
					Tools.loginSuccess.repaint() ;
					JOptionPane.showMessageDialog(null, "文件名不能包含非法字符(\\,|,\",',’,”,*,:,/,?)", "信息提示", 1);
				}else if(!(new_filename.length() > 0 && new_filename.length() <= 20)) {
					this.filePanel.fileName.setText(old_filename) ;
					this.filePanel.fileName.setEditable(false) ;
					Tools.loginSuccess.repaint() ;
					JOptionPane.showMessageDialog(null, "文件名长度介于0~20个字符。。", "信息提示", 1);
				}else {
					// 文件名更改成功
					if(this.fileManager.rename(new_filename, old_filename)) {
						System.out.println("名字更改成功");
						this.filePanel.fileName.setText(new_filename) ;
						this.filePanel.fileName.setEditable(false) ;
						Tools.loginSuccess.repaint() ;
					}else {
						// 文件名更改失败
						System.out.println("名字更改失败");
						this.filePanel.fileName.setText(old_filename) ;
						this.filePanel.fileName.setEditable(false) ;
						Tools.loginSuccess.repaint() ;
					}
				}

			}
			this.filePanel.fileName.removeKeyListener(this) ;
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}

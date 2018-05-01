package com.test.event;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import com.test.ui.FindPassword;
/**
 * 找回密码事件类
 * @author asus
 *
 */
public class FindPasswordEvent extends MouseAdapter {
	/**
	 * 获取找回密码类的引用
	 */
	private FindPassword findPassword ;
	public FindPasswordEvent(FindPassword findPassword) {
		this.findPassword = findPassword ;
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == this.findPassword.confirm) {
			if(this.findPassword.user.findPassword(this.findPassword)) {
				JOptionPane.showMessageDialog(null, "找回密码成功", "信息提示", 1);
				this.findPassword.setVisible(false) ;
			}else {
//				JOptionPane.showMessageDialog(null, "找回密码失败", "信息提示", 1);
			}
		}
		
		if(e.getSource() == this.findPassword.cancle) {
			this.findPassword.setVisible(false) ;
		}

	}
}

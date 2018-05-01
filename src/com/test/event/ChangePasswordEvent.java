package com.test.event;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import com.test.ui.ChangePassword;

public class ChangePasswordEvent extends MouseAdapter {
	private ChangePassword changePassword ;
	public ChangePasswordEvent(ChangePassword changePassword) {
		this.changePassword = changePassword ;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == this.changePassword.confirm) {
			System.out.println("修改密码");
			if(this.changePassword.user.changePassword(changePassword)) {
				JOptionPane.showMessageDialog(null, "修改密码成功", "信息提示", 1);
				this.changePassword.setVisible(false) ;
			}else {
				JOptionPane.showMessageDialog(null, "修改密码失败", "信息提示", 1);
			}
		}
		if(e.getSource() == this.changePassword.cancle) {
			this.changePassword.setVisible(false) ;
		}
	}

}

package com.test.event;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;

import com.test.ui.Register;
/**
 * 用户注册事件类
 * @author asus
 *
 */
public class RegisterEvent extends MouseAdapter {
	/**
	 * 获取注册类引用
	 */
	private Register register ;
	public RegisterEvent(Register register) {
		this.register = register ;
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == this.register.confirm) {
			try {
				if(this.register.user.register(this.register)) {
					JOptionPane.showMessageDialog(null, "注册成功", "信息提示", 1);
					this.register.setVisible(false) ;
				}else {
					System.out.println("注册失败");
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else if(e.getSource() == this.register.cancle) {
			this.register.setVisible(false) ;
		}
	}
}

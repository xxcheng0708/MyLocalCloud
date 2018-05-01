package com.test.ui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;
import com.test.event.ChangePasswordEvent;
import com.test.model.User;
import com.test.override.JTextField2;
/**
 * 找回密码类
 * @author asus
 *
 */
public class ChangePassword extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel panel = new JPanel();
	
	private JLabel loldPassword = new JLabel("旧密码：");
	private JLabel lnewpassword = new JLabel("新密码：");
	private JLabel lconfirmPassword = new JLabel("确认密码：");
	
	private JPasswordField toldPassword = new JPasswordField("");
	private JPasswordField tnewpassword = new JPasswordField();
	private JPasswordField tconfirmPassword = new JPasswordField();
	

	public JButton confirm = new JButton("确认");
	public JButton cancle = new JButton("取消");
	
	public User user ;	
	private ChangePasswordEvent changePasswordEvent ;
	public ChangePassword() {
		changePasswordEvent = new ChangePasswordEvent(this) ;
		init() ;
	}
	
	
	public void init() {
		
		user = new User() ;
		
		this.setTitle("Safe Cloud 修改密码");
		this.setBounds(525, 150, 450, 400);
		this.setResizable(false);
		this.add(panel);
		
		loldPassword.setBounds(new Rectangle(50 , 50 , 60 , 30)) ;
		toldPassword.setBounds(new Rectangle(110 , 50 , 200 , 30)) ;
		toldPassword.setFont(new Font("宋体" ,Font.PLAIN , 16)) ;
		
		lnewpassword.setBounds(new Rectangle(50 , 100 , 60 , 30)) ;
		tnewpassword.setBounds(new Rectangle(110,100,200,30)) ;
		tnewpassword.setFont(new Font("宋体" ,Font.PLAIN , 16)) ;
		
		lconfirmPassword.setBounds(new Rectangle(50 , 150 , 60 , 30)) ;
		tconfirmPassword.setBounds(new Rectangle(110 , 150 , 200 , 30)) ;
		tconfirmPassword.setFont(new Font("宋体" ,Font.PLAIN , 16)) ;
		
		confirm.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		confirm.setBounds(new Rectangle(115 , 200 , 90 , 30)) ;
		cancle.setBounds(new Rectangle(215 , 200 , 90 , 30)) ;
		
		confirm.addMouseListener(changePasswordEvent) ;
		cancle.addMouseListener(changePasswordEvent) ;
		
		panel.setLayout(null);
		panel.add(loldPassword) ;
		panel.add(toldPassword) ;
		panel.add(lnewpassword) ;
		panel.add(tnewpassword) ;
		panel.add(lconfirmPassword) ;
		panel.add(tconfirmPassword) ;
		panel.add(confirm) ;
		panel.add(cancle) ;
		
		this.setVisible(true) ;
	}
	/**
	 * 获取旧密码
	 */
	public String getOldPassword() {
		return this.toldPassword.getText() ;
	}
	
	/**
	 * 获取新密码
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String getNewPassword() {
		return this.tnewpassword.getText() ;
	}
	
	/**
	 * 获取确认密码
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String getConfirmPassword() {
		return this.tconfirmPassword.getText() ;
	}
	
	/**
	 * 显示异常信息
	 * @param str
	 */
	public void paint(String str) {
		Graphics g = this.getGraphics();
		super.paint(g) ;
		g.setFont(new Font("微软雅黑", Font.BOLD, 15));
		g.drawString(str, 200, 60);
	}

}

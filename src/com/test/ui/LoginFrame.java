package com.test.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;
import com.test.event.LoginEvent;
import com.test.model.User;
import com.test.override.JTextField2;
/**
 * 登录界面类
 * @author asus
 *
 */
public class LoginFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private JPanel pane = new JPanel();
	private JPanel basepane=new JPanel(); 
	public JButton login = new JButton("登      录");
	public JButton register = new JButton("注册账号");
	public JButton forgotPassword = new JButton("找回密码");
	public JTextField2 tusername = new JTextField2("");
	private JPasswordField tpassword = new JPasswordField(20);
	private JLabel lusername = new JLabel("账号：");
	private JLabel lpassword = new JLabel("密码：");
	private JLabel image = new JLabel();
	private ImageIcon img = new ImageIcon("image/1.png");
	private JCheckBox rememberusername = new JCheckBox("记住密码",false);
	private JCheckBox autologin = new JCheckBox("自动登录",false);
	private GridBagLayout gb = new GridBagLayout();
	private GridBagConstraints gbc = new GridBagConstraints();
	
	public Register registerFrame ; 
	public LoginSuccess loginSuccess ;
	public FindPassword findPassword ;
	
	public User user ;
	private LoginEvent loginEvent ;
	
	public LoginFrame() {
		init() ;
	}
	
	public void init() {
		loginEvent = new LoginEvent(this) ;
		user = new User() ;
		
		this.register.addMouseListener(loginEvent) ;
		this.login.addMouseListener(loginEvent) ;
		this.forgotPassword.addMouseListener(loginEvent) ;
		
		this.setTitle("Safe Cloud 登录");
		this.setBounds(500, 250, 500, 375);
		this.setResizable(false);
		this.add(basepane);
		basepane.setLayout(new BorderLayout());
		image.setIcon(img);
		basepane.add(image,BorderLayout.NORTH);
		basepane.add(pane);
		pane.setLayout(gb);
		gbc.insets = new Insets(3,3,3,3);
		lusername.setFont(new Font("宋体" ,Font.PLAIN , 16)) ;
		gb.setConstraints(lusername, gbc);
		pane.add(lusername);
		gbc.gridwidth = 3;
		tusername.setColumns(20);
		tusername.setFont(new Font("宋体" ,Font.PLAIN , 16)) ;
		gb.setConstraints(tusername, gbc);
		pane.add(tusername);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gb.setConstraints(register, gbc);
		pane.add(register);
		gbc.gridwidth = 1;
		lpassword.setFont(new Font("宋体" ,Font.PLAIN , 16)) ;
		gb.setConstraints(lpassword, gbc);
		pane.add(lpassword);
		gbc.gridwidth = 3;
		tpassword.setFont(new Font("宋体" ,Font.PLAIN , 16)) ;
		gb.setConstraints(tpassword, gbc);
		pane.add(tpassword);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gb.setConstraints(forgotPassword, gbc);
		pane.add(forgotPassword);
		gbc.gridx = 1;
		gbc.gridwidth = 1;
		gb.setConstraints(rememberusername, gbc);
		pane.add(rememberusername);
		gbc.gridx = 2;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gb.setConstraints(autologin, gbc);
		pane.add(autologin);
		gbc.gridx = 1;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		login.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		gb.setConstraints(login, gbc);
		pane.add(login);
		
		this.setVisible(true);
	}
	
	/**
	 * 获取用户名
	 * @return
	 */
	public String getUsername() {
		return this.tusername.getText() ;
	}
	/**
	 * 获取密码
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String getPassword() {
		return this.tpassword.getText() ;
	}
	
	
	/**
	 * 显示异常信息
	 * @param str
	 */
	public void paint(String str) {
		Graphics g = this.getGraphics();
		super.paint(g) ;
		g.setFont(new Font("微软雅黑", Font.BOLD, 15));
		g.drawString(str, 180, 120);
	}
	
}

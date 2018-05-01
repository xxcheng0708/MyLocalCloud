package com.test.ui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import com.test.event.RegisterEvent;
import com.test.model.User;
import com.test.override.JTextField2;

/**
 * 用户注册类
 * @author asus
 *
 */
@SuppressWarnings("serial")
public class Register extends JFrame {
	//	组件
	private JPanel panel = new JPanel();
	
	private JLabel lusername = new JLabel("输入账号：");
	private JLabel lpassword = new JLabel("输入密码：");
	private JLabel lconfirmpassword = new JLabel("确认密码：");
	private JLabel lquestion = new JLabel("密保问题：");
	private JLabel lanswer = new JLabel("密保答案：");
	
	private JTextField2 tusername = new JTextField2("");
	private JTextField2 tanswer = new JTextField2("");
	private JPasswordField tpassword = new JPasswordField();
	private JPasswordField tconfirmPassword = new JPasswordField();	
	private String[] items = {"你是谁？"} ;
	private JComboBox<String> jcproblem = new JComboBox<String>(items);
	public JButton confirm = new JButton("确认");
	public JButton cancle = new JButton("取消");
	
	private RegisterEvent registerEvent ;	
	public User user = null ;
	
	public Register() {
		init() ;
	}
	
	public void init() {
		registerEvent = new RegisterEvent(this) ;
		user = new User() ;
		
		this.setTitle("Safe Cloud 注册");
		this.setBounds(525, 150, 450, 500);
		this.setResizable(false);
		this.add(panel);
		//组件位置
		lusername.setBounds(new Rectangle(50 , 50 , 80 , 30)) ;
		tusername.setBounds(new Rectangle(110 , 50 , 200 , 30)) ;
		tusername.setFont(new Font("宋体" ,Font.PLAIN , 16)) ;
		lpassword.setBounds(new Rectangle(50 , 100 , 80 , 30)) ;
		tpassword.setBounds(new Rectangle(110 , 100 , 200 , 30)) ;
		tpassword.setFont(new Font("宋体" ,Font.PLAIN , 16)) ;
		lconfirmpassword.setBounds(new Rectangle(50 , 150 , 80 , 30)) ;
		tconfirmPassword.setBounds(new Rectangle(110 , 150 , 200 , 30)) ;
		tconfirmPassword.setFont(new Font("宋体" ,Font.PLAIN , 16)) ;
		
		lquestion.setBounds(new Rectangle(50 , 200 , 80 , 30)) ;
		jcproblem.setBounds(new Rectangle(110 , 200 , 200 , 30)) ;
		lanswer.setBounds(new Rectangle(50 , 250 , 80 , 30)) ;
		tanswer.setBounds(new Rectangle(110 , 250 , 200 , 30)) ;
		tanswer.setFont(new Font("宋体" ,Font.PLAIN , 16)) ;
		
		confirm.setBounds(new Rectangle(115 , 300 , 90 , 30)) ;
		confirm.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		cancle.setBounds(new Rectangle(215 , 300 , 90 , 30)) ;
		//添加鼠标事件
		confirm.addMouseListener(registerEvent) ;
		cancle.addMouseListener(registerEvent) ;
		//添加组件
		panel.setLayout(null);
		panel.add(lusername) ;
		panel.add(tusername) ;
		panel.add(lpassword) ;
		panel.add(lanswer) ;
		panel.add(tpassword) ;
		panel.add(lconfirmpassword) ;
		panel.add(tconfirmPassword) ;
		panel.add(lquestion) ;
		panel.add(jcproblem) ;
		panel.add(tanswer) ;
		panel.add(confirm) ;
		panel.add(cancle) ;
		
		this.setVisible(true);
		
	}
	/**
	 * 获取用户名
	 */
	public String getUsername() {
		return this.tusername.getText() ;
	}
	
	/**
	 * 获取密码
	 */
	@SuppressWarnings("deprecation")
	public String getPassword() {
		return this.tpassword.getText() ;
	}
	/**
	 * 获取确认密码
	 */
	@SuppressWarnings("deprecation")
	public String getConfirmPassword() {
		return this.tconfirmPassword.getText() ;
	}
	/**
	 * 获取问题
	 */
	public String getProblem() {
		return this.jcproblem.getSelectedItem().toString() ;
	}
	/**
	 * 获取问题答案
	 */
	public String getAnswer(){
		return this.tanswer.getText() ;
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

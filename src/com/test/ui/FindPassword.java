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

import com.test.event.FindPasswordEvent;
import com.test.model.User;
import com.test.override.JTextField2;
/**
 * 找回密码类
 * @author asus
 *
 */
public class FindPassword extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel panel = new JPanel();
	
	private JLabel lusername = new JLabel("输入账号：");
	private JLabel lnewpassword = new JLabel("新密码：");
	private JLabel lconfirmPassword = new JLabel("确认密码：");
	private JLabel lquestion = new JLabel("密保问题：");
	private JLabel lanswer = new JLabel("密保答案：");
	
	private JTextField2 tusername = new JTextField2("");
	private JTextField2 tanswer = new JTextField2("");
	private JPasswordField tpassword = new JPasswordField();
	private JPasswordField tconfirmPassword = new JPasswordField();
	
	private String[] items = {"你是谁？" } ;
	private JComboBox<String> jcproblem = new JComboBox<String>(items);

	public JButton confirm = new JButton("确认");
	public JButton cancle = new JButton("取消");
	
	public User user ;	
	private FindPasswordEvent findPasswordEvent ;
	public FindPassword() {
		init() ;
	}
	
	
	public void init() {
		
		user = new User() ;
		findPasswordEvent = new FindPasswordEvent(this) ;
		
		this.setTitle("Safe Cloud 找回密码");
		this.setBounds(525, 150, 450, 500);
		this.setResizable(false);
		this.add(panel);
		
		lusername.setBounds(new Rectangle(50 , 50 , 60 , 30)) ;
		tusername.setBounds(new Rectangle(110 , 50 , 200 , 30)) ;
		tusername.setFont(new Font("宋体" ,Font.PLAIN , 16)) ;
		lquestion.setBounds(new Rectangle(50 , 100 , 60 , 30)) ;
		jcproblem.setBounds(new Rectangle(110,100,200,30)) ;
		lanswer.setBounds(new Rectangle(50 , 150 , 60 , 30)) ;
		tanswer.setBounds(new Rectangle(110 , 150 , 200 , 30)) ;
		tanswer.setFont(new Font("宋体" ,Font.PLAIN , 16)) ;
		lnewpassword.setBounds(new Rectangle(50 , 200 , 60 , 30)) ;
		tpassword.setBounds(new Rectangle(110 , 200 , 200 , 30)) ;
		tpassword.setFont(new Font("宋体" ,Font.PLAIN , 16)) ;
		lconfirmPassword.setBounds(new Rectangle(50 , 250 , 60 , 30)) ;
		tconfirmPassword.setBounds(new Rectangle(110 , 250 , 200 , 30)) ;
		tconfirmPassword.setFont(new Font("宋体" ,Font.PLAIN , 16)) ;
		
		confirm.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		confirm.setBounds(new Rectangle(115 , 300 , 90 , 30)) ;
		cancle.setBounds(new Rectangle(215 , 300 , 90 , 30)) ;
		
		confirm.addMouseListener(findPasswordEvent) ;
		cancle.addMouseListener(findPasswordEvent) ;
		
		panel.setLayout(null);
		panel.add(lusername) ;
		panel.add(tusername) ;
		panel.add(lquestion) ;
		panel.add(jcproblem) ;
		panel.add(lanswer) ;
		panel.add(tanswer) ;
		panel.add(lnewpassword) ;
		panel.add(tpassword) ;
		panel.add(lconfirmPassword) ;
		panel.add(tconfirmPassword) ;
		panel.add(confirm) ;
		panel.add(cancle) ;
		
		this.setVisible(true) ;
		

	}
	/**
	 * 获取用户名
	 */
	public String getUsername() {
		return this.tusername.getText() ;
	}
	
	/**
	 * 获取密码问题
	 * @return
	 */
	public String getProblem() {
		return this.jcproblem.getSelectedItem().toString() ;
	}
	
	/**
	 * 获取密码答案
	 * @return
	 */
	public String getAnswer() {
		return this.tanswer.getText() ;
	}
	
	/**
	 * 获取新密码
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String getPassword() {
		return this.tpassword.getText() ;
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

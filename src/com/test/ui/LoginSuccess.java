package com.test.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import com.test.dao.file_dir.FileManager;
import com.test.dao.file_dir.FileSeperate;
import com.test.event.LoginSuccessEvent;
import com.test.model.HadoopHDFS;
import com.test.override.JTextField2;

/**
 * 登录成功界面类
 * @author asus
 *
 */
@SuppressWarnings("serial")
public class LoginSuccess extends JFrame {
	ImageIcon img = null;
	
	//主面板	
	private JTabbedPane tabbedpane = new JTabbedPane(JTabbedPane.LEFT,JTabbedPane.WRAP_TAB_LAYOUT); 
	
	//菜单栏
	private JMenuBar menubar ;
	private JMenu user ;
	private JMenu option ;
	public JMenuItem useritem ;
	public JMenuItem pswditem ;
	public JMenuItem logoutitem ;
	
	//背景图片
	private ImageIcon backpic = new ImageIcon("image/background.png");
	
	//传输列表控件
	public JPanel transmitPanel ;
	private JTabbedPane transmittabbedpane ;  
	//上传列表
	public JScrollPane uploadScrollpane; 
	private JPanel uploadPanel ;
	public DefaultTableModel uploadModel ;
	public JTable uploadTable ;
	public String[] uploadcolnames={"文件名","状态"};
	public JMenuItem reUpload ;
	public JMenuItem deleteUploadAssignment ;
	//下载列表
	private JScrollPane downloadScrollpane; 
	private JPanel downloadPanel;
	public DefaultTableModel downloadModel ;
	public JTable downloadTable ;
	String[] downloadcolnames={"文件名","状态"};
	public JMenuItem reDownload ;
	public JMenuItem deleteDownloadAssignment ;
	
	//HDFS节点管理部分控件
	public JPanel hdfsPanel ;
	@SuppressWarnings("unused")
	private ImageIcon hdfsIcon ;
	public JLabel addHdfsNode ;
	public JLabel myHdfsNode ;
	private JPanel hdfsTopPanel ;
	public JPanel hdfsBottomPanel ;
	public DefaultTableModel hdfsModel; 
	public JTable hdfsNodesTable; 
	private JScrollPane hdfsScrollpane; 
	String[] colnames={"IP地址","主机名","已用空间","全部空间","使用率","节点类型"}; 
	
	// HDFS文件系统类
	public HadoopHDFS hdfs ;
	
	//我的文件管理部分控件
	public JPanel file ;
	@SuppressWarnings("unused")
	private ImageIcon fileIcon ;
	private JPanel fileTopPanel ;
	private JPanel navicatPanel ;
	@SuppressWarnings("unused")
	private JPanel searchPanel ;
	public JPanel fileBottomPanel ;
	public JPanel fileInfoPanel ;
	@SuppressWarnings("unused")
	private JScrollPane scrollPane ;
	
	//文件管理导航栏
	public JLabel goBack ; // 返回上一级
	public JLabel createDir ; //创建文件夹
	public JLabel upload ; // 上传文件
	public JLabel  myFiles ; // 已上传的文件
	public JFileChooser fileChooserUpload ; //选择要上传的文件
	public JTextField2 search ;
	public FilePanel filePanel ;
	public FileManager fileManager ;
	
	// 文件剪切、粘贴操作
	public JPopupMenu popMenu ;
	public JMenuItem paste ;
	
	// 登录成功界面的事件管理
	public LoginSuccessEvent loginSuccessEvent ;
	
	public LoginSuccess() {
		init() ;
		setMenuBar() ;
		setUploadList(0) ;
		setDownloadList(0) ;
		this.addWindowListener(loginSuccessEvent) ;
	}
	
	public void init() {
		// HDFS文件系统类初始化
		hdfs = new HadoopHDFS() ; 
		
		// 文件管理初始化
		fileManager = new FileManager() ;
		
		loginSuccessEvent = new LoginSuccessEvent() ;
		
		this.setBounds(220,10,900,675);
		this.setTitle("Safe Cloud");

		//设置背景
		JPanel panel = new JPanel(){
			 public void paintComponent(Graphics g){
				 g.drawImage(backpic.getImage(), 0, 0, this.getSize().width,this.getSize().height,this);
			 }
		};
		this.add(panel);
		panel.setLayout(new BorderLayout());
		//添加tab页

		panel.add(tabbedpane);
		
		/**
		 * 传输列表部分
		 */
		transmitPanel = new JPanel();
		transmittabbedpane = new JTabbedPane(JTabbedPane.TOP,JTabbedPane.WRAP_TAB_LAYOUT);

		transmitPanel.setLayout(new BorderLayout());
		transmitPanel.add(transmittabbedpane);
		
		/**
		 * HDFS节点管理部分
		 */
		hdfsPanel = new JPanel() ;
//		netDiskIcon = new ImageIcon("image/plate.png") ;
		hdfsTopPanel = new JPanel() ;
		hdfsBottomPanel = new JPanel() ;
		addHdfsNode = new JLabel("添加HDFS集群") ;
		myHdfsNode = new JLabel("HDFS节点列表") ;
		this.fileManager.fileSeperate = new FileSeperate() ;
		hdfsModel = new DefaultTableModel(colnames,1); 
//		System.out.println("HDFS节点个数：" + this.fileManager.fileSeperate.getFileBlockNum());
		hdfsNodesTable = new JTable(hdfsModel){
			public boolean isCellEditable(int row , int column) {
				return false ;
			}
		};
		hdfsScrollpane = new JScrollPane(hdfsNodesTable) ;
		hdfsPanel.setLayout(new BorderLayout() ) ;
		hdfsTopPanel.setLayout(new FlowLayout(FlowLayout.LEFT)) ;
		hdfsBottomPanel.setLayout(new BorderLayout()) ;
		hdfsTopPanel.add(myHdfsNode) ;
		hdfsTopPanel.add(addHdfsNode) ;		
		hdfsBottomPanel.add(hdfsScrollpane) ;
		hdfsPanel.add(hdfsTopPanel , BorderLayout.NORTH) ;
		hdfsPanel.add(hdfsBottomPanel , BorderLayout.CENTER) ;
		
		hdfsNodesTable.addMouseListener(loginSuccessEvent) ;
		addHdfsNode.addMouseListener(loginSuccessEvent) ;
		myHdfsNode.addMouseListener(loginSuccessEvent) ;
		
		/**
		 * 文件管理部分
		 */
		goBack = new JLabel() ; //返回上一级
		goBack.setIcon(new ImageIcon("image/goback.png")) ;
		goBack.addMouseListener(loginSuccessEvent) ;
		
		createDir = new JLabel(new ImageIcon("image/createDir.png")) ; //新建文件夹
		createDir.addMouseListener(loginSuccessEvent) ;
		
		upload = new JLabel(new ImageIcon("image/upload.png")) ; // 上传文件
		upload.addMouseListener(loginSuccessEvent) ;
		
		myFiles = new JLabel("我的文件") ;
		myFiles.addMouseListener(loginSuccessEvent) ;
				
		search = new JTextField2("") ;
		search.setEditable(false);
		//设置查询框的长度
		search.setColumns(60) ;
		
		//选择要上传的文件
		fileChooserUpload = new JFileChooser() ;																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																						
		fileChooserUpload.setDialogType(JFileChooser.OPEN_DIALOG) ;
		fileChooserUpload.addActionListener(loginSuccessEvent) ;
		
		file = new JPanel() ;
		file.setLayout(new BorderLayout()) ;
		
		fileTopPanel = new JPanel() ;
		fileTopPanel.setLayout(new BorderLayout()) ;
		
		//设置导航栏
		navicatPanel = new JPanel() ;
		navicatPanel.setLayout(new FlowLayout(FlowLayout.LEFT , 5 , 0)) ;
		navicatPanel.add(goBack) ;
		navicatPanel.add(myFiles) ;
		navicatPanel.add(createDir) ;
		navicatPanel.add(upload) ;
		navicatPanel.add(search) ;
		
		fileBottomPanel = new JPanel() ;
		this.fileBottomPanel.setBackground(new Color(252,252,252)) ;
		//设置为左对齐显示
		fileBottomPanel.setLayout(new FlowLayout(FlowLayout.LEFT , 10 , 0) ) ;
		fileBottomPanel.addMouseListener(loginSuccessEvent) ;
		fileIcon = new ImageIcon("image/plate.png") ;

		fileInfoPanel=  new JPanel() ;
		fileInfoPanel.setLayout(new FlowLayout(FlowLayout.LEFT)) ;
		
		fileTopPanel.add(navicatPanel,BorderLayout.CENTER ) ;
		
		file.add(fileTopPanel , BorderLayout.NORTH) ;
		file.add(fileBottomPanel , BorderLayout.CENTER) ;		
		file.add(fileInfoPanel , BorderLayout.SOUTH) ;		
		
		//添加Tab页
		tabbedpane.addTab("我的文件"  , file) ;
		tabbedpane.addTab("传输列表" , transmitPanel) ;
		tabbedpane.addTab("HDFS管理" , hdfsPanel) ;
//		drag();
		this.setVisible(true) ;
	}
	/**
	 * 设置菜单栏
	 */
	public void setMenuBar() {
		menubar = new JMenuBar();
		user = new JMenu("用户");
		option = new JMenu("选项");
		useritem = new JMenuItem("用户信息");
		pswditem = new JMenuItem("修改密码");
		logoutitem = new JMenuItem("注销账户");
		// 给菜单栏添加事件
		useritem.addActionListener(loginSuccessEvent) ;
		pswditem.addActionListener(loginSuccessEvent) ;
		logoutitem.addActionListener(loginSuccessEvent) ;
		//组装菜单栏
		user.add(useritem);
		user.add(pswditem);
		user.add(logoutitem);
		menubar.add(user);
		menubar.add(option);
		this.setJMenuBar(menubar);
	}

    //定义的拖拽方法
//    public void drag() {
//    	new DropTarget( fileBottomPanel, DnDConstants.ACTION_COPY_OR_MOVE, new FileDragIntoEvent()) ;
//    }
    
    /**
     * 设置文件上传列表
     * @param rows
     */
    public void setUploadList(int rows) {
    	//上传列表
		uploadPanel = new JPanel();
		uploadPanel.setLayout(new BorderLayout()) ;
		
		uploadModel = new DefaultTableModel(uploadcolnames,rows);
		uploadTable = new JTable(uploadModel){
			public boolean isCellEditable(int row , int column) {
				return false ;
			}
		};
		uploadTable.addMouseListener(loginSuccessEvent) ;
		uploadScrollpane = new JScrollPane(uploadTable) ; 
		
		transmittabbedpane.add("正在上传", uploadPanel);
		uploadPanel.add(uploadScrollpane);
    }
    
    /**
     * 设置文件下载列表
     * @param rows
     */
    public void setDownloadList(int rows) {
		downloadPanel = new JPanel();	
		downloadPanel.setLayout(new BorderLayout()) ;
		downloadModel = new DefaultTableModel(downloadcolnames,rows);
		downloadTable = new JTable(downloadModel){
			public boolean isCellEditable(int row , int column) {
				return false ;
			}
		};
		downloadTable.addMouseListener(loginSuccessEvent) ;
		downloadScrollpane = new JScrollPane(downloadTable) ;
		
		transmittabbedpane.add("正在下载", downloadPanel);

		downloadPanel.add(downloadScrollpane);
    }
    
	/**
	 * 在文件管理面板单机右键，出现粘贴功能
	 * @param x
	 * @param y
	 */
	public void mouseRightCilck(int x,int y) {
		System.out.println("是我吗");
		// 文件粘贴操作
		popMenu = new JPopupMenu() ;
		paste = new JMenuItem("粘贴") ;
		popMenu.add(paste) ;
		popMenu.setLocation(x, y) ;
		popMenu.show(this.fileBottomPanel , x ,y) ;
		paste.addActionListener(loginSuccessEvent) ;
	}
    
    /**
     * 上传列表的鼠标右键操作
     * @param x
     * @param y
     */
	public void mouseRightClickUploadTable(int x , int y) {
		popMenu = new JPopupMenu() ;
		reUpload = new JMenuItem("失败重传") ;
		deleteUploadAssignment = new JMenuItem("删除任务") ;

		reUpload.addActionListener(loginSuccessEvent) ;
		deleteUploadAssignment.addActionListener(loginSuccessEvent) ;
		
		popMenu.add(reUpload) ;
		popMenu.add(deleteUploadAssignment) ;
		popMenu.setLocation(x, y) ;
		popMenu.show(this.uploadTable , x ,y) ;
	}
	
	/**
	 * 下载列表的鼠标右键操作
	 * @param x
	 * @param y
	 */
	public void mouseRightClickDownloadTable(int x , int y) {
		popMenu = new JPopupMenu() ;
		reDownload = new JMenuItem("失败重下") ;
		deleteDownloadAssignment = new JMenuItem("删除任务") ;

		reDownload.addActionListener(loginSuccessEvent) ;
		deleteDownloadAssignment.addActionListener(loginSuccessEvent) ;
		
		popMenu.add(reDownload) ;
		popMenu.add(deleteDownloadAssignment) ;
		popMenu.setLocation(x, y) ;
		popMenu.show(this.downloadTable , x ,y) ;
	}
}

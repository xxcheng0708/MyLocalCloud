package com.test.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import com.test.event.FilePanelEvent;
import com.test.override.JTextField2;
/**
 * 显示文件类
 * @author asus
 *
 */
@SuppressWarnings("serial")
public class FilePanel extends JPanel{
	private FilePanelEvent filePanelEvent ;
	public JLabel filePic ;
	public JTextField2 fileName ;
	public JPopupMenu popMenu ;
	public JMenuItem paste ;
	public JMenuItem cut ;
	public JMenuItem rename ;
	public JMenuItem delete ;
	public JMenuItem property ;
	
	public FilePanel(Icon icon , String name) {
		init(icon , name) ;
	}
	/**
	 * 设置文件图标和文件名
	 * @param icon
	 * @param name
	 */
	public void init(Icon icon , String name) {
		this.setBackground(new Color(252,252,252)) ;
		filePanelEvent = new FilePanelEvent(this) ;
		this.filePic = new JLabel(icon) ;
		this.fileName = new JTextField2("") ;
		this.fileName.setBorder(null) ;
		this.fileName.setFont(new Font("宋体" ,Font.PLAIN , 13)) ;
		this.fileName.setBackground(new Color(252,252,252)) ;
		this.fileName.setHorizontalAlignment(JTextField2.CENTER);
		this.fileName.setColumns(10) ;
		this.fileName.setText(name) ;
		this.setLayout(new BorderLayout()) ;
		this.add(filePic , BorderLayout.CENTER) ;
		this.add(fileName , BorderLayout.SOUTH) ;
		this.addMouseListener(filePanelEvent) ;
		this.fileName.setEditable(false) ;
		this.setSize(80, 80) ;
	}
	
	/**
	 * 在文件夹上右击鼠标显示的菜单选项
	 * @param x
	 * @param y
	 */
	public void mouseRightClick(int x , int y) {
		popMenu = new JPopupMenu() ;
		paste = new JMenuItem("粘贴") ;
		cut = new JMenuItem("剪切") ;
		rename = new JMenuItem("重命名") ;
		delete = new JMenuItem("删除") ;
		property = new JMenuItem("文件属性") ;

		popMenu.add(cut) ;
		popMenu.add(paste) ;
		popMenu.add(rename) ;
		popMenu.add(delete) ;
		popMenu.add(property) ;
		popMenu.setLocation(x, y) ;
		popMenu.show(this , x ,y) ;
		
		paste.addActionListener(filePanelEvent) ;
		cut.addActionListener(filePanelEvent) ;
		rename.addActionListener(filePanelEvent) ;
		delete.addActionListener(filePanelEvent) ;
		property.addActionListener(filePanelEvent) ;
	}

}

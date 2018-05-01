package com.test.ui;

import javax.swing.JFrame;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class ShowMessage extends JFrame{
	private JTextArea showFileProperty ;
	public ShowMessage(String text) {
		init(text) ;
	}
	
	public void init(String text) {
		showFileProperty = new JTextArea(text) ;
		this.setSize(400, 300) ;
		this.add(showFileProperty) ;
		this.setVisible(true) ;
	}
}

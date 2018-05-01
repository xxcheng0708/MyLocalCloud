package com.test.ui;

import javax.swing.UIManager;

public class Start {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	    try
	    {
	        org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
	        UIManager.put("RootPane.setupButtonVisible", false);
	    }
	    catch(Exception e)
	    {
	        //TODO exception
	    }
		
		new LoginFrame() ;
	}

}

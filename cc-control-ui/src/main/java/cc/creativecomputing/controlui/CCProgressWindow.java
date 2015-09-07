package cc.creativecomputing.controlui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import cc.creativecomputing.core.util.CCFormatUtil;

public class CCProgressWindow extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4436530082291804538L;
	private JLabel jl;
	private JProgressBar dpb ;

	public CCProgressWindow(){
		super();
	    setSize(500, 150);
	    jl = new JLabel();
	    jl.setText("Count : 0");

	    add(BorderLayout.NORTH, jl);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


	    dpb = new JProgressBar(0, 500);
	    add(BorderLayout.SOUTH, dpb);
	    add(BorderLayout.CENTER, new JLabel("Progress..."));
	}
	
	public void progress(double theProgress){
	   if(!isVisible()) setVisible(true);
		jl.setText("Progress : " + CCFormatUtil.ndc(theProgress * 100, 2) + " %");
		dpb.setValue((int)(theProgress * 500));
		 if(dpb.getValue() == 500){
//		        System.exit(0);
		        
		      }
	}
	
	public static void main(String[] args) {
		CCProgressWindow myWindow = new CCProgressWindow();
		
		Thread t = new Thread(new Runnable() {
		      public void run() {
		    	  myWindow.setVisible(true);
		      }
		    });
		    t.start();
		    for (int i = 0; i <= 500; i++) {
		    	myWindow.progress(i / 500f);
		      
		      try {
		        Thread.sleep(25);
		      } catch (InterruptedException e) {
		        e.printStackTrace();
		      }
		    }
	}
}

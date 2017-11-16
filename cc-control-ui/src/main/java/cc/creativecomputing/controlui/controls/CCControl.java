package cc.creativecomputing.controlui.controls;

import javax.swing.JPanel;

public interface CCControl {
	
	public void addToComponent(JPanel thePanel, int theY, int theDepth);
	
	public void dispose();
}

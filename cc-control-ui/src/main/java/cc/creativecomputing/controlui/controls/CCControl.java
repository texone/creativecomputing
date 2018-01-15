package cc.creativecomputing.controlui.controls;

import javax.swing.JPanel;

public interface CCControl {
	
	void addToComponent(JPanel thePanel, int theY, int theDepth);
	
	void dispose();
}

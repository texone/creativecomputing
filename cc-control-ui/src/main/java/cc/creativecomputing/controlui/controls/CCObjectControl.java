package cc.creativecomputing.controlui.controls;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.controlui.CCColorMap;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.controlui.PropertyPopUp;
import cc.creativecomputing.controlui.timeline.view.SwingGuiConstants;

public class CCObjectControl extends JPanel implements CCControl{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2697384457010979576L;
	
	private Container _myInfoPanel;
	private JPanel _mySubPanel;
	
	private boolean _myIsSelected = false;
	
	private int _myDepth;
	
	private String _myName;
	protected PropertyPopUp _myPopUp;

	public CCObjectControl(CCObjectPropertyHandle thePropertyHandle, CCControlComponent theInfoPanel, int theDepth){
		setBackground(CCColorMap.getColor(thePropertyHandle.path()));
		
		_myDepth = theDepth;
		
		_myInfoPanel = theInfoPanel;
		setLayout(new GridBagLayout());
		
		_myName = thePropertyHandle.name();
		

		_myPopUp = new PropertyPopUp(thePropertyHandle, theInfoPanel);
		
		GridBagConstraints myConstraints = new GridBagConstraints();
		myConstraints.gridx = 0;
		myConstraints.gridy = 0;
		myConstraints.gridwidth = 3;
		myConstraints.anchor = GridBagConstraints.LINE_START;
		myConstraints.fill = GridBagConstraints.HORIZONTAL;
		myConstraints.weightx = 1f;
		myConstraints.insets = new Insets(5, 5  + 10 * (_myDepth - 1), 5, 5);
		JLabel myLabel = new JLabel("[+] " + _myName, SwingConstants.LEFT);
		myLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		myLabel.setForeground(Color.WHITE);
		myLabel.setFont(SwingGuiConstants.ARIAL_BOLD_10);
		myLabel.setPreferredSize(new Dimension(100,15));
		
		myLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent theE) {
				JLabel myLabel = (JLabel)theE.getSource();
				if(theE.getButton() == MouseEvent.BUTTON1){
					if(_myIsSelected){
						remove(_mySubPanel);
						_myInfoPanel.invalidate(); 
						_myInfoPanel.validate(); // or ((JComponent) getContentPane()).revalidate();
						_myInfoPanel.repaint();
						_myIsSelected = false;	
						myLabel.setText("[+] " + _myName);
					}else{
						_mySubPanel.removeAll();
						GridBagConstraints myConstraints = new GridBagConstraints();
						myConstraints.gridx = 0;
						myConstraints.gridwidth = 3;
						myConstraints.anchor = GridBagConstraints.LINE_START;
						myConstraints.fill = GridBagConstraints.HORIZONTAL;
	
						int myGridY = 0;
						
						for(JPanel theControl:_myControlPanels){
							myConstraints.gridy = myGridY;
							theControl.setBackground(getBackground().brighter());
							_mySubPanel.add(theControl, myConstraints);
							myGridY++;
						}
						
						_mySubPanel.invalidate(); 
						_mySubPanel.validate(); // or ((JComponent) getContentPane()).revalidate();
						_mySubPanel.repaint();
						
						myConstraints.gridx = 0;
						myConstraints.gridy = 1;
						myConstraints.gridwidth = 3;
						add(_mySubPanel, myConstraints);
						
						_myInfoPanel.invalidate(); 
						_myInfoPanel.validate(); // or ((JComponent) getContentPane()).revalidate();
						_myInfoPanel.repaint();
						_myIsSelected = true;
						myLabel.setText("[-] " + _myName);
					}
				}
				if(theE.getButton() == MouseEvent.BUTTON3){
					_myPopUp.show(myLabel, theE.getX(), theE.getY());
				}
			}
		});
		add(myLabel, myConstraints);
		
		_mySubPanel = new JPanel(new GridBagLayout());
//		_mySubPanel.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}
	
	private List<JPanel> _myControlPanels = new ArrayList<>();
	
	public void addStuff(JPanel theControl){
		_myControlPanels.add(theControl);
	}

	@Override
	public void addToComponent(JPanel thePanel, int theY, int theDepth) {
		GridBagConstraints myConstraints = new GridBagConstraints();
		myConstraints.gridx = 0;
		myConstraints.gridwidth = 3;
		myConstraints.gridy = theY;
		myConstraints.fill = GridBagConstraints.HORIZONTAL;
		thePanel.add(this, myConstraints);
	}
	
	@Override
    protected void paintComponent(Graphics grphcs) {
        super.paintComponent(grphcs);
        Graphics2D g2d = (Graphics2D) grphcs;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gp = new GradientPaint(
        	0, 0,
        	getBackground().brighter(), 0, 20,
        	getBackground());
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight()); 

    }
}

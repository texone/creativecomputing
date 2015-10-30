package cc.creativecomputing.controlui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JSplitPane;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class CCSplitPane extends JSplitPane{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6970650628935362049L;

	public CCSplitPane() {
		super();
	}

	public CCSplitPane(
		int theNewOrientation, 
		boolean theNewContinuousLayout, 
		Component theNewLeftComponent,
		Component theNewRightComponent
	) {
		super(theNewOrientation, theNewContinuousLayout, theNewLeftComponent, theNewRightComponent);
		style();
	}

	public CCSplitPane(int theNewOrientation, boolean theNewContinuousLayout) {
		super(theNewOrientation, theNewContinuousLayout);
		style();
	}

	public CCSplitPane(int theNewOrientation, Component theNewLeftComponent, Component theNewRightComponent) {
		super(theNewOrientation, theNewLeftComponent, theNewRightComponent);
		style();
	}

	public CCSplitPane(int theNewOrientation) {
		super(theNewOrientation);
		style();
	}

	private void style(){
		setBorder(null);
		setUI(new BasicSplitPaneUI() {
	        public BasicSplitPaneDivider createDefaultDivider() {
	        return new BasicSplitPaneDivider(this) {
	            /**
				 * 
				 */
				private static final long serialVersionUID = -4620415539708917115L;

				public void setBorder(Border b) {
	            }

	            @Override
	                public void paint(Graphics g) {
	                g.setColor(Color.GREEN);
	                g.fillRect(0, 0, getSize().width, getSize().height);
	                    super.paint(g);
	                }
	        };
	        }
	    });
		setDividerSize(2);
	}
}

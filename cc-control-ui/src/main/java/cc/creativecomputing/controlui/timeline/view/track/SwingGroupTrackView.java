/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package cc.creativecomputing.controlui.timeline.view.track;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import cc.creativecomputing.controlui.CCUIConstants;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineController;
import cc.creativecomputing.controlui.timeline.controller.track.CCGroupTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.CCTrackController;
import cc.creativecomputing.controlui.timeline.view.SwingMultiTrackPanel;
import cc.creativecomputing.controlui.timeline.view.SwingTableLayout;
import cc.creativecomputing.gl.app.CCGLWindow;


public class SwingGroupTrackView extends CCAbstractTrackView {
	
	private class SingleGroupTrackControlPopup extends JPopupMenu{

		public SingleGroupTrackControlPopup() {
			this.setFont(CCUIConstants.ARIAL_9);
			
			JMenuItem entryHead = new JMenuItem("Group Edit Funtions");
			entryHead.setFont(CCUIConstants.ARIAL_11);
			add(entryHead);
			addSeparator();
			
			JMenuItem myRemoveItem = new JMenuItem("remove track");
			myRemoveItem.setFont(CCUIConstants.ARIAL_11);
			myRemoveItem.setActionCommand("remove");
			myRemoveItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent theE) {
					_myTimelineController.removeTrack(_myGroupController.track().path());
				}
			});
			add(myRemoveItem);
			
			JMenuItem myColorItem = new JMenuItem("color track");
			myColorItem.setFont(CCUIConstants.ARIAL_11);
			myColorItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent theE) {
					openColorChooser();
				}
			});
			add(myColorItem);
			
			JMenuItem myWriteItem = new JMenuItem("write values");
			myWriteItem.setFont(CCUIConstants.ARIAL_11);
			myWriteItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent theE) {
					_myGroupController.writeValues();
				}
			});
			add(myWriteItem);
		}
		
		public void openColorChooser() {
			Color newColor = JColorChooser.showDialog(
				SwingGroupTrackView.this,
				"Choose Background Color",
				SwingGroupTrackView.this.getBackground().brighter()
			);
			_myGroupController.color(newColor);
		}
	}
	
	static final Dimension SMALL_BUTTON_SIZE = new Dimension(20,15);
	static final int RESIZE_HANDLE_HEIGHT = 5;
	static final Dimension ADDRESS_FIELD_SIZE = new Dimension(100,15);


    static final public DecimalFormat VALUE_FORMAT;
    static {
    	VALUE_FORMAT = new DecimalFormat();
    	VALUE_FORMAT.applyPattern("#0.00");
    }
	
	private CCGroupTrackController _myGroupController;
	private CCTimelineController _myTimelineController;
	private JLabel _myOpenButton;
	private JLabel _myAddressField;
	private ArrayList<ActionListener> _myListeners;
	
	private SingleGroupTrackControlPopup _myPopUp = new SingleGroupTrackControlPopup();
	
	private SwingMultiTrackPanel _myMultiTrackPanel;
	
	private boolean _myShowUnusedItems = true;
	
	public SwingGroupTrackView(
		CCGLWindow theMainFrame,
	    CCAbstractTrackDataView<?> theDataView,
		SwingMultiTrackPanel theMultiTrackPanel, 
		CCTimelineController theTimelineController, 
		CCGroupTrackController theGroupController
	) {
		super(theMainFrame, theDataView);
		_myMultiTrackPanel = theMultiTrackPanel;
		_myGroupController = theGroupController;
		_myTimelineController = theTimelineController;
		FlowLayout myLayout = new FlowLayout(FlowLayout.LEFT, 3,2);
		myLayout.setVgap(0);
		setLayout(myLayout);
		
		setMinimumSize(new Dimension(100, 10));
		setPreferredSize(new Dimension(100,10));

		add( Box.createHorizontalStrut((theGroupController.track().path().getNameCount() - 1) * 5));
		_myOpenButton = new JLabel("[-]");
//		_myOpenButton.setBackground(Color.WHITE);
//		_myOpenButton.setForeground(Color.BLACK);
//		_myOpenButton.setBorderPainted(false);
		
		_myOpenButton.setForeground(Color.WHITE);
		_myOpenButton.setFont(CCUIConstants.ARIAL_BOLD_10);
		_myOpenButton.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				boolean _myPressedShift = (e.getModifiers() & ActionEvent.SHIFT_MASK) == ActionEvent.SHIFT_MASK;
				
				if(_myOpenButton.getText().equals("[+]")) {
					_myGroupController.openGroup(_myPressedShift);
				}else if(_myOpenButton.getText().equals("[-]")) {
					_myGroupController.closeGroup(true);
				}
			}
		});
		
		add(_myOpenButton);
		
		_myAddressField = new JLabel(theGroupController.track().path().getFileName().toString());
		_myAddressField.setPreferredSize(new Dimension(100,10));
		_myAddressField.setFont(CCUIConstants.ARIAL_BOLD_10);
		_myAddressField.setForeground(Color.WHITE);
		
		add(_myAddressField);
		
		_myListeners = new ArrayList<ActionListener>();
		
		addMouseListener(new MouseAdapter()  {
			
			@Override
			public void mousePressed(MouseEvent theE) {
				if (theE.getButton() == MouseEvent.BUTTON3)
					_myPopUp.show(SwingGroupTrackView.this, theE.getX(), theE.getY());
			}
		});
		
	}
	
	public CCGroupTrackController controller(){
		return _myGroupController;
	}
	
	public boolean containsData(){
		for(CCTrackController myTrackController:_myGroupController.trackController()) {
			if(myTrackController.trackData().size() > 0)return true;
			
		}
		return false;
	}
	
	public void showUnusedItems(boolean theShowUsedItems){
		_myShowUnusedItems = theShowUsedItems;
	}
	
	/* (non-Javadoc)
	 * @see de.artcom.timeline.view.GroupTrackView#openGroup()
	 */
	public void openGroup() {
		_myOpenButton.setText("[-]");
		int myIndex = _myMultiTrackPanel.index(this) + 1;
		for(CCTrackController myTrackController:_myGroupController.trackController()) {
			if(!_myShowUnusedItems){
				if(myTrackController.trackData().size() <= 1)continue;
			}
			if(myTrackController.view() instanceof SwingTrackView){
				SwingTrackView myTrackView = (SwingTrackView)myTrackController.view();
				
				Path myPath = myTrackController.track().path();
				
				_myMultiTrackPanel.insertTrackView(myTrackView.controlView(), myPath, myIndex, SwingTableLayout.DEFAULT_ROW_HEIGHT, true);
				_myMultiTrackPanel.insertTrackDataView(myTrackView.dataView(), myPath, myIndex);
				myIndex++;
			}
			
			if(myTrackController.view() instanceof SwingGroupTrackView){
				SwingGroupTrackView myTrackView = (SwingGroupTrackView)myTrackController.view();
				
				Path myPath = myTrackController.track().path();
				
				_myMultiTrackPanel.insertTrackView(myTrackView, myPath, myIndex, SwingTableLayout.DEFAULT_GROUP_HEIGHT, true);
				_myMultiTrackPanel.insertTrackDataView(myTrackView.dataView(), myPath, myIndex);
				myIndex++;
			}
			
		}
	}
	
	/* (non-Javadoc)
	 * @see de.artcom.timeline.view.GroupTrackView#closeGroup()
	 */
	public void closeGroup() {
		_myOpenButton.setText("[+]");
		for(CCTrackController myTrackDataController:_myGroupController.trackController()) {
//			CCLog.info(myTrackDataController+":"+myTrackDataController.track().address());
			_myMultiTrackPanel.removeTrackView(myTrackDataController.track().path());
		}
	}
	
	public void color(Color theColor) {
		setBackground(theColor);
		_myDataView.color(theColor);
	}
	
	public void addActionListener( ActionListener theListener ) {
		_myListeners.add(theListener);
	}
	
	public void removeActionListener( ActionListener theListener ) {
		_myListeners.remove(theListener);
	}
	
	@Override
	public void mute(final boolean theMute) {}
	
	@Override
	public void min(double theMin) {}
	
	@Override
	public void max(double theMax) {}

	@Override
	public void finalize() {
	}

}

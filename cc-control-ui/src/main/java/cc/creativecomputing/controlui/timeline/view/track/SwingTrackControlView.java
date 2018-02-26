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

import java.text.DecimalFormat;

import cc.creativecomputing.controlui.CCNumberBox;
import cc.creativecomputing.controlui.CCUIConstants;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineController;
import cc.creativecomputing.controlui.timeline.controller.track.CCBooleanTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.CCCurveTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.CCTrackController;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.draw.CCUIFillDrawable;
import cc.creativecomputing.ui.layout.CCUIGridPane;
import cc.creativecomputing.ui.widget.CCUILabelWidget;
import cc.creativecomputing.ui.widget.CCUIMenu;
import cc.creativecomputing.ui.widget.CCUITextFieldWidget;


public class SwingTrackControlView extends CCUIGridPane{
	
	static final CCVector2 SMALL_BUTTON_SIZE = new CCVector2(20,15);
	static final int RESIZE_HANDLE_HEIGHT = 5;
	static final CCVector2 ADDRESS_FIELD_SIZE = new CCVector2(100,20);


    static final public DecimalFormat VALUE_FORMAT;
    static {
    		VALUE_FORMAT = new DecimalFormat();
    		VALUE_FORMAT.applyPattern("#0.000");
    }
	
	private class CCSingleTrackControlPopup extends CCUIMenu{

		public CCSingleTrackControlPopup() {
			super(CCUIConstants.DEFAULT_FONT);
			
			addItem("Track Edit Funtions");
			addSeparator();
			addItem("remove track", ()->{_myTimelineController.removeTrack(_myTrackController.property().path());});
			addItem("reset zoom");
		}
	
	}
	
	private CCTimelineController _myTimelineController;
	private CCTrackController _myTrackController;
	private JToggleButton _myMuteButton;
	private CCNumberBox _myMinField;
	private CCNumberBox _myMaxField;
	private CCUITextFieldWidget _myValueField;
	private CCUILabelWidget _myAddressField;
	private ArrayList<ActionListener> _myListeners;
	
	private CCSingleTrackControlPopup _myPopUp = new CCSingleTrackControlPopup();
	
	public SwingTrackControlView(
		CCTimelineController theTimelineController,
		CCTrackController theTrackController
	) {
		_myTimelineController = theTimelineController;
		_myTrackController = theTrackController;
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints myConstraints = new GridBagConstraints();
		myConstraints.fill = GridBagConstraints.HORIZONTAL;
		
		setMinimumSize(new Dimension( 150, 50));
		setPreferredSize(new Dimension(150,50));

		add( Box.createHorizontalStrut(0));//(theTrackController.property().path().getNameCount() - 1) * 5));
		
		if(CCUIConstants.CREATE_MUTE_BUTTON) {
			_myMuteButton = new JToggleButton("m");
			_myMuteButton.setBackground(Color.WHITE);
			_myMuteButton.setForeground(Color.BLACK);
	//		_myMuteButton.setBorderPainted(false);
			_myMuteButton.setMargin(new Insets(0, 0, 0, 0));
			_myMuteButton.setFont(CCUIConstants.ARIAL_9);
			_myMuteButton.setPreferredSize(new Dimension(20,12));
			_myMuteButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					boolean _myPressedShift = (e.getModifiers() & ActionEvent.SHIFT_MASK) == ActionEvent.SHIFT_MASK;
					boolean _myPressedAlt = (e.getModifiers() & ActionEvent.ALT_MASK) == ActionEvent.ALT_MASK;
					
					if(_myPressedShift) {
						_myTimelineController.muteAll(_myMuteButton.isSelected());
					}else if(_myPressedAlt){
						_myTrackController.muteGroup(_myMuteButton.isSelected());
					}else {
						_myTrackController.mute(_myMuteButton.isSelected());
					}
					_myTrackController.view().dataView().render();
					repaint();
				}
			});
			myConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
			myConstraints.weightx = 0;
			myConstraints.weighty = 0;
			myConstraints.gridx = 0;
			myConstraints.gridy = 0;
			myConstraints.gridwidth = 1;
			myConstraints.insets = new Insets(2, 4, 2, 2);
			add(_myMuteButton, myConstraints);
			myConstraints.insets = new Insets(2, 2, 2, 2);
		}
		_myMuteButton.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
		
		if(
			theTrackController instanceof CCCurveTrackController && 
			!(theTrackController instanceof CCBooleanTrackController)
		) {
			_myMinField = new CCNumberBox(_myTrackController.track().min(), -Float.MAX_VALUE, Float.MAX_VALUE, 2);
			style(_myMinField);
			_myMinField.changeEvents().add(theValue -> {
				_myTrackController.min(theValue);
			});

			myConstraints.weightx = 0;
			myConstraints.weighty = 0;
			myConstraints.gridx = 1;
			myConstraints.gridy = 0;
			myConstraints.gridwidth = 1;
			add(_myMinField, myConstraints);
			
			_myMaxField = new CCNumberBox(_myTrackController.track().max(), -Float.MAX_VALUE, Float.MAX_VALUE, 2);
			style(_myMaxField);
			_myMaxField.changeEvents().add(theValue -> {
				_myTrackController.max(theValue);
			});
			myConstraints.weightx = 0;
			myConstraints.gridx = 1;
			myConstraints.gridy = 1;
			myConstraints.gridwidth = 1;
			add(_myMaxField, myConstraints);
			
			_myValueField = new JTextField();
			style(_myValueField);
			_myValueField.setText(_myTrackController.property().valueString());
			myConstraints.weightx = 0;
			myConstraints.weighty = 1;
			myConstraints.gridx = 1;
			myConstraints.gridy = 2;
			myConstraints.gridwidth = 1;
			add(_myValueField, myConstraints);
		}
		
		_myAddressField = new JLabel("");
		_myAddressField.setFont(CCUIConstants.ARIAL_BOLD_10);
		_myAddressField.setForeground(Color.WHITE);
		myConstraints.weightx = 1;
		myConstraints.weighty = 0;
		myConstraints.gridx = 2;
		myConstraints.gridy = 0;
		myConstraints.gridwidth = 1;
		myConstraints.gridheight = 2;
		add(_myAddressField, myConstraints);
		
		
		
		_myListeners = new ArrayList<ActionListener>();
		
//		setBorder(BorderFactory.createLineBorder(Color.gray));
		
		addMouseListener(new MouseAdapter()  {
			
			@Override
			public void mousePressed(MouseEvent theE) {
				if (theE.getButton() == MouseEvent.BUTTON3) _myPopUp.show(SwingTrackControlView.this, theE.getX(), theE.getY());
			}
		});
		
	}
	
	public void color(CCColor theColor) {
		background(new CCUIFillDrawable(theColor));
	}
	
	public void addActionListener( ActionListener theListener ) {
		_myListeners.add(theListener);
	}
	
	public void removeActionListener( ActionListener theListener ) {
		_myListeners.remove(theListener);
	}
	
	public void mute(final boolean theMute) {
		if(_myMuteButton != null)_myMuteButton.setSelected(theMute);
	}
	
	public void min(final double theMin){
		if(_myMinField != null)_myMinField.setText(theMin + ":");
	}
	
	public void max(final double theMax){
		if(_myMaxField != null)_myMaxField.setText(theMax + ":");
	}

	public void address(final String theAddress) {
		_myAddressField.setText(theAddress);
	}
	
	public void value(final String theValue) {
		if(_myValueField != null)_myValueField.setText(theValue);
	}
	
}

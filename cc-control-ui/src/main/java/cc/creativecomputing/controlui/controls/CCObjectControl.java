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
package cc.creativecomputing.controlui.controls;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.control.CCControlMatrix;
import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.control.CCSelection;
import cc.creativecomputing.control.code.CCShaderSource;
import cc.creativecomputing.control.handles.CCBooleanPropertyHandle;
import cc.creativecomputing.control.handles.CCColorPropertyHandle;
import cc.creativecomputing.control.handles.CCControlMatrixHandle;
import cc.creativecomputing.control.handles.CCEnumPropertyHandle;
import cc.creativecomputing.control.handles.CCEnvelopeHandle;
import cc.creativecomputing.control.handles.CCEventTriggerHandle;
import cc.creativecomputing.control.handles.CCGradientPropertyHandle;
import cc.creativecomputing.control.handles.CCNumberPropertyHandle;
import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.control.handles.CCPathHandle;
import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.control.handles.CCPropertyListener;
import cc.creativecomputing.control.handles.CCSelectionPropertyHandle;
import cc.creativecomputing.control.handles.CCShaderSourceHandle;
import cc.creativecomputing.control.handles.CCSplineHandle;
import cc.creativecomputing.control.handles.CCStringPropertyHandle;
import cc.creativecomputing.control.handles.CCTriggerProgress;
import cc.creativecomputing.controlui.CCColorMap;
import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.controlui.CCPropertyPopUp;
import cc.creativecomputing.controlui.CCUIConstants;
import cc.creativecomputing.controlui.controls.code.CCShaderCompileControl;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.ui.layout.CCUIGridPane;
import cc.creativecomputing.ui.layout.CCUIVerticalFlowPane;
import cc.creativecomputing.uinano.CCUILabel;

public class CCObjectControl extends CCUIGridPane implements CCControl{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2697384457010979576L;
	
	private CCControlComponent _myInfoPanel;
	public CCUIGridPane _myControlPane = null;
	
	private boolean _myIsSelected = false;
	
	private int _myDepth;
	
	private String _myName;
	
	protected CCObjectPropertyHandle _myProperty;
	
	private CCUILabel _myLabel;
	
	private CCPropertyListener<Object> _myListener;

	public CCObjectControl(CCObjectPropertyHandle thePropertyHandle, CCControlComponent theInfoPanel, int theDepth){
		_myProperty = thePropertyHandle;

		_myControlPane = new JPanel(new GridBagLayout());
		_myControlPane.background(CCColorMap.getColor(_myProperty.path()).brighter());
		_myProperty.events().add(_myListener = theValue ->{
			try{
				if(_myIsSelected){
					remove(_myControlPane);
				}
				createUI(false);
				if(_myIsSelected){
					GridBagConstraints myConstraints = new GridBagConstraints();
					myConstraints.gridx = 0;
					myConstraints.gridy = 1;
					myConstraints.gridwidth = 3;
					myConstraints.weightx = 1.0;
					myConstraints.anchor = GridBagConstraints.LINE_START;
					myConstraints.fill = GridBagConstraints.HORIZONTAL;
					add(_myControlPane, myConstraints);
					if(getParent() != null)getParent().revalidate(); 
					theInfoPanel.invalidate(); 
					theInfoPanel.validate(); // or ((JComponent) getContentPane()).revalidate();
					theInfoPanel.repaint();
					
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		});
		
		setBackground(CCColorMap.getColor(_myProperty.path()));
		
		_myDepth = theDepth;
		
		_myInfoPanel = theInfoPanel;
		setLayout(new GridBagLayout());
		
		_myName = _myProperty.name();
		
		GridBagConstraints myConstraints = new GridBagConstraints();
		myConstraints.gridx = 0;
		myConstraints.gridy = 0;
		myConstraints.gridwidth = 3;
		myConstraints.anchor = GridBagConstraints.LINE_START;
		myConstraints.fill = GridBagConstraints.HORIZONTAL;
		myConstraints.weightx = 1f;
		myConstraints.insets = new Insets(0, (5  + 10 * (_myDepth - 1)) * CCUIConstants.SCALE, 0, 5 * CCUIConstants.SCALE);
	
		_myLabel = new JLabel("[+] " + _myName, SwingConstants.LEFT);
		_myLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		_myLabel.setForeground(Color.WHITE);
		_myLabel.setFont(CCUIConstants.ARIAL_BOLD_10);
		_myLabel.setPreferredSize(new Dimension(100 * CCUIConstants.SCALE,15 * CCUIConstants.SCALE));
		
		_myLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent theE) {
				if(theE.getButton() == MouseEvent.BUTTON1){
					if(_myIsSelected){
						close();
					}else{
						open();
					}
				}
				if(theE.getButton() == MouseEvent.BUTTON3){
					popup().show(_myLabel, theE.getX(), theE.getY());
				}
			}
		});
		add(_myLabel, myConstraints);
		thePropertyHandle.addSelectionListener(isSelected -> {
			if(isSelected){
				setBorder(BorderFactory.createMatteBorder(0, 0, 0, 5, Color.red));
			}else{
				setBorder(BorderFactory.createEmptyBorder());
			}
		});
		
		if(CCControlApp.preferences.getBoolean(_myProperty.path().toString() + "/open" , false)){
			open();
		}
	}
	
	@Override
	public void dispose() {
		_myProperty.events().remove(_myListener);
		for(CCControl myControl:_myControls){
			myControl.dispose();
		}
	}
	
	public CCPropertyPopUp popup() {
		return new CCPropertyPopUp(CCObjectControl.this, _myProperty, _myInfoPanel);
	}
	
	private int _myGridY = 0;
	
	public void open() {
		createUI(false);
		
		GridBagConstraints myConstraints = new GridBagConstraints();
		myConstraints.gridx = 0;
		myConstraints.gridy = 1;
		myConstraints.gridwidth = 3;
		myConstraints.weightx = 1f;
		myConstraints.anchor = GridBagConstraints.LINE_START;
		myConstraints.fill = GridBagConstraints.HORIZONTAL;
		addChild(_myControlPane, myConstraints);
		
		_myInfoPanel.invalidate(); 
		_myInfoPanel.validate(); // or ((JComponent) getContentPane()).revalidate();
		_myInfoPanel.repaint();
		_myIsSelected = true;
		_myLabel.setText("[-] " + _myName);
		CCControlApp.preferences.put(_myProperty.path().toString() + "/open" , true + "");
	}
	
	public void close() {
		for(CCControl myControl:_myControls){
			myControl.dispose();
		}
		remove(_myControlPane);
		invalidate(); 
		validate(); // or ((JComponent) getContentPane()).revalidate();
		repaint();
		_myIsSelected = false;	
		_myLabel.setText("[+] " + _myName);
		CCControlApp.preferences.put(_myProperty.path().toString() + "/open" , false + "");
	}
	
	public CCUIGridPane controlComponent(){
		return _myControlPane;
	}
	
	private List<CCControl> _myControls = new ArrayList<>();
	
	private interface CCControlCreator{
		CCControl create(CCPropertyHandle<?> thePropertyHandle, CCControlComponent theInfoPanel);
	}
	
	private static Map<Class<?>, CCControlCreator> creatorMap = new HashMap<>();
	static{
		creatorMap.put(CCTriggerProgress.class, (myHandle, myInfoPanel) -> {return new CCEventTriggerControl((CCEventTriggerHandle)myHandle, myInfoPanel);});
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		CCControlCreator myNumberCreator = (myHandle, myInfoPanel) -> {return new CCNumberControl((CCNumberPropertyHandle)myHandle, myInfoPanel);};
		creatorMap.put(Float.class, myNumberCreator);
		creatorMap.put(Float.TYPE, myNumberCreator);
		creatorMap.put(Double.class, myNumberCreator);
		creatorMap.put(Double.TYPE, myNumberCreator);
		creatorMap.put(Integer.class, myNumberCreator);
		creatorMap.put(Integer.TYPE, myNumberCreator);
		
		CCControlCreator myBooleanCreator = (myHandle, myInfoPanel) -> {return new CCBooleanControl((CCBooleanPropertyHandle)myHandle, myInfoPanel);};
		creatorMap.put(Boolean.class, myBooleanCreator);
		creatorMap.put(Boolean.TYPE, myBooleanCreator);

		creatorMap.put(CCSelection.class, (myHandle, myInfoPanel) -> {return new CCSelectionControl((CCSelectionPropertyHandle)myHandle, myInfoPanel);});
		creatorMap.put(CCColor.class, (myHandle, myInfoPanel) -> {return new CCColorControl((CCColorPropertyHandle)myHandle, myInfoPanel);});
		creatorMap.put(CCGradient.class, (myHandle, myInfoPanel) -> {return new CCGradientControl((CCGradientPropertyHandle)myHandle, myInfoPanel);});
		creatorMap.put(String.class, (myHandle, myInfoPanel) -> {return new CCStringControl((CCStringPropertyHandle)myHandle, myInfoPanel);});
		creatorMap.put(CCControlMatrix.class, (myHandle, myInfoPanel) -> {return new CCControlMatrixControl((CCControlMatrixHandle)myHandle, myInfoPanel);});
		creatorMap.put(CCEnvelope.class, (myHandle, myInfoPanel) -> {return new CCEnvelopeControl((CCEnvelopeHandle)myHandle, myInfoPanel);});
		creatorMap.put(CCSplineHandle.class, (myHandle, myInfoPanel) -> {return new CCSplineControl((CCSplineHandle)myHandle, myInfoPanel);});
		creatorMap.put(Path.class, (myHandle, myInfoPanel) -> {return new CCPathControl((CCPathHandle)myHandle, myInfoPanel);});
		creatorMap.put(CCShaderSource.class, (myHandle, myInfoPanel) -> {return new CCShaderCompileControl((CCShaderSourceHandle)myHandle, myInfoPanel);});
	}
	
	private static CCControlCreator handleCreator(Class<?> theClass){
		if(theClass == null)return null;
		CCControlCreator myCreator = null;
		Class<?> myClass = theClass;
		do{
			myCreator = creatorMap.get(myClass);
			myClass = myClass.getSuperclass();
		}while(myClass != null && myCreator == null && myClass != Object.class);
		return myCreator;
	}
	
	private void createUI(boolean theHideUnchanged){
		_myControlPane.removeAll();
	
		for(CCPropertyHandle<?> myPropertyHandle:_myProperty.children().values()){
			if(theHideUnchanged && !myPropertyHandle.isChanged())continue;
			
			Class<?> myClass = myPropertyHandle.type();
			
			CCControl myControl;
			
			CCControlCreator myCreator = handleCreator(myClass);
			if(myCreator != null){
				myControl = myCreator.create(myPropertyHandle, _myInfoPanel);
			}else if(myClass == null){
				myControl = new CCEventTriggerControl((CCEventTriggerHandle)myPropertyHandle, _myInfoPanel);
			}else  if(myClass.isEnum()){
				myControl = new CCEnumControl((CCEnumPropertyHandle)myPropertyHandle, _myInfoPanel);
			}else{
				CCObjectPropertyHandle myObjectHandle = (CCObjectPropertyHandle)myPropertyHandle;
				CCObjectControl myObjectControl = new CCObjectControl(myObjectHandle, _myInfoPanel, _myDepth + 1);
				myControl = myObjectControl;
			}

			myControl.addToPane(_myControlPane, _myGridY, _myDepth + 1);
			_myControls.add(myControl);
			_myGridY++;
		}
	}
	
	public void hideUnchanged(){
		createUI(true);
	}
	
	public void showUnchanged(){
		createUI(false);
	}
	
	public CCObjectPropertyHandle propertyHandle(){
		return _myProperty;
	}
	
	@Override
	public String toString() {
		return _myProperty.name();
	}

	@Override
	public void addToPane(CCUIGridPane thePanel, int theY, int theDepth) {
		GridBagConstraints myConstraints = new GridBagConstraints();
		myConstraints.gridx = 0;
		myConstraints.gridwidth = 3;
		myConstraints.gridy = theY;
		myConstraints.weightx = 1d;
		myConstraints.anchor = GridBagConstraints.LINE_START;
		myConstraints.fill = GridBagConstraints.HORIZONTAL;
		thePanel.add(this, myConstraints);
	}
}

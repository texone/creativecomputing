package cc.creativecomputing.controlui.controls;

import java.awt.Color;
import java.awt.Component;
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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import cc.creativecomputing.control.CCControlMatrix;
import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.control.CCSelection;
import cc.creativecomputing.control.code.CCRealtimeCompile;
import cc.creativecomputing.control.code.CCRuntimeCompilable;
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
import cc.creativecomputing.control.handles.CCRealtimeCompileHandle;
import cc.creativecomputing.control.handles.CCRuntimeCompileHandle;
import cc.creativecomputing.control.handles.CCSelectionPropertyHandle;
import cc.creativecomputing.control.handles.CCShaderSourceHandle;
import cc.creativecomputing.control.handles.CCSplineHandle;
import cc.creativecomputing.control.handles.CCStringPropertyHandle;
import cc.creativecomputing.control.handles.CCTriggerProgress;
import cc.creativecomputing.controlui.CCColorMap;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.controlui.CCPropertyPopUp;
import cc.creativecomputing.controlui.controls.code.CCRealtimeCompileControl;
import cc.creativecomputing.controlui.controls.code.CCRuntimeCompileControl;
import cc.creativecomputing.controlui.controls.code.CCShaderCompileControl;
import cc.creativecomputing.controlui.timeline.view.SwingGuiConstants;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCReflectionUtil;
import cc.creativecomputing.math.CCColor;

public class CCObjectControl extends JPanel implements CCControl{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2697384457010979576L;
	
	private CCControlComponent _myInfoPanel;
	public JPanel _myControlComponent = null;
	
	private boolean _myIsSelected = false;
	
	private int _myDepth;
	
	private String _myName;
	
	protected CCObjectPropertyHandle _myProperty;
	
	private JLabel _myLabel;
	
	private CCPropertyListener<Object> _myListener;
	

	public CCObjectControl(CCObjectPropertyHandle thePropertyHandle, CCControlComponent theInfoPanel, int theDepth){
		_myProperty = thePropertyHandle;

		_myControlComponent = new JPanel(new GridBagLayout());
		_myControlComponent.setBackground(CCColorMap.getColor(_myProperty.path()).brighter());
		_myControlComponent.setBorder(BorderFactory.createEmptyBorder());
		_myProperty.events().add(_myListener = theValue ->{
			try{
				if(_myIsSelected){
					remove(_myControlComponent);
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
					add(_myControlComponent, myConstraints);
					getParent().revalidate();
					theInfoPanel.invalidate(); 
					theInfoPanel.validate(); // or ((JComponent) getContentPane()).revalidate();
					theInfoPanel.repaint();
					
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		});
		
		setBackground(CCColorMap.getColor(_myProperty.path()));
		setBorder(BorderFactory.createEmptyBorder());
		
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
		myConstraints.insets = new Insets(0, (5  + 10 * (_myDepth - 1)) * SwingGuiConstants.SCALE, 0, 5 * SwingGuiConstants.SCALE);
	
		_myLabel = new JLabel("[+] " + _myName, SwingConstants.LEFT);
		_myLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		_myLabel.setForeground(Color.WHITE);
		_myLabel.setFont(SwingGuiConstants.ARIAL_BOLD_10);
		_myLabel.setPreferredSize(new Dimension(100 * SwingGuiConstants.SCALE,15 * SwingGuiConstants.SCALE));
		
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
		add(_myControlComponent, myConstraints);
		
		_myInfoPanel.invalidate(); 
		_myInfoPanel.validate(); // or ((JComponent) getContentPane()).revalidate();
		_myInfoPanel.repaint();
		_myIsSelected = true;
		_myLabel.setText("[-] " + _myName);
	}
	
	public void close() {
		for(CCControl myControl:_myControls){
			myControl.dispose();
		}
		remove(_myControlComponent);
		invalidate(); 
		validate(); // or ((JComponent) getContentPane()).revalidate();
		repaint();
		_myIsSelected = false;	
		_myLabel.setText("[+] " + _myName);
	}
	
	public JPanel controlComponent(){
		return _myControlComponent;
	}
	
	private List<CCControl> _myControls = new ArrayList<>();
	
	private static interface CCControlCreator{
		public CCControl create(CCPropertyHandle<?> thePropertyHandle, CCControlComponent theInfoPanel);
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
		creatorMap.put(CCRealtimeCompile.class, (myHandle, myInfoPanel) -> {return new CCRealtimeCompileControl((CCRealtimeCompileHandle)myHandle, myInfoPanel);});
		creatorMap.put(CCShaderSource.class, (myHandle, myInfoPanel) -> {return new CCShaderCompileControl((CCShaderSourceHandle)myHandle, myInfoPanel);});
	}
	
	private static CCControlCreator handleCreator(Class<?> theClass){
		if(theClass == null)return null;
		CCControlCreator myCreator = null;
		Class<?> myClass = theClass;
		do{
			CCLog.info(myClass);
			myCreator = creatorMap.get(myClass);
			myClass = myClass.getSuperclass();
		}while(myClass != null && myCreator == null && myClass != Object.class);
		return myCreator;
	}
	
	private void createUI(boolean theHideUnchanged){
		_myControlComponent.removeAll();
	
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
			}else if(CCReflectionUtil.implementsInterface(myClass, CCRuntimeCompilable.class)){
				myControl = new CCRuntimeCompileControl((CCRuntimeCompileHandle)myPropertyHandle, _myInfoPanel);
			}else{
				CCObjectPropertyHandle myObjectHandle = (CCObjectPropertyHandle)myPropertyHandle;
				CCObjectControl myObjectControl = new CCObjectControl(myObjectHandle, _myInfoPanel, _myDepth + 1);
				myControl = myObjectControl;
			}

			myControl.addToComponent(_myControlComponent, _myGridY, _myDepth + 1);
			_myControls.add(myControl);
			_myGridY++;
		}
	}
	
	public void hideUnchanged(){
		createUI(true);
		_myInfoPanel.invalidate(); 
		_myInfoPanel.validate(); // or ((JComponent) getContentPane()).revalidate();
		_myInfoPanel.repaint();
	}
	
	public void showUnchanged(){
		createUI(false);
		_myInfoPanel.invalidate(); 
		_myInfoPanel.validate(); // or ((JComponent) getContentPane()).revalidate();
		_myInfoPanel.repaint();
	}
	
	public CCObjectPropertyHandle propertyHandle(){
		return _myProperty;
	}
	
	@Override
	public String toString() {
		return _myProperty.name();
	}

	@Override
	public void addToComponent(JPanel thePanel, int theY, int theDepth) {
		GridBagConstraints myConstraints = new GridBagConstraints();
		myConstraints.gridx = 0;
		myConstraints.gridwidth = 3;
		myConstraints.gridy = theY;
		myConstraints.weightx = 1d;
		myConstraints.anchor = GridBagConstraints.LINE_START;
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

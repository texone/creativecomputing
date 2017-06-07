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

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;

import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.control.CCSelection;
import cc.creativecomputing.control.code.CCRealtimeCompile;
import cc.creativecomputing.control.code.CCRuntimeCompilable;
import cc.creativecomputing.control.code.CCShaderObject;
import cc.creativecomputing.control.handles.CCBooleanPropertyHandle;
import cc.creativecomputing.control.handles.CCColorPropertyHandle;
import cc.creativecomputing.control.handles.CCEnumPropertyHandle;
import cc.creativecomputing.control.handles.CCEnvelopeHandle;
import cc.creativecomputing.control.handles.CCEventTriggerHandle;
import cc.creativecomputing.control.handles.CCGradientPropertyHandle;
import cc.creativecomputing.control.handles.CCNumberPropertyHandle;
import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.control.handles.CCPathHandle;
import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.control.handles.CCRealtimeCompileHandle;
import cc.creativecomputing.control.handles.CCRuntimeCompileHandle;
import cc.creativecomputing.control.handles.CCSelectionPropertyHandle;
import cc.creativecomputing.control.handles.CCShaderCompileHandle;
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
	private JPanel _myControlComponent = null;
	
	private boolean _myIsSelected = false;
	
	private int _myDepth;
	
	private String _myName;
	protected CCPropertyPopUp _myPopUp;
	
	protected CCObjectPropertyHandle _myProperty;

	public CCObjectControl(CCObjectPropertyHandle thePropertyHandle, CCControlComponent theInfoPanel, int theDepth){
		_myProperty = thePropertyHandle;
		
		_myProperty.events().add(theValue ->{
			try{
				if(_myIsSelected){
					remove(_myControlComponent);
				}
				createUI();
				if(_myIsSelected){
					GridBagConstraints myConstraints = new GridBagConstraints();
					myConstraints.gridx = 0;
					myConstraints.gridy = 1;
					myConstraints.gridwidth = 3;
					myConstraints.anchor = GridBagConstraints.LINE_START;
					myConstraints.fill = GridBagConstraints.HORIZONTAL;
					add(_myControlComponent, myConstraints);
					getParent().revalidate();
//					theInfoPanel.invalidate(); 
//					theInfoPanel.validate(); // or ((JComponent) getContentPane()).revalidate();
//					theInfoPanel.repaint();
					
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
		
		_myPopUp = new CCPropertyPopUp(_myProperty, theInfoPanel);
		
		GridBagConstraints myConstraints = new GridBagConstraints();
		myConstraints.gridx = 0;
		myConstraints.gridy = 0;
		myConstraints.gridwidth = 3;
		myConstraints.anchor = GridBagConstraints.LINE_START;
		myConstraints.fill = GridBagConstraints.HORIZONTAL;
		myConstraints.weightx = 1f;
		myConstraints.insets = new Insets(0, (5  + 10 * (_myDepth - 1)) * SwingGuiConstants.SCALE, 0, 5 * SwingGuiConstants.SCALE);
		JLabel myLabel = new JLabel("[+] " + _myName, SwingConstants.LEFT);
		myLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		myLabel.setForeground(Color.WHITE);
		myLabel.setFont(SwingGuiConstants.ARIAL_BOLD_10);
		myLabel.setPreferredSize(new Dimension(100 * SwingGuiConstants.SCALE,15 * SwingGuiConstants.SCALE));
		
		myLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent theE) {
				JLabel myLabel = (JLabel)theE.getSource();
				if(theE.getButton() == MouseEvent.BUTTON1){
					if(_myIsSelected){
						remove(_myControlComponent);
						invalidate(); 
						validate(); // or ((JComponent) getContentPane()).revalidate();
						repaint();
						_myIsSelected = false;	
						myLabel.setText("[+] " + _myName);
					}else{
						GridBagConstraints myConstraints = new GridBagConstraints();
						myConstraints.gridx = 0;
						myConstraints.gridy = 1;
						myConstraints.gridwidth = 3;
						myConstraints.anchor = GridBagConstraints.LINE_START;
						myConstraints.fill = GridBagConstraints.HORIZONTAL;
						add(_myControlComponent, myConstraints);
						
						invalidate(); 
						validate(); // or ((JComponent) getContentPane()).revalidate();
						repaint();
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
		thePropertyHandle.addSelectionListener(isSelected -> {
			if(isSelected){
				setBorder(BorderFactory.createMatteBorder(0, 0, 0, 5, Color.red));
			}else{
				setBorder(BorderFactory.createEmptyBorder());
			}
		});
	}
	
	private int _myGridY = 0;
	
	public JPanel controlComponent(){
		return _myControlComponent;
	}
	
	private DefaultMutableTreeNode _myParentNode;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createUI(){
		
		_myControlComponent = new JPanel(new GridBagLayout());
		_myControlComponent.setBackground(CCColorMap.getColor(_myProperty.path()).brighter());
		
		for(CCPropertyHandle<?> myPropertyHandle:_myProperty.children().values()){
			Class<?> myClass = myPropertyHandle.type();
			
			CCControl myControlPanel;
			if(myClass == null){
				myControlPanel = new CCEventTriggerControl((CCEventTriggerHandle)myPropertyHandle, _myInfoPanel);
			}else if(myClass == CCTriggerProgress.class){
				myControlPanel = new CCEventTriggerControl((CCEventTriggerHandle)myPropertyHandle, _myInfoPanel);
			}else if(myClass == Float.class || myClass == Float.TYPE){
				myControlPanel = new CCNumberControl((CCNumberPropertyHandle)myPropertyHandle, _myInfoPanel);
			}else if(myClass == Double.class || myClass == Double.TYPE){
				myControlPanel = new CCNumberControl((CCNumberPropertyHandle)myPropertyHandle, _myInfoPanel);
			}else  if(myClass == Integer.class || myClass == Integer.TYPE){
				myControlPanel = new CCNumberControl((CCNumberPropertyHandle)myPropertyHandle, _myInfoPanel);
			}else  if(myClass == Boolean.class || myClass == Boolean.TYPE){
				myControlPanel = new CCBooleanControl((CCBooleanPropertyHandle)myPropertyHandle, _myInfoPanel);
			}else  if(myClass.isEnum()){
				myControlPanel = new CCEnumControl((CCEnumPropertyHandle)myPropertyHandle, _myInfoPanel);
			}else  if(myClass == CCSelection.class){
				myControlPanel = new CCSelectionControl((CCSelectionPropertyHandle)myPropertyHandle, _myInfoPanel);
			}else  if(myClass == CCColor.class){
				myControlPanel = new CCColorControl((CCColorPropertyHandle)myPropertyHandle, _myInfoPanel);
			}else  if(myClass == CCGradient.class){
				myControlPanel = new CCGradientControl((CCGradientPropertyHandle)myPropertyHandle, _myInfoPanel);
			}else  if(myClass == String.class){
				myControlPanel = new CCStringControl((CCStringPropertyHandle)myPropertyHandle, _myInfoPanel);
			}else  if(myClass == CCEnvelope.class){
				myControlPanel = new CCEnvelopeControl((CCEnvelopeHandle)myPropertyHandle, _myInfoPanel);
			}else  if(myPropertyHandle.getClass() == CCSplineHandle.class){
				myControlPanel = new CCSplineControl((CCSplineHandle)myPropertyHandle, _myInfoPanel);
			}else  if(myClass == Path.class){
				myControlPanel = new CCPathControl((CCPathHandle)myPropertyHandle, _myInfoPanel);
			}else  if(myClass == CCRealtimeCompile.class){
				myControlPanel = new CCRealtimeCompileControl((CCRealtimeCompileHandle)myPropertyHandle, _myInfoPanel);
			}else  if(myClass == CCShaderObject.class){
				myControlPanel = new CCShaderCompileControl((CCShaderCompileHandle)myPropertyHandle, _myInfoPanel);
			}else if(CCReflectionUtil.implementsInterface(myClass, CCRuntimeCompilable.class)){
				myControlPanel = new CCRuntimeCompileControl((CCRuntimeCompileHandle)myPropertyHandle, _myInfoPanel);
			}else{
				CCObjectPropertyHandle myObjectHandle = (CCObjectPropertyHandle)myPropertyHandle;
				CCObjectControl myObjectControl = new CCObjectControl(myObjectHandle, _myInfoPanel, _myDepth + 1);
				myControlPanel = myObjectControl;
				
				DefaultMutableTreeNode myObjectNode = new DefaultMutableTreeNode(myObjectControl);
				_myParentNode.add(myObjectNode);
				myObjectControl.createUI(myObjectNode);
			}

			myControlPanel.addToComponent(_myControlComponent, _myGridY, _myDepth + 1);
			_myGridY++;
		}
	}
	
	public void createUI(DefaultMutableTreeNode theParentNode){
		_myParentNode = theParentNode;
		createUI();
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

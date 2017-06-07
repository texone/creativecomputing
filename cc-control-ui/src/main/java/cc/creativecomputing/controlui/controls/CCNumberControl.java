package cc.creativecomputing.controlui.controls;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import cc.creativecomputing.control.handles.CCNumberPropertyHandle;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.controlui.CCSwingDraggableValueBox;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.math.CCMath;
import net.objecthunter.exp4j.ExpressionBuilder;

public class CCNumberControl extends CCValueControl<Number, CCNumberPropertyHandle<Number>>{
	
	private static final int MAX_SLIDER_VALUE = 1000;
	
	private double _myMin;
	private double _myMax;
	private double _myValue;
	
	private CCSwingDraggableValueBox _myValueField;
	private JSlider _mySlider;
	
	private boolean _myTriggerEvent = true;

	public CCNumberControl(CCNumberPropertyHandle<Number> theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
 
        //Create the label.
		theHandle.events().add(theValue -> {
			_myTriggerEvent = false;
			_myValue = ((Number)theValue).doubleValue();
			updateSlider(_myValue);
			if(_myHandle.numberType() == Integer.class){
				_myValueField.setText((int)_myValue + "");
			}else{
				_myValueField.setText(CCFormatUtil.nd((float)_myValue, theHandle.digits()) + "");
			}
			_myTriggerEvent = true;
			
		});
 
        //Create the slider.
        _myMin = _myHandle.min().doubleValue();
        _myMax = _myHandle.max().doubleValue();
        _myValue = _myHandle.value().doubleValue();
        if(_myHandle.isNumberBox()){
        	_mySlider = null;
        }else{
	        _mySlider = new JSlider(JSlider.HORIZONTAL, 0, MAX_SLIDER_VALUE, 0);
	        _mySlider.addChangeListener(theE -> {
	        	if(!_myTriggerEvent)return;
	        	value((float)(_mySlider.getValue() / (float)MAX_SLIDER_VALUE * (_myMax - _myMin) + _myMin), true);
			});
	 
	        //Turn on labels at major tick marks.
	 
	        _mySlider.setMajorTickSpacing(MAX_SLIDER_VALUE / 10);
	        _mySlider.setMinorTickSpacing(MAX_SLIDER_VALUE / 20);
	        
	        _mySlider.setPaintTicks(false);
	        _mySlider.setPaintLabels(false);
	        _mySlider.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
	        _mySlider.putClientProperty( "JComponent.sizeVariant", "mini" );
	        _mySlider.setPreferredSize(new Dimension(100,14));
        }
        
        _myValueField = new CCSwingDraggableValueBox(_myValue, _myMin, _myMax, CCMath.pow(0.1, theHandle.digits()));
        CCUIStyler.styleTextField(_myValueField, 100);
        
        _myValueField.changeEvents().add(theValue -> {
        	value(theValue, true);
        });
        _myValueField.addActionListener(theE -> {
        	try{
        		value(new ExpressionBuilder(_myValueField.getText()).build().evaluate(), true);
        	}catch(Exception e){
        		value((float)(_mySlider.getValue() / (float)MAX_SLIDER_VALUE * (_myMax - _myMin) + _myMin), true);
        	}
		});
        _myValueField.addKeyListener(new KeyAdapter() {
        	@Override
        	public void keyReleased(KeyEvent e) {
        		switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					value(value().doubleValue() + 1, true);
					break;
				case KeyEvent.VK_DOWN:
					value(value().doubleValue() - 1, true);
					break;

				default:
					break;
				}
        	}
		});
        _myValueField.setHorizontalAlignment(JTextField.LEFT);
        value(_myHandle.value().doubleValue(), true);
	}
	
	@Override
	public void addToComponent(JPanel thePanel, int theY, int theDepth) {
		thePanel.add(_myLabel, constraints(0, theY, GridBagConstraints.LINE_END,5, 5, 1, 5));
		if(_mySlider != null){
			thePanel.add(_mySlider, constraints(1, theY, GridBagConstraints.PAGE_END,5, 0, 1, 5));
			_myValueField.setPreferredSize(new Dimension(68, 13));
			thePanel.add(_myValueField, constraints(2, theY, GridBagConstraints.LINE_START,5, 5, 1, 5));
		}else{
			thePanel.add(_myValueField, constraints(1, theY, GridBagConstraints.LINE_START,5, 5, 1, 5));
		}
	}
	
	public Number value(){
		return _myValue;
	}
	
	public void value(double theValue, boolean theOverWrite){
		_myValue = theValue;
		_myHandle.value(_myValue, theOverWrite);
	}
	
	private void updateSlider(double theValue){
		if(_mySlider == null)return;
		double myValue = CCMath.constrain((theValue - _myMin) / (_myMax - _myMin) * MAX_SLIDER_VALUE, 0, MAX_SLIDER_VALUE);
		_mySlider.setValue((int)myValue);
	}

}

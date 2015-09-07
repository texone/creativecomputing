package cc.creativecomputing.controlui.controls;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.control.handles.CCEnvelopeHandle;
import cc.creativecomputing.control.handles.CCPropertyListener;
import cc.creativecomputing.control.timeline.TrackData;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.controlui.timeline.view.SwingCurveFrame;

public class CCEnvelopeControl extends CCValueControl<CCEnvelope, CCEnvelopeHandle>{

	private JButton _myButton;
	
	private SwingCurveFrame _myCurveFrame;
	
	private JComboBox<String> _myCurveList;
	private ComboBoxEditor _myEditor;
	
	private JButton _myRemoveButton;
	
	private JPanel _myContainer;
	
	private String _myCurveKey = null;
	private TrackData _myEnvelopeCurve = null;

	public CCEnvelopeControl(CCEnvelopeHandle theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
		
		theHandle.events().add(new CCPropertyListener<CCEnvelope>() {
			
			@Override
			public void onChange(CCEnvelope theValue) {
				_myHandle.value().curves(theValue.curves());
				_myCurveList.removeAllItems();
				for(String myKey:theValue.curves().keySet()){
					_myCurveList.addItem(myKey);
				}
			}
		});
		
		_myCurveFrame = new SwingCurveFrame();
		_myCurveFrame.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent theE) {
				value().currentCurve(_myEnvelopeCurve);
			}
		});
 
        //Create the Button.
		
		_myContainer = new JPanel();
        ((FlowLayout)_myContainer.getLayout()).setVgap(0);
        ((FlowLayout)_myContainer.getLayout()).setHgap(0);
       
        _myCurveList = new JComboBox<String>();
        _myCurveList.setEditable(true);
      
        _myCurveList.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent theE) {
				switch(theE.getStateChange()){
				case ItemEvent.SELECTED:
					if(_myCurveList.getSelectedItem() == null)return;
					if(_myCurveList.getSelectedItem().equals(""))return;
					setEnvelope(_myCurveList.getSelectedItem().toString());
					break;
				}
			}
		});
        CCUIStyler.styleCombo(_myCurveList);
        _myEditor = _myCurveList.getEditor();
        _myEditor.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent theE) {
				addEnvelope(_myEditor.getItem().toString());
			}
		});
        
        _myContainer.add(_myCurveList);
        
        _myButton = new JButton("edit");
        _myButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent theE) {
				if(_myCurveKey == null){
					_myCurveFrame.track().trackData(new TrackData(null));
				}else{
					_myCurveFrame.track().trackData(value().curves().get(_myCurveKey));
				}

				_myEnvelopeCurve = value().curves().get(_myCurveKey);
				value().currentCurve(_myCurveFrame.track().trackData());
				
				_myCurveFrame.setVisible(true);
			}
		});
        CCUIStyler.styleButton(_myButton, 30, 15);
        _myContainer.add(_myButton);
        
        _myRemoveButton = new JButton("remove");
        CCUIStyler.styleButton(_myRemoveButton, 40, 15);
        _myRemoveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent theE) {
//				removePreset();
			}
		});
        _myContainer.add(_myRemoveButton);
	}
	
	private void setEnvelope(String theKey){
		_myCurveKey = theKey;
		if(value().curves().get(_myCurveKey) == null){
			return;
		}else{
			_myCurveFrame.track().trackData(value().curves().get(_myCurveKey));
			value().currentCurve(_myCurveFrame.track().trackData());
//			if(_myCurveFrame.isVisible()){
//				value().currentCurve(_myCurveFrame.track().trackData());
//			}
		}

		_myCurveFrame.panel().updateView();
	}
	
	private boolean addEnvelope(String theKey){
		if(containsItem(theKey))return false;

		_myCurveList.addItem(theKey);
		if(_myCurveKey == null){
			value().curves().put(theKey, _myCurveFrame.track().trackData());
		}else{
			value().curves().put(theKey, new TrackData(null));
		}
		
		if(value().currentCurve() == null){
			value().curve(theKey);
		}
	
		_myCurveKey = theKey;
		_myCurveFrame.track().trackData(value().curves().get(_myCurveKey));
		_myCurveFrame.panel().updateView();
		return true;
	}
	
	public void removeEnvelope(){
		_myCurveList.removeItem(_myCurveList.getSelectedItem());
	}
	
	private boolean containsItem(Object theItem){
		for(int i = 0; i < _myCurveList.getItemCount();i++){
			if(_myCurveList.getItemAt(i) != null && _myCurveList.getItemAt(i).equals(theItem))return true;
		}
		return false;
	}
	
	
	@Override
	public void addToComponent(JPanel thePanel, int theY, int theDepth) {
		thePanel.add(_myLabel, 	constraints(0, theY, GridBagConstraints.LINE_END, 	5,  5, 1, 5));
		thePanel.add(_myContainer, constraints(1, theY, 2, GridBagConstraints.LINE_START, 5, 15, 1, 5));
	}

	@Override
	public CCEnvelope value() {
		// TODO Auto-generated method stub
		return _myHandle.value();
	}
}

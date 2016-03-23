package cc.creativecomputing.controlui;

import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.controlui.controls.CCControl;

public class CCTreeNodeUserObject {

	private JPanel _myControlComponent = null;
	
	private CCObjectPropertyHandle _myHandle;
	
	public CCTreeNodeUserObject(CCObjectPropertyHandle theName){
		_myHandle = theName;
	}
	
	private int _myGridY = 0;
	
	public void add(CCControl theComponent, int theDepth){
		if(_myControlComponent == null){
			_myControlComponent = new JPanel(new GridBagLayout());
//			_myControlComponent.setPreferredSize(new Dimension(600, 10));
			_myControlComponent.setBackground(CCColorMap.getColor(_myHandle.path()));
		}
		theComponent.addToComponent(_myControlComponent, _myGridY, theDepth);
		_myGridY++;
	}
	
	public JPanel controlComponent(){
		return _myControlComponent;
	}
	
	public CCObjectPropertyHandle propertyHandle(){
		return _myHandle;
	}
	
	@Override
	public String toString() {
		return _myHandle.name();
	}
}

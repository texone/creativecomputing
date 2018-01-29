package cc.creativecomputing.controlui.patch;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;

public class CCNode{
	
	public String name;
	
	public List<CCPinInput> inputs = new ArrayList<>();
	
	public CCPinOutput output;
	
	public CCNode(String theName, CCPinOutput theOuput){
		name = theName;
		output = theOuput;
	}

	public void update(CCAnimator theAnimator){
		
	}
}

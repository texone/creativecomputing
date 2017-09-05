package cc.creativecomputing.controlui.patch;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;

public class CCNode{
	
	public String name;
	
	public List<CCNodeInput> inputs = new ArrayList<>();
	
	public CCNodeOutput output;
	
	public CCNode(String theName, CCNodeOutput theOuput){
		name = theName;
		output = theOuput;
	}

	public void update(CCAnimator theAnimator){
		
	}
}

package cc.creativecomputing.controlui.patch;

import cc.creativecomputing.core.CCProperty;

public class CCNodeConstant extends CCNode{
	
	@CCProperty(name = "constant")
	private double _cConstant = 0;

	public CCNodeConstant(String theName, CCNodeOutput theOuput) {
		super(theName, theOuput);
	}

}

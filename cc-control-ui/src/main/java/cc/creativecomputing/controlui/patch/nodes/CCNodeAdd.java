package cc.creativecomputing.controlui.patch.nodes;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.controlui.patch.CCNode;
import cc.creativecomputing.controlui.patch.CCNodeOutput;

public class CCNodeAdd extends CCNode{

	public CCNodeAdd(CCNodeOutput<Double> theOuput) {
		super("+", theOuput);
	}

	@Override
	public void update(CCAnimator theAnimator) {
		// TODO Auto-generated method stub
		super.update(theAnimator);
	}
}

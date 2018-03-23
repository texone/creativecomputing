package cc.creativecomputing.core;

import cc.creativecomputing.core.CCEventManager.CCEvent;

public interface CCSelectable {

	boolean isSelected();
	
	void select(boolean theSelect);
	
	void addListener(CCEvent<Boolean> theListener);
}

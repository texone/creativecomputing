package cc.creativecomputing.core;

public interface CCSelectable {

	boolean isSelected();
	
	void select(boolean theSelect);
	
	void addListener(CCSelectionListener theListener);
}

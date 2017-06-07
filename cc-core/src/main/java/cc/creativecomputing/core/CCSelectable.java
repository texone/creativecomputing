package cc.creativecomputing.core;

public interface CCSelectable {

	public boolean isSelected();
	
	public void select(boolean theSelect);
	
	public void addListener(CCSelectionListener theListener);
}

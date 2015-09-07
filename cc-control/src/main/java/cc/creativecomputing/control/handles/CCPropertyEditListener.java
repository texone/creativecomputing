package cc.creativecomputing.control.handles;

public interface CCPropertyEditListener {

	public void beginEdit(CCPropertyHandle<?> theProperty);
	
	public void endEdit(CCPropertyHandle<?> theProperty);
}

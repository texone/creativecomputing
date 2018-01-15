package cc.creativecomputing.control.handles;

public interface CCPropertyEditListener {

	void beginEdit(CCPropertyHandle<?> theProperty);
	
	void endEdit(CCPropertyHandle<?> theProperty);
}

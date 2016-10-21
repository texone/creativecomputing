package cc.creativecomputing.io.markup;

public class CCMarkUpParagraphElement extends CCMarkUpElement {

    @Override
    public boolean canClean() {
        return _myChildren.isEmpty();
    }
    
    @Override
    public String toString() {
    	StringBuffer myResult = new StringBuffer();
    	myResult.append("\n");
    	myResult.append(super.toString());
    	myResult.append("\n");
    	myResult.append("\n");
    	return myResult.toString();
    }
}

package cc.creativecomputing.io.markup;

public class CCMarkUpListElement extends CCMarkUpElement {

	public static enum CCMarkupListStyle{
		UNORDERED,
		ORDERED
	}
    
    private final CCMarkupListStyle _myStyle;

    public CCMarkupListStyle style() {
        return _myStyle;
    }

    public CCMarkUpListElement() {
        this(CCMarkupListStyle.UNORDERED);
    }

    public CCMarkUpListElement(CCMarkupListStyle theStyle) {
        _myStyle = theStyle;
    }

    @Override
    public CCMarkUpElement add(CCMarkUpElement theElement) {
    	if(theElement.getClass() != CCMarkUpListItemElement.class)throw new IllegalArgumentException("Illegal child");
        return super.add(theElement);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + _myStyle.ordinal();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CCMarkUpListElement other = (CCMarkUpListElement) obj;
        if (_myStyle != other._myStyle) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
    	StringBuffer myResult = new StringBuffer();
    	myResult.append("\n");
    	for(CCMarkUpElement myElement:_myChildren){
    		if(_myStyle == CCMarkupListStyle.ORDERED){
    			myResult.append("# ");
    		}else{
    			myResult.append("* ");
    		}
    		myResult.append(myElement.toString());
        	myResult.append("\n");
    	}
    	myResult.append("\n");
    	return myResult.toString();
    }
}

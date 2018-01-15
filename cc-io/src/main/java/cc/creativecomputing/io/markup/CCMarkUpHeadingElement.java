package cc.creativecomputing.io.markup;

public class CCMarkUpHeadingElement extends CCMarkUpTextElement {

    private final int _myLevel;

    public int level() {
        return _myLevel;
    }

    public CCMarkUpHeadingElement(int level, String text) {
        super(text);
        _myLevel = level;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + _myLevel;
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
        CCMarkUpHeadingElement other = (CCMarkUpHeadingElement) obj;
        return _myLevel == other._myLevel;
    }

    @Override
    public String toString() {
    	StringBuffer myResult = new StringBuffer();
    	for(int i = 0; i < _myLevel;i++){
    		myResult.append("=");
    	}
		myResult.append(" ");
		myResult.append(value());
		myResult.append("\n");
        return myResult.toString();
    }
}

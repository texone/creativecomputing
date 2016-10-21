package cc.creativecomputing.io.markup;

public class CCMarkUpPreformattedElement extends CCMarkUpTextElement {

    private final boolean _myIsInline;

    public boolean isInline() {
        return _myIsInline;
    }

    public CCMarkUpPreformattedElement(String theValue) {
        this(false, theValue);
    }

    public CCMarkUpPreformattedElement(boolean theIsInline, String theValue) {
        super(theValue);
        _myIsInline = theIsInline;
    }
}

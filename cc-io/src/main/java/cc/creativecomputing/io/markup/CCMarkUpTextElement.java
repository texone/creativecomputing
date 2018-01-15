package cc.creativecomputing.io.markup;

public class CCMarkUpTextElement extends CCMarkUpElement {

    private final String _myValue;

    public CCMarkUpTextElement(Character theCharacter) {
        _myValue = theCharacter.toString();
    }

    public CCMarkUpTextElement(String theText) {
        _myValue = theText;
    }

    public String value() {
        return _myValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((_myValue == null) ? 0 : _myValue.hashCode());
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
        CCMarkUpTextElement other = (CCMarkUpTextElement) obj;
        if (_myValue == null) {
            return other._myValue == null;
        } else return _myValue.equals(other._myValue);
    }

    @Override
    public String toString() {
        return _myValue;
    }
}

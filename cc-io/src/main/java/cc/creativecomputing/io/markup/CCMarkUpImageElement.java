package cc.creativecomputing.io.markup;

public class CCMarkUpImageElement extends CCMarkUpElement {

    private String _mySource;
    private String _myText;

    public CCMarkUpImageElement(String src, String text) {
        _mySource = src;
        _myText = text;
    }

    public String source() {
        return _mySource;
    }

    public String text() {
        return _myText;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((_mySource == null) ? 0 : _mySource.hashCode());
        result = prime * result + ((_myText == null) ? 0 : _myText.hashCode());
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
        CCMarkUpImageElement other = (CCMarkUpImageElement) obj;
        if (_mySource == null) {
            if (other._mySource != null) {
                return false;
            }
        } else if (!_mySource.equals(other._mySource)) {
            return false;
        }
        if (_myText == null) {
            return other._myText == null;
        } else return _myText.equals(other._myText);
    }

    @Override
    public String toString() {
        return "Image [source=" + _mySource + ", text=" + _myText + "]";
    }
}

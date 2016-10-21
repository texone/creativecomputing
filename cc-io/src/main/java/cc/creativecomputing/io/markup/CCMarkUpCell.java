package cc.creativecomputing.io.markup;

public class CCMarkUpCell extends CCMarkUpElement {

    private boolean _myHeading = false;

    public CCMarkUpCell() {
    }

    public CCMarkUpCell(String theText) {
        this(false, theText);
    }

    public CCMarkUpCell(boolean theHeading, String theText) {
        _myHeading = theHeading;
        add(new CCMarkUpTextElement(theText));
    }

    public boolean isHeading() {
        return _myHeading;
    }

    public void setHeading(boolean heading) {
        _myHeading = heading;
    }

    public void trim() {

        for (int i = _myChildren.size() - 1; i >= 0; i--) {

            CCMarkUpElement elem = _myChildren.get(i);

            if (!elem.getClass().isAssignableFrom(CCMarkUpTextElement.class)) {
                break;
            }

            CCMarkUpTextElement txt = (CCMarkUpTextElement) elem;

            if (!" ".equals(txt.value())) {
                break;
            }


            _myChildren.remove(i);
        }

    }
}

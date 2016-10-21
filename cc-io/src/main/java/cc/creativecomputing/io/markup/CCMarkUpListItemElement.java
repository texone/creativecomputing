package cc.creativecomputing.io.markup;

public class CCMarkUpListItemElement extends CCMarkUpElement {

    public CCMarkUpListItemElement() {}

    public CCMarkUpListItemElement(String text) {
        add(new CCMarkUpTextElement(text));
    }

}

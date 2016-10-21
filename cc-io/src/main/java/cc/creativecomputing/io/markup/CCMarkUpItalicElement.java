package cc.creativecomputing.io.markup;

public class CCMarkUpItalicElement extends CCMarkUpElement {

	public CCMarkUpItalicElement() {
	}

	public CCMarkUpItalicElement(CCMarkUpElement theElement) {
		add(theElement);
	}

	public CCMarkUpItalicElement(String theText) {
		add(new CCMarkUpTextElement(theText));
	}
	
	 @Override
	 public String toString() {
		 StringBuffer myResult = new StringBuffer();
		 myResult.append("//");
		 myResult.append(super.toString());
		 myResult.append("//");
		 return myResult.toString();
	 }
}

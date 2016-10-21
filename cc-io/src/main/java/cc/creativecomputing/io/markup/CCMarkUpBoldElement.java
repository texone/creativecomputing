package cc.creativecomputing.io.markup;

public class CCMarkUpBoldElement extends CCMarkUpElement {

	public CCMarkUpBoldElement() {
	}

	public CCMarkUpBoldElement(CCMarkUpElement theElement) {
		add(theElement);
	}

	public CCMarkUpBoldElement(String theText) {
		add(new CCMarkUpTextElement(theText));
	}
	
	@Override
	 public String toString() {
		 StringBuffer myResult = new StringBuffer();
		 myResult.append("**");
		 myResult.append(super.toString());
		 myResult.append("**");
		 return myResult.toString();
	 }
}

package cc.creativecomputing.io.markup;

public class CCMarkUpLinkElement extends CCMarkUpElement {


	private String _myTarget;
	
	
	public CCMarkUpLinkElement(String theLabel, String theTarget) {
		super();
		_myTarget = theTarget;

		if(theLabel != null && !theLabel.trim().equals(""))add(new CCMarkUpTextElement(theLabel));
	}

	public CCMarkUpLinkElement(String theTarget) {
		this(null, theTarget);
	}

	public String target() {
		return _myTarget;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((_myTarget == null) ? 0 : _myTarget.hashCode());
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
		CCMarkUpLinkElement other = (CCMarkUpLinkElement) obj;
		if (_myTarget == null) {
            return other._myTarget == null;
		} else return _myTarget.equals(other._myTarget);
    }
	
	@Override
	public String toString() {
		StringBuffer myResult = new StringBuffer();
		myResult.append("[[");
		myResult.append(_myTarget);
		if(_myChildren.size() > 0){
			myResult.append("|");
			myResult.append(_myChildren.get(0).toString());
		}
		myResult.append("]]");
		
		return myResult.toString();
	}
}

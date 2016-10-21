package cc.creativecomputing.io.markup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class CCMarkUpElement implements Iterable<CCMarkUpElement>{

    protected CCMarkUpElement _myParent;
    protected List<CCMarkUpElement> _myChildren = new ArrayList<>();

    public CCMarkUpElement getParent() {
        return _myParent;
    }

    public CCMarkUpElement add(CCMarkUpElement theElement) {
        _myChildren.add(theElement);
        theElement._myParent = this;

        return this;
    }

    public CCMarkUpElement addAll(CCMarkUpElement[] theElements) {
        return addAll(Arrays.asList(theElements));
    }

    public CCMarkUpElement addAll(Collection<? extends CCMarkUpElement> theElements) {
        _myChildren.addAll(theElements);
        for (CCMarkUpElement myElement : theElements) {
        	myElement._myParent = this;
        }

        return this;
    }

    public void removeElement(CCMarkUpElement theElement) {
        _myChildren.remove(theElement);
    }

    public Iterator<CCMarkUpElement> iterator() {
        return _myChildren.listIterator();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((_myChildren == null) ? 0 : _myChildren.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CCMarkUpElement other = (CCMarkUpElement) obj;
        if (_myChildren == null) {
            if (other._myChildren != null) {
                return false;
            }
        } else if (!_myChildren.equals(other._myChildren)) {
            return false;
        }
        if (_myParent == null) {
            if (other._myParent != null) {
                return false;
            }
        }
        return true;
    }

    public void clean() {
        Iterator<CCMarkUpElement> it = _myChildren.iterator();
        while (it.hasNext()) {
            CCMarkUpElement theElement = it.next();

            if (theElement.canClean()) {
                it.remove();
            } else {
                theElement.clean();
            }
        }
    }

    public boolean canClean() {
        return false;
    }

    @Override
    public String toString() {
    	StringBuffer myResult = new StringBuffer();
    	for(CCMarkUpElement myElement:_myChildren){
    		myResult.append(myElement.toString());
    	}
        return myResult.toString();
    }
}

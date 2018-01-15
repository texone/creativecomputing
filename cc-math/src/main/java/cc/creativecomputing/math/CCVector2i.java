package cc.creativecomputing.math;

public class CCVector2i {

	public int x;
	public int y;
	
	public CCVector2i(int theX, int theY){
		x = theX;
		y = theY;
	}
	
	public CCVector2i(double theX, double theY){
		x = (int)theX;
		y = (int)theY;
	}
	
	public CCVector2i(){
		this(0,0);
	}
	
	public CCVector2i(int i) {
		this(i,i);
	}

	public CCVector2i(CCVector2i theVector) {
		this(theVector.x, theVector.y);
	}

	@Override
    public String toString() {
        return getClass().getName() + " [X=" + x + ", Y=" + y + "]";
    }

	public void set(CCVector2i pos) {
		x = pos.x;
		y = pos.y;
	}
	
	public void set(int theIndex, int theValue){
		if(theIndex == 0)x = theValue;
		if(theIndex == 1)y = theValue;
	}
	
	public void add(int theIndex, int theValue){
		if(theIndex == 0)x += theValue;
		if(theIndex == 1)y += theValue;
	}
	
	public int get(int theIndex){
		if(theIndex == 0)return x;
		if(theIndex == 1)return y;
		return 0;
	}
	
	public CCVector2i add(CCVector2i theVector){
		return new CCVector2i(x + theVector.x, y + theVector.y);
	}

	public void addLocal(CCVector2i theVector) {
		x += theVector.x;
		y += theVector.y;
	}

	public CCVector2i subtract(CCVector2i theVector) {
		return new CCVector2i(x - theVector.x, y - theVector.y);
	}

	public void subtractLocal(CCVector2i theVector) {
		x -= theVector.x;
		y -= theVector.y;
	}

	public CCVector2i cwiseMax(CCVector2i theVector) {
		return new CCVector2i(CCMath.max(x, theVector.x), CCMath.max(y, theVector.y));
	}

	public CCVector2i cwiseMin(CCVector2i theVector) {
		return new CCVector2i(CCMath.min(x, theVector.x), CCMath.min(y, theVector.y));
	}

	public boolean isZero() {
		return x == 0 && y == 0;
	}
}

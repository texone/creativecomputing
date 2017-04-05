package cc.creativecomputing.core;

public class CCPropertyObject implements CCProperty{

	private String _myName;
	
	private double _myMin;
	
	private double _myMax;
	
	public CCPropertyObject(String theName, double theMin, double theMax){
		_myName = theName;
		_myMin = theMin;
		_myMax = theMax;
	}
	
	public CCPropertyObject(String theName){
		this(theName, -1, -1);
	}
	
	@Override
	public Class<CCProperty> annotationType() {
		return CCProperty.class;
	}

	@Override
	public String name() {
		return _myName;
	}

	@Override
	public String desc() {
		return "";
	}

	@Override
	public double min() {
		return _myMin;
	}

	@Override
	public double max() {
		return _myMax;
	}

	@Override
	public String precision() {
		return "";
	}

	@Override
	public double defaultValue() {
		return Double.NaN;
	}

	@Override
	public boolean readBack() {
		return false;
	}

	@Override
	public int digits() {
		return 2;
	}

	@Override
	public boolean hide() {
		return false;
	}

}

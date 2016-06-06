package cc.creativecomputing.effects;


public abstract class CCEffectable {

	protected final int _myID;
	
	private double _myIDBlend;
	private double _myGroupIDBlend;
	private double _myGroupBlend;
	private int _myGroup;

	public double _myXBlend;
	public double _myYBlend;
	
	public CCEffectable(int theId){
		_myID = theId;
	}
	
	public void update(double theDeltaTime){
		
	}
	
	public void parameters(String...theParameters){
		
	}
	
	public void apply(double...theValues){
		
	}
	
	public double groupIDBlend(){
		return _myGroupIDBlend;
	}
	
	public void groupIDBlend(double theGroupIDBlend){
		_myGroupIDBlend = theGroupIDBlend;
	}
	
	public double groupBlend(){
		return _myGroupBlend;
	}
	
	public void groupBlend(double theGroupBlend){
		_myGroupBlend = theGroupBlend;
	}
	
	public int group(){
		return _myGroup;
	}
	
	public void group(int theGroup){
		_myGroup = theGroup;
	}
	
	public double xBlend(){
		return _myXBlend;
	}
	
	public void xBlend(double theXBlend){
		_myXBlend = theXBlend;
	}
	
	public double yBlend(){
		return _myYBlend;
	}
	
	public void yBlend(double theYBlend){
		_myYBlend = theYBlend;
	}
	
	public int id(){
		return _myID;
	}
	
	public double idBlend(){
		return _myIDBlend;
	}
	
	public void idBlend(double theIDBlend){
		_myIDBlend = theIDBlend;
	}
}

package cc.creativecomputing.core;

public interface CCBlendable<Type> {

	public Type blend(Type theB, double theScalar);
	
	public Type clone();
}

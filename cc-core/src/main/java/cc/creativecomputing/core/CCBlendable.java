package cc.creativecomputing.core;

import cc.creativecomputing.core.io.format.CCDataSerializable;

public interface CCBlendable<Type> extends CCDataSerializable{
	public static final String BLENDABLE_TYPE_ATTRIBUTE = "blendable_type";

	public Type blend(Type theB, double theScalar);
	
	public Type clone();
}

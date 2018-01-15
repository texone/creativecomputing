package cc.creativecomputing.core;

import cc.creativecomputing.core.io.format.CCDataSerializable;

public interface CCBlendable<Type> extends CCDataSerializable{
	String BLENDABLE_TYPE_ATTRIBUTE = "blendable_type";

	Type blend(Type theB, double theScalar);
	
	Type clone();
}

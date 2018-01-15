package cc.creativecomputing.math.util;

public interface CCMatrixObject<Type extends CCMatrixObject<Type>>{
	Type create(double[] theData);
	
	double[] data();
	
	int dataSize();
}
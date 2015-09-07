package cc.creativecomputing.math.util;

public interface CCMatrixObject<Type extends CCMatrixObject<Type>>{
	public Type create(double[] theData);
	
	public double[] data();
	
	public int dataSize();
}
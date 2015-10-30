package cc.creativecomputing.math;

public class CCFastMath extends CCMath{

	private static final int SIN_RESOLUTION = 100;
	private static final double RADIANS_TO_INDEX = SIN_RESOLUTION * 180.0 / Math.PI;

	private static double sin_lookUp_table[] = new double[SIN_RESOLUTION * 360 + 1];
	static {
		for (int n = 0; n < sin_lookUp_table.length; n++){
			sin_lookUp_table[n] = Math.sin(Math.toRadians((double) n / SIN_RESOLUTION));
		}
	}
	
	public static double sin(double theRadians) {
		theRadians %= CCMath.TWO_PI;
		if(theRadians < 0){
			theRadians += CCMath.TWO_PI;
		}
		double d = theRadians * RADIANS_TO_INDEX;
		int a = (int) d;
		if (a == sin_lookUp_table.length - 1)
			return sin_lookUp_table[a];
		double x = sin_lookUp_table[a];
		double y = sin_lookUp_table[a + 1];
		return x + (d - a) * (y - x);
	}
	
	public static double cos(double theRadians){
		return sin(theRadians - CCMath.HALF_PI);
	}
	
	public static void main(String[] args) {
		System.out.println(cos(0d));
		System.out.println(cos(2 * TWO_PI));
		System.out.println(sin_lookUp_table.length);
	}
}

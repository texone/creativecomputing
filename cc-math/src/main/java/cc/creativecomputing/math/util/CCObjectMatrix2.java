package cc.creativecomputing.math.util;

import cc.creativecomputing.math.CCMatrix2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.interpolate.CCInterpolators;

public class CCObjectMatrix2<ObjectType extends CCMatrixObject<ObjectType>> extends CCMatrix2{

	private ObjectType _myObject;
	
	public CCObjectMatrix2(ObjectType theObject, int theColumns, int theRows) {
		super(theColumns, theRows, theObject.dataSize());
		_myObject = theObject;
	}
	
	public ObjectType getObject(int theColumn, int theRow){
		return _myObject.create(get(theColumn, theRow));
	}
	
	public ObjectType getObject(double theColumn, double theRow){
		return _myObject.create(get(theColumn, theRow));
	}
	
	public ObjectType getObject(CCInterpolators theInterpolator, double theColumn, double theRow){
		return _myObject.create(get(theInterpolator, theColumn, theRow));
	}
	
	public void setObject(ObjectType theObject, int theColumn, int theRow){
		double[] myData = get(theColumn, theRow);
		double[] myObjectData = theObject.data();
		
		for(int i = 0; i < depth();i++){
			myData[i] = myObjectData[i];
		}
	}

	private double sign (int theIndex0, int theIndex1, double[] p1, double[] p2, double[] p3) {
		return 
			(p1[theIndex0] - p3[theIndex0]) * (p2[theIndex1] - p3[theIndex1]) - 
			(p2[theIndex0] - p3[theIndex0]) * (p1[theIndex1] - p3[theIndex1]);
	}

	private double[][] inTriangle(int theIndex0, int theIndex1, double[] pt, int x0, int y0, int x1, int y1, int x2, int y2) {

		double[] p0 = get(x0,y0);
		double[] p1 = get(x1,y1);
		double[] p2 = get(x2,y2);

		if (p0 == null || p0[theIndex0] == 0 || p0[theIndex1] == 0 || 
			p1 == null || p1[theIndex0] == 0 || p1[theIndex1] == 0 || 
			p2 == null || p2[theIndex0] == 0 || p2[theIndex1] == 0
		)
			return null;

		boolean b1 = sign(theIndex0, theIndex1, pt, p0, p1) < 0.0f;
		boolean b2 = sign(theIndex0, theIndex1, pt, p1, p2) < 0.0f;
		boolean b3 = sign(theIndex0, theIndex1, pt, p2, p0) < 0.0f;

		// println(pt);

		if (!((b1 == b2) && (b2 == b3))) {
			return null;
		}

		return new double[][] { p0, p1, p2 };
	}

	
	
	private double[][] triang(int theID0, int theID1, double[] theData) {

		for (int x = 0; x < columns() - 1; x++) {
			for (int y = 0; y < rows() - 1; y++) {
				double[][] myResult0 = null;

				myResult0 = inTriangle(theID0, theID1, theData, x, y, x + 1, y, x, y + 1);
				if (myResult0 != null)
					return myResult0;

				myResult0 = inTriangle(theID0, theID1, theData, x + 1, y, x + 1, y + 1, x, y + 1);
				if (myResult0 != null)
					return myResult0;
			}
		}
		return null;
	}
	
	private CCVector3 baryCoords(int theIndex0, int theIndex1, double[] theData, double[][] tri) {
		double T = 
			(tri[0][theIndex0] - tri[2][theIndex0]) * (tri[1][theIndex1] - tri[2][theIndex1]) - 
			(tri[1][theIndex0] - tri[2][theIndex0]) * (tri[0][theIndex1] - tri[2][theIndex1]);

		double lam1 = (
			(tri[1][theIndex1] - tri[2][theIndex1]) * (theData[theIndex0] - tri[2][theIndex0]) + 
			(tri[2][theIndex0] - tri[1][theIndex0]) * (theData[theIndex1] - tri[2][theIndex1])
		) / T;
		double lam2 = (
			(tri[2][theIndex1] - tri[0][theIndex1]) * (theData[theIndex0] - tri[2][theIndex0]) + 
			(tri[0][theIndex0] - tri[2][theIndex0]) * (theData[theIndex1] - tri[2][theIndex1])
		) / T;
		double lam3 = 1 - lam1 - lam2;

		return new CCVector3(lam1, lam2, lam3);
	}
	
	public ObjectType interpolate(ObjectType theObject, int theID0, int theID1){
		double[] myArray = theObject.data();
		
		double[][]tri = triang(theID0, theID1, myArray);

		if (tri == null) return null;

		CCVector3 lam = baryCoords(theID0, theID1, myArray, tri);
		
		double[] myResult = new double[theObject.dataSize()];
		for(int i = 0; i < myResult.length; i++){
			myResult[i] = lam.x * tri[0][i] + lam.y * tri[1][i] + lam.z * tri[2][i];
		}

		return theObject.create(myResult);
	}
}

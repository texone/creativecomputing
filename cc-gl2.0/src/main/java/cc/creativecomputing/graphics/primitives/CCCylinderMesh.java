package cc.creativecomputing.graphics.primitives;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CCCylinderMesh extends CCMesh {

	public CCCylinderMesh(double theRadius, double theLength, int theRadialResolution, int theLengthResolution) {
		super(CCDrawMode.TRIANGLES, (theRadialResolution + 1) * (theLengthResolution + 1) * 6);

		for (int r = 0; r < theRadialResolution; r++) {
			double myAngle0 = CCMath.map(r, 0, theRadialResolution, 0, CCMath.TWO_PI);
			double myAngle1 = CCMath.map(r + 1, 0, theRadialResolution, 0, CCMath.TWO_PI);
			CCVector2 myPoint0 = CCVector2.circlePoint(myAngle0, 1, 0, 0);
			CCVector2 myPoint1 = CCVector2.circlePoint(myAngle1, 1, 0, 0);

			double myV0 = CCMath.norm(r, 0, theRadialResolution );
			double myV1 = CCMath.norm(r + 1, 0, theRadialResolution );

			for (int l = 0; l < theLengthResolution; l++) {
				double myX0 = CCMath.map(l, 0, theLengthResolution, -theLength / 2, theLength / 2);
				double myX1 = CCMath.map(l + 1, 0, theLengthResolution, -theLength / 2, theLength / 2);
				
				double myU0 = CCMath.norm(l, 0, theLengthResolution);
				double myU1 = CCMath.norm(l + 1, 0, theLengthResolution);

				addNormal(0, myPoint0.x, myPoint0.y);
				addNormal(0, myPoint1.x, myPoint1.y);
				addNormal(0, myPoint1.x, myPoint1.y);
				
				addNormal(0, myPoint0.x, myPoint0.y);
				addNormal(0, myPoint1.x, myPoint1.y);
				addNormal(0, myPoint0.x, myPoint0.y);

				addTextureCoords(myU0, myV0);
				addTextureCoords(myU0, myV1);
				addTextureCoords(myU1, myV1);

				addTextureCoords(myU0, myV0);
				addTextureCoords(myU1, myV1);
				addTextureCoords(myU1, myV0);
				
				addVertex(myX0, myPoint0.x * theRadius, myPoint0.y * theRadius);
				addVertex(myX0, myPoint1.x * theRadius, myPoint1.y * theRadius);
				addVertex(myX1, myPoint1.x * theRadius, myPoint1.y * theRadius);
				
				addVertex(myX0, myPoint0.x * theRadius, myPoint0.y * theRadius);
				addVertex(myX1, myPoint1.x * theRadius, myPoint1.y * theRadius);
				addVertex(myX1, myPoint0.x * theRadius, myPoint0.y * theRadius);
			}
			
			
		}
	}
}
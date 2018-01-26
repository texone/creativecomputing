package cc.creativecomputing.graphics.primitives;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.math.CCMath;

public class CCQuadMesh extends CCVBOMesh {

	public CCQuadMesh(double theWidth, double theHeight, int theXResolution, int theYResolution) {
		super(CCDrawMode.TRIANGLES, (theXResolution) * (theYResolution) * 6);

		for (int y = 0; y < theYResolution; y++) {
			double myY0 = CCMath.map(y, 0, theYResolution, -theHeight/2, theHeight/2);
			double myY1 = CCMath.map(y + 1, 0, theYResolution, -theHeight/2, theHeight/2);

			double myV0 = CCMath.norm(y, 0, theYResolution);
			double myV1 = CCMath.norm(y + 1, 0, theYResolution);

			for (int x = 0; x < theXResolution; x++) {
				double myX0 = CCMath.map(x, 0, theXResolution, -theWidth / 2, theWidth / 2);
				double myX1 = CCMath.map(x + 1, 0, theXResolution, -theWidth / 2, theWidth / 2);
				
				double myU0 = CCMath.norm(x, 0, theXResolution);
				double myU1 = CCMath.norm(x + 1, 0, theXResolution);

				addNormal(0, 0, 1);
				addNormal(0, 0, 1);
				addNormal(0, 0, 1);
				
				addNormal(0, 0, 1);
				addNormal(0, 0, 1);
				addNormal(0, 0, 1);

				addTextureCoords(myU0, myV0);
				addTextureCoords(myU0, myV1);
				addTextureCoords(myU1, myV1);

				addTextureCoords(myU0, myV0);
				addTextureCoords(myU1, myV1);
				addTextureCoords(myU1, myV0);
				
				addVertex(myX0, myY0, 0);
				addVertex(myX0, myY1, 0);
				addVertex(myX1, myY1, 0);
				
				addVertex(myX0, myY0, 0);
				addVertex(myX1, myY1, 0);
				addVertex(myX1, myY0, 0);
			}
			
			
		}
	}
}
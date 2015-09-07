package cc.creativecomputing.kle.simple;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class C4DPlayer extends Player {

	C4DImport importer;
	boolean fromTop = false;
	float ceil;
	
	public C4DPlayer (int theNElements, float theCeil) {
		nElements = theNElements;
		importer = new C4DImport();
		nFrames = 0;
		ceil = theCeil;
	}
	
	public void load (String base, String name, Sculpture theSculpture) {
	
		sculpture = theSculpture;
		importer.load(base+"/"+name, nElements);
		nFrames = importer.nFrames;
	}
	
	public String getViolation () { 
		return "";
	}
	
	@Override
	public CCVector2[] getRopes (float theFrame) {
		CCVector2[] positions = getPositions(theFrame);
		CCVector2[] ropes     = new CCVector2[positions.length];
		
		for (int e=0; e<sculpture.nElements; e++) {
			sculpture.elements.get(e).translateNoScale (positions[e].x, positions[e].y, 1/5f);
		}
		
		return null;
	}
	
	@Override
	public CCVector2[] getPositions (float theFrame) {
		
		CCVector2[] pos0 = importer.getPositions (CCMath.floor(theFrame) % importer.nFrames);
		CCVector2[] pos1 = importer.getPositions (CCMath.ceil(theFrame) % importer.nFrames);
		CCVector2[] ret = new CCVector2[pos0.length];
		
		float t = theFrame - CCMath.floor(theFrame);
		for (int i=0; i<pos0.length; i++) {
			ret[i] = new CCVector2();
			ret[i].x = CCMath.blend(pos0[i].x, pos1[i].x, t);
			ret[i].y = CCMath.blend(pos0[i].y, pos1[i].y, t);
			if (!fromTop) {
				ret[i].y = -ceil - ret[i].y; //ceil - ret[i].y;
			}
		}
		return ret;
	}
}
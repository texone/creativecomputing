package cc.creativecomputing.graphics.scene.debug;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.graphics.bounding.CCBoundingSphere;
import cc.creativecomputing.graphics.bounding.CCBoundingVolume;
import cc.creativecomputing.graphics.scene.CCNode;
import cc.creativecomputing.graphics.scene.CCSpatial;
import cc.creativecomputing.graphics.scene.controllers.CCController;

public class CCDebugNode extends CCNode{
	
	private class CCBoundController extends CCController<CCSpatial>{
		
		private CCBoundingSphere _mySphere;
		
		public CCBoundController(CCBoundingSphere theSphere){
			_mySphere = theSphere;
		}

		@Override
		public boolean update(CCAnimator theAnimator) {
			if(!super.update(theAnimator)){
				return false;
			}
			object().localTransform().scale(_mySphere.radius());
			object().localTransform().translation(_mySphere.center());
			return true;
		}
		
	}
	
	private CCNode _myRootNode;

	private List<CCBoundingSphereShape> _myBoundShapes = new ArrayList<>();
	
	public CCDebugNode(CCNode theRootNode){
		_myRootNode = theRootNode;
	}
	
	private void createBoundShape(CCBoundingVolume theBoundingVolume){
		if(theBoundingVolume instanceof CCBoundingSphere){
			CCBoundingSphere mySphere = (CCBoundingSphere)theBoundingVolume;
			CCBoundingSphereShape myBoundingSphereShape = new CCBoundingSphereShape();
			myBoundingSphereShape.attachController(new CCBoundController(mySphere));
		    _myBoundShapes.add(myBoundingSphereShape);
		}
	}
}

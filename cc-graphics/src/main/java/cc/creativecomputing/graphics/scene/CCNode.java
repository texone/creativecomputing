package cc.creativecomputing.graphics.scene;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.bounding.CCBoundingVolume;
import cc.creativecomputing.graphics.scene.effect.CCVisualEffect;
import cc.creativecomputing.math.CCVector3;

public class CCNode extends CCSpatial implements Iterable<CCSpatial>{
	
	protected List<CCSpatial> _myChildren = new ArrayList<>();
	
	public void attachChild(CCSpatial theChild){
		if(theChild == null)CCLog.error("Cannot add null child");
		if(theChild.hasParent()){
			CCLog.error("Cannot add child with parent");
			return;
		}
		theChild.parent(this);
		_myChildren.add(theChild);
	}
	
	public void detachChild(CCSpatial theChild){
		if(theChild == null)return;
		if(!_myChildren.remove(theChild))return;
		theChild.parent(null);
	}
	
	public CCSpatial detachChild(int theIndex){
		return _myChildren.remove(theIndex);
	}
	
	public CCSpatial setChild(int theIndex, CCSpatial theChild){
		return _myChildren.set(theIndex,theChild);
	}
	
	public CCSpatial child(int theIndex){
		return _myChildren.get(theIndex);
	}
	
	public int numberOfChildren(){
		return _myChildren.size();
	}
	
	@Override
	protected void updateWorldData (CCAnimator theAnimator){
		super.updateWorldData(theAnimator);
		for(CCSpatial myChild:_myChildren){
			myChild.updateGeometricState(theAnimator, false);
		}
	}
	
	@Override
	protected void updateWorldBound (){
		if(worldIsCurrent)return;
		
		CCBoundingVolume worldBound = null;
		for (CCSpatial myChild:_myChildren) {
			if (myChild == null) continue;
				
				if (worldBound != null) {
					// merge current world bound with child world bound
					worldBound.mergeLocal(myChild._myWorldBound);

					// simple check to catch NaN issues
					if (!CCVector3.isValid(worldBound.center())) {
						throw new RuntimeException("WorldBound center is invalid after merge between " + this + " and " + myChild);
					}
				} else {
					// set world bound to first non-null child world bound
					if (myChild._myWorldBound != null) {
						worldBound = myChild._myWorldBound.clone(_myWorldBound);
					}
				}
		}
		_myWorldBound = worldBound;	
	}
	
	public void getVisibleSet(CCCuller culler, boolean noCull) {
		for (CCSpatial myChild : _myChildren) {
			myChild.onGetVisibleSet(culler, noCull);
		}
	}
	
	public void draw(CCRenderer theRenderer, CCVisualEffect theEffect){
		CCVisualEffect myEffect = effect();
		
		for (CCSpatial myChild : _myChildren) {
			myChild.draw(theRenderer, myEffect);
		}
	}

	@Override
	public Iterator<CCSpatial> iterator() {
		return _myChildren.iterator();
	}
}

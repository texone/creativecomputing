package cc.creativecomputing.graphics.scene;

import java.util.HashSet;
import java.util.Set;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.graphics.bounding.CCBoundingVolume;
import cc.creativecomputing.graphics.scene.controllers.CCControlledObject;
import cc.creativecomputing.graphics.scene.effect.CCVisualEffect;
import cc.creativecomputing.math.CCMatrix4x4;
import cc.creativecomputing.math.CCTransform;

public abstract class CCSpatial extends CCControlledObject{
	
	protected CCSpatial _myParent;
	
	/** Spatial's transform relative to its parent. */
	protected final CCTransform _myLocalTransform;

	/** Spatial's absolute transform. */
	protected final CCTransform _myWorldTransform;
	
	/** Spatial's world modelmatrix to its parent. */
	protected CCMatrix4x4 _myModelMatrix;
	
	protected boolean worldIsCurrent;
	
	/** Spatial's world bounding volume. */
	protected CCBoundingVolume _myWorldBound;
	
	protected CCVisualEffect _myEffect = null;
	
	// Culling parameters.
    public enum CCCullingMode
    {
        // Determine visibility state by comparing the world bounding volume
        // to culling planes.
        CULL_DYNAMIC,

        // Force the object to be culled.  If a Node is culled, its entire
        // subtree is culled.
        CULL_ALWAYS,

        // Never cull the object.  If a Node is never culled, its entire
        // subtree is never culled.  To accomplish this, the first time such
        // a Node is encountered, the bNoCull parameter is set to 'true' in
        // the recursive chain GetVisibleSet/OnGetVisibleSet.
        CULL_NEVER
    }

    public CCCullingMode Culling;
	
	protected Set<CCLight> _myLights = new HashSet<>();
	
	protected CCSpatial(){
		_myLocalTransform = new CCTransform();
		_myWorldTransform = new CCTransform();
		
		_myModelMatrix = new CCMatrix4x4();
	}
	
	public CCSpatial parent (){
		return _myParent;
	}
	
	public void parent(CCSpatial theParent){
		_myParent = theParent;
	}
	
	public boolean hasParent(){
		return _myParent != null;
	}
	
	/**
	 * Returns the local transform object of the spatial
	 * @return local transform object of the spatial
	 */
	public CCTransform localTransform(){
		return _myLocalTransform;
	}
	
	/**
	 * Returns the world transform object of the spatial
	 * @return world transform object of the spatial
	 */
	public CCTransform worldTransform(){
		return _myWorldTransform;
	}
	
	public CCBoundingVolume worldBound(){
		return _myWorldBound;
	}
	
	

	/**
	 * Update geometric state.
	 * 
	 * @param theDeltaTime
	 *            The time in seconds between the last two consecutive frames
	 *            (time per frame). See {@link ReadOnlyTimer#getTimePerFrame()}
	 * @see #updateGeometricState(double, boolean)
	 */
	public void updateGeometricState(CCAnimator theAnimator) {
		updateGeometricState(theAnimator, true);
	}

	/**
	 * <code>updateGeometricState</code> updates all the geometry information
	 * for the node.
	 * 
	 * @param theDeltaTime
	 *            The time in seconds between the last two consecutive frames
	 *            (time per frame). See {@link ReadOnlyTimer#getTimePerFrame()}
	 * @param initiator
	 *            true if this node started the update process.
	 */
	public void updateGeometricState(CCAnimator theAnimator, final boolean initiator) {
		updateWorldData(theAnimator);
		updateWorldBound();
	    if ( initiator )
	    	propagateBoundToRoot();
	}
	
	public void updateBoundState(){
		updateWorldBound();
	    propagateBoundToRoot();
	}
	
	protected void updateWorldData (CCAnimator theAnimator){
		updateControllers(theAnimator);
		
		if(worldIsCurrent)return;
		
		if (_myParent != null) {
			_myParent._myWorldTransform.multiply(_myLocalTransform, _myWorldTransform);
		} else {
			_myWorldTransform.set(_myLocalTransform);
		}
		
		_myWorldTransform.toMatrix(_myModelMatrix);
	}
	
	protected void updateWorldBound () {
		
	}
	protected void propagateBoundToRoot (){
		if (_myParent == null) return;
		
		_myParent.updateWorldBound();
		_myParent.propagateBoundToRoot();
	}
	
	public void setLight(CCLight theLight) {
		_myLights.add(theLight);
	}

	public void removeLight(CCLight theLight) {
		_myLights.clear();
	}

	public void removeAllLights() {
		_myLights.clear();
	}
	
	public void getVisibleSet(CCCuller culler, boolean noCull){
		
	}
	
	public void onGetVisibleSet(CCCuller culler, boolean noCull) {
		if (Culling == CCCullingMode.CULL_ALWAYS) {
			return;
		}

		if (Culling == CCCullingMode.CULL_NEVER) {
			noCull = true;
		}

		int savePlaneState = culler.GetPlaneState();
		if (noCull || culler.IsVisible(_myWorldBound)) {
			getVisibleSet(culler, noCull);
		}
		culler.SetPlaneState(savePlaneState);
	}
	
	public void effect(CCVisualEffect theEffect){
		_myEffect = theEffect;
	}
	
	public CCVisualEffect effect(){
		if(_myEffect != null)return _myEffect;
		if(_myParent == null)return null;
		return _myParent.effect();
	}
	
	public void draw(CCRenderer theRenderer){
		draw(theRenderer, _myEffect);
	}
	
	public abstract void draw(CCRenderer theRenderer, CCVisualEffect theEffect);
}

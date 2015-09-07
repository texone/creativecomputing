package cc.creativecomputing.graphics.scene;

import cc.creativecomputing.math.CCMatrix3x3;

public class CCLightNode extends CCNode {

	protected CCLight _myLight;

	// Construction and destruction. The node's world translation is used
	// as the light's location. The node's world rotation matrix is used
	// for the light's coordinate axes. Column 0 of the world rotation
	// matrix is the light's direction vector, column 1 of the world rotation
	// matrix is the light's up vector, and column 2 of the world rotation
	// matrix is the light's right vector.
	//
	// On construction, the node's local transformation is set to the
	// light's current coordinate system.
	// local translation = light location
	// local rotation column 0 = light direction
	// local rotation column 1 = light up
	// local rotation column 2 = light right
	public CCLightNode(CCLight theLight) {
		light(theLight);
	}

	public CCLightNode() {
	}

	// When you set the light, the node's local transformation is set to the
	// light's current current coordinate system. The node's world
	// transformation is computed, and the light's coordinate system is set
	// to use the node's light transformation.
	public void light(CCLight theLight) {
		_myLocalTransform.translation(theLight.position);

		CCMatrix3x3 rotate = new CCMatrix3x3();
		rotate.setColumn(0, theLight.direction);
		rotate.setColumn(1, theLight.up);
		rotate.setColumn(2, theLight.right);

		_myLocalTransform.rotation(rotate);
	}

	// Member access.
	// ----------------------------------------------------------------------------
	public CCLight light() {
		return _myLight;
	}

	protected void updateWorldData(double applicationTime) {

	}
}

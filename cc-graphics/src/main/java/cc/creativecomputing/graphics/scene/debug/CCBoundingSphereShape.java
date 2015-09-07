package cc.creativecomputing.graphics.scene.debug;

import cc.creativecomputing.graphics.scene.CCNode;
import cc.creativecomputing.graphics.scene.effect.CCSolidColorEffect;
import cc.creativecomputing.graphics.scene.shape.line.CCEllipse;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;

public class CCBoundingSphereShape extends CCNode {

	protected static final CCColor xAxisColor = new CCColor(1, 0, 0, .4f);
	protected static final CCColor yAxisColor = new CCColor(0, 1, 0, .25f);
	protected static final CCColor zAxisColor = new CCColor(0, 0, 1, .4f);

	private CCEllipse _myXEllipse;
	private CCEllipse _myYEllipse;
	private CCEllipse _myZEllipse;

	public CCBoundingSphereShape() {
		_myXEllipse = new CCEllipse(100, 1f);
		_myXEllipse.localTransform().rotation(-90 * CCMath.DEG_TO_RAD, 1, 0, 0);
		_myXEllipse.effect(new CCSolidColorEffect(CCColor.RED));
		attachChild(_myXEllipse);

		_myYEllipse = new CCEllipse(100, 1f);
		_myYEllipse.effect(new CCSolidColorEffect(CCColor.GREEN));
		// _myYEllipse.localTransform().translation(0, _myLength * .5f, 0);
		attachChild(_myYEllipse);

		_myZEllipse = new CCEllipse(100, 1f);
		_myZEllipse.localTransform().rotation(90 * CCMath.DEG_TO_RAD, 0, 1, 0);
		_myZEllipse.effect(new CCSolidColorEffect(CCColor.BLUE));
		// _myZEllipse.localTransform().translation(0, 0, _myLength * .5f);

		attachChild(_myZEllipse);
	}
}

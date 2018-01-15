package cc.creativecomputing.demo.realtime.particles;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector2;

public class CCParticle {
	public final CCVector2 position;
	
	public final CCVector2 velocity;

	public CCParticle(CCVector2 thePosition, CCVector2 theVelocity) {
		position = thePosition;
		velocity = theVelocity;
	}
	
	public void update(CCAnimator theAnimator) {
		position.add(velocity.multiply(theAnimator.deltaTime()));
	}
	
	public void draw(CCGraphics g) {
	}
}

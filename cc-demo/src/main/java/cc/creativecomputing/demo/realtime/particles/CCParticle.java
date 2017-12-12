package cc.creativecomputing.demo.realtime.particles;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.math.CCVector2;

public class CCParticle {

	public CCVector2 position;
	public CCVector2 velocity;
	
	public double age;
	
	public CCParticle(CCVector2 thePosition, CCVector2 theVelocity){
		position = thePosition;
		velocity = theVelocity;
		age = 0;
	}
	
	public void update(CCAnimator theAnimator){
		age += theAnimator.deltaTime();
		position.addLocal(velocity.multiply(theAnimator.deltaTime()));
	}
}

package cc.creativecomputing.demo.realtime.particles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector2;

public class CCParticles {

	public List<CCParticle> particles = new ArrayList<>();
	@CCProperty(name = "maxage", min = 1, max = 20)
	private double _cmaxage = 10;
	
	public CCParticles(){
		
	}
	
	public void addParticle(CCVector2 thePosition, CCVector2 theVelocity){
		particles.add(new CCParticle(thePosition, theVelocity));
	}
	
	public void update(CCAnimator theAnimator){
		for (CCParticle ccParticle : new ArrayList<>(particles)) {
			ccParticle.update(theAnimator);
			if(ccParticle.age > _cmaxage)particles.remove(ccParticle);
		}
	}
	
	public void draw(CCGraphics g){
		for (CCParticle ccParticle : particles) {
			g.color(1d - ccParticle.age / _cmaxage);
			g.ellipse(ccParticle.position, 10);
		}
	}
}

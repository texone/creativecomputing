package cc.creativecomputing.demo.realtime.particles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCVector2;

public class CCParticles {
	
	@CCProperty(name = "MaxAge", min = 0, max = 10)
	private double _cMaxAge = 5;
	
	private List<CCParticle> _myParticles = new ArrayList<>();
	
	public void emit(CCVector2 thePosition, CCVector2 theVelocity){
		_myParticles.add(new CCParticle(thePosition, theVelocity));
	}
	
	public void update(CCAnimator theAnimator) {
		for (CCParticle ccParticle : new ArrayList<>(_myParticles)) {
			ccParticle.update(theAnimator);
		}
	}
	
	public String toString(){
		return "TEXONE";
	}
}

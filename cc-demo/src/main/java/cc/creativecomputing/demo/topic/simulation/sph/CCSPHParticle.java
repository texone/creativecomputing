package cc.creativecomputing.demo.topic.simulation.sph;

import cc.creativecomputing.math.CCVector2;

public class CCSPHParticle {
	CCVector2 position = new CCVector2();
	CCVector2 velocity = new CCVector2();
	CCVector2 acceleration = new CCVector2();
	CCVector2 next = new CCVector2();

	int cellX = 0;
	int cellY = 0;

	CCSPHParticle() {
	}
}
package cc.creativecomputing.demo.topic.simulation.sph;

import cc.creativecomputing.math.CCVector2;

class Particle {

	public CCVector2 pos;
	public CCVector2 vel;
	public CCVector2 acc;
	public CCVector2 ev;

	public double dens;
	public double pres;

	public Particle next;
}
package cc.creativecomputing.controlui.timeline.controller;

public interface TransportStateListener {

	void play(double theTime);
	
	void stop(double theTime);
}

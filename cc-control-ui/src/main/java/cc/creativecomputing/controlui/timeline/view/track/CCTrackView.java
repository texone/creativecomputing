package cc.creativecomputing.controlui.timeline.view.track;

public interface CCTrackView {
	int width();
	
	int height();
	
	void render();

	void update();

	void value(String theValue);

	void mute(boolean theIsMuted);

	void moveRangeCursor();
}

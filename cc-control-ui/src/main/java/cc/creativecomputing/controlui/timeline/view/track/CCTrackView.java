package cc.creativecomputing.controlui.timeline.view.track;

public interface CCTrackView {
	public int width();
	
	public int height();
	
	public void render();

	public void update();

	public void value(String theValue);

	public void mute(boolean theIsMuted);

	public void moveRangeCursor();
}

package cc.creativecomputing.app.modules;

import cc.creativecomputing.app.modules.CCAnimatorModule.CCAnimationMode;
import cc.creativecomputing.core.CCProperty;

public abstract class CCAnimator extends CCAbstractAppModule<CCAnimatorListener>{
	@CCProperty(name = "fix update time")
	public boolean fixUpdateTime = false;
	
	@CCProperty(name = "fixed update time", min = 0, max = 1)
	public double fixedUpdateTime = 1;
	
	/**
	 * animation mode. there are different animation modes focusing on performance or precision.
	 * <ul>
	 * <li>{@linkplain CCAnimationMode#PERFORMANT}</li> yields the animation thread for better performance this is the default mode
	 * <li>{@linkplain CCAnimationMode#AS_FAST_AS_POSSIBLE}</li> runs as fast as possible taking more performance
	 * <li>{@linkplain CCAnimationMode#FRAMERATE_PERFORMANT}</li> running with the define forced frame rate, weighting performance over precision
	 * <li>{@linkplain CCAnimationMode#FRAMERATE_PRECISE}</li> running with the define forced frame rate, weighting performance of precision
	 * </ul>
	 */
	@CCProperty(desc = "timer implementation")
	public CCAnimationMode animationMode = CCAnimationMode.PERFORMANT;

	protected CCAnimator(Class<CCAnimatorListener> theListenerInterface, String theID) {
		super(theListenerInterface, theID);
	}

	/**
	 * Indicates whether this animator is currently running. This should only be
	 * used as a heuristic to applications because in some circumstances the
	 * Animator may be in the process of shutting down and this method will
	 * still return true.
	 */
	public abstract boolean isAnimating();

	/**
	 * Returns the current frame rate of the animator
	 * @return current frame rate of the animator
	 */
	public abstract double frameRate();
	
	/**
	 * Returns the time since the last frame of the animator in seconds
	 * @return time since the last frame of the animator 
	 */
	public abstract double deltaTime();
	
	public abstract double deltaTimeVariation();
	
	/**
	 * Returns the time since the start of the animator in seconds
	 * @return
	 */
	public abstract double time();
	
	/**
	 * Returns the number of frames since application start.
	 * @return number of frames since application start
	 */
	public abstract int frames();
	
	/** 
	 * Starts this animator. 
	 **/
	public abstract void start();
}

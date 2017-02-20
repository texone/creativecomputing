/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.video;

public interface CCMovie{

	/**
	 * Positive values indicate forward rates and negative values indicate reverse rates. 
	 * This value immediately changes the rate at which the movie is playing. A value of 1 starts 
	 * the movie playing at normal speed, a value of 2 causes the movie to play at double speed, 
	 * -2 starts the movie playing backward at double speed, and so on. A value of 0 stops the movie.
	 * @param speed
	 */
	public void rate(double speed);

	/**
	 * Positive values indicate forward rates and negative values indicate reverse rates. 
	 * A value of 1 indicates normal speed, a value of 2 indicates double speed, -2 means the 
	 * movie is playing backward at double speed, and so on. A value of 0 means the movie is paused or stopped.
	 * @return a movie's play back rate.
	 */
	public double rate();
	
	/**
	 * Use this method to get the frame rate of the movie.
	 * @return the frame rate of the movie
	 */
	public double frameRate();

	/**
	 * Returns the current time of the movie as a relative value between 0 and 1.
	 * @return progress of movie play back
	 */
	public double progress();

	/**
	 * Returns the current time of the movie in seconds
	 * @return the time in milliseconds
	 */
	public double time();

	/**
	 * Sets the time for movie play back
	 * @param theNewTime new time for movie play back
	 */
	public void time(double theNewTime);
	
	public void goToBeginning();
	
	/**
	 * Returns the length of the movie in seconds
	 * @return length of the movie in seconds
	 */
	public double duration();

	/**
	 * Volume values range from -1.0 to 1.0. Negative values play no sound 
	 * but preserve the absolute value of the volume setting.
	 * @return
	 */
	public double volume();

	/**
	 * Volume values range from -1.0 to 1.0. Negative values play no sound 
	 * but preserve the absolute value of the volume setting.
	 * @param volume
	 */
	public void volume(double volume);

	/**
	 * Starts the movie in loop modus
	 */
	public void loop();
	
	/**
	 * true activates looping false turns looping off
	 * @param doLoop
	 */
	public void loop(final boolean doLoop);

	/**
	 * Returns true if the movie is running otherwise false. 
	 * You can use this method to check if a movie is still playing
	 * when it is not looping.
	 * @return
	 */
	public boolean isRunning();

	/**
	 * Starts the movie, if loop is set false. The movie stops after it has been
	 * played once. The movie is started at the last position. If you set restart
	 * to true the movie will start from the beginning, other wise it will start 
	 * from its last position.
	 * @param restart
	 */
	public void play();

	/**
	 * 
	 * 
	 */
	public void play(boolean restart);
	
	/**
	 * Pauses a movie during playback. If a movie is started again with play(), it will continue from where it was
	 * paused.
	 */
	public void pause();

	/**
	 * Stops a movie from continuing. The playback returns to the beginning so when a movie is played, it will begin
	 * from the beginning.
	 */
	public void stop();
}

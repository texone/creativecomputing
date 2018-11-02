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

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCImageEvent;

/**
 * This class is representing dynamic texture data the content of this object
 * might be fed from a movie or capture device and change. You can add listeners
 * to react on changes of the data.
 * @author christian riekoff
 *
 */
public abstract class CCVideo extends CCImage {
	
	/**
	 * indicates a needed update of the data although the movie is not running
	 * this might happen on change of the position
	 */
	protected boolean _myForceUpdate = false;
	
	/**
	 * Indicates the initialization of the first frame on data update
	 */
	protected boolean _myIsFirstFrame;
	
	/**
	 * Keep the listeners for init events
	 */
	public CCListenerManager<CCImageEvent> initEvents = new CCListenerManager<>(CCImageEvent.class);
	/**
	 * Keep the listeners for update events
	 */
	public CCListenerManager<CCImageEvent> updateEvents = new CCListenerManager<>(CCImageEvent.class);

	/**
	 * Creates a new instance, without setting any parameters.
	 * @param theApp
	 */
	public CCVideo(final CCAnimator theAnimator) {
		super();
		_myPixelStorageModes.alignment(1);
		theAnimator.updateEvents().add(this::update);
	}
	
	public void update(CCAnimator theAnimator) {
	}
	
}

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
package cc.creativecomputing.graphics.texture;

import cc.creativecomputing.video.CCVideo;
import cc.creativecomputing.video.CCVideoTextureDataListener;

/**
 * @author christianriekoff
 *
 */
public class CCVideoTexture<VideoType extends CCVideo> extends CCTexture2D implements CCVideoTextureDataListener{
	
	private VideoType _myMovie;
	
	public CCVideoTexture(VideoType theData, CCTextureTarget theTarget, final CCTextureAttributes theAttributes) {
		super(theTarget, theAttributes, 1,800,200);
		video(theData);
	}
	
	public CCVideoTexture(VideoType theData){
		super(theData);
		video(theData);
	}
	
	public void video(VideoType theData){
		if(_myMovie != null)_myMovie.removeListener(this);
		_myMovie = theData;
		_myMovie.addListener(this);
	}
	
	public VideoType video(){
		return _myMovie;
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.texture_new.video.CCVideoTextureData.CCVideoTextureDataListener#onInit(cc.creativecomputing.texture_new.video.CCVideoTextureData)
	 */
	public void onInit(CCVideo theData) {
		data(theData);
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.texture_new.video.CCVideoTextureData.CCVideoTextureDataListener#onUpdate(cc.creativecomputing.texture_new.video.CCVideoTextureData)
	 */
	public void onUpdate(CCVideo theData) {
		updateData(theData);
	}

}

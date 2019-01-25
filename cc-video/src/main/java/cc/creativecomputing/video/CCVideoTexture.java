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

import cc.creativecomputing.gl.app.CCGLListener;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCImageEvent;
import cc.creativecomputing.video.CCVideo;

/**
 * @author christianriekoff
 *
 */
public class CCVideoTexture extends CCTexture2D implements CCGLListener<CCGraphics>{
	
	private CCVideo _myVideoData;

	private CCImage _myInitVideo = null;
	private CCImageEvent _myInitEvent = i -> _myInitVideo = i;
	
	private CCImage _myUpdateVideo = null;
	private CCImageEvent _myUpdateEvent = i -> _myUpdateVideo = i;
	
	private boolean reset = false;
	
	public CCVideoTexture(CCGL2Adapter theAdapter, CCVideo theVideoData, CCTextureTarget theTarget, final CCTextureAttributes theAttributes) {
		super(theTarget, theAttributes, 1,800,200);
		video(theVideoData);
		theAdapter.glListener().add(this);
	}
	
	public CCVideoTexture(CCGL2Adapter theAdapter,final CCTextureTarget theTarget, CCTextureAttributes theAttributes) {
		super(theTarget, theAttributes, 1,800,200);
		theAdapter.glListener().add(this);
	}
	
	public CCVideoTexture(CCGL2Adapter theAdapter,CCVideo theVideoData){
		this(theVideoData);
		theAdapter.glListener().add(this);
	}
	
	public CCVideoTexture(CCVideo theVideoData){
		super(theVideoData);
		video(theVideoData);
	}
	
	public void video(CCVideo theVideoData){
		if(_myVideoData != null) {
			_myVideoData.initEvents.remove(_myInitEvent);
			_myVideoData.updateEvents.remove(_myUpdateEvent);
		}
		_myVideoData = theVideoData;
		_myVideoData.initEvents.add(_myInitEvent);
		_myVideoData.updateEvents.add(_myUpdateEvent);
		
		reset = true;
	}
	
	public CCVideo video(){
		return _myVideoData;
	}

	public void reset() {
		allocateData(800, 200, null);
	}

	@Override
	public void reshape(CCGraphics theContext) {}

	@Override
	public void init(CCGraphics theContext) {}

	@Override
	public void dispose(CCGraphics theContext) {}

	@Override
	public void display(CCGraphics theContext) {
		if(reset) {
			reset();
			reset = false;
		}
		if(_myInitVideo != null){
			allocateData(_myInitVideo.width(), _myInitVideo.height(), null);
			data(_myInitVideo);
			_myInitVideo = null;
		}
		if(_myUpdateVideo != null){
			data(_myUpdateVideo);
			_myUpdateVideo = null;
		}
	}

}

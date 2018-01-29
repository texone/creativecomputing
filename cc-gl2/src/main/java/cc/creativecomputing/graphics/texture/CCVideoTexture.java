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

import cc.creativecomputing.gl.app.CCGLListener;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.video.CCVideo;
import cc.creativecomputing.video.CCVideoTextureDataListener;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.opengl.GL42.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.opengl.GL44.*;
import static org.lwjgl.opengl.GL45.*;
/**
 * @author christianriekoff
 *
 */
public class CCVideoTexture extends CCTexture2D implements CCVideoTextureDataListener, CCGLListener<CCGraphics>{
	
	private CCVideo _myMovie;
	
	public CCVideoTexture(CCGL2Adapter theAdapter, CCVideo theData, CCTextureTarget theTarget, final CCTextureAttributes theAttributes) {
		super(theTarget, theAttributes, 1,800,200);
		video(theData);
		theAdapter.glListener().add(this);
	}
	
	public CCVideoTexture(CCGL2Adapter theAdapter,final CCTextureTarget theTarget, CCTextureAttributes theAttributes) {
		super(theTarget, theAttributes, 1,800,200);
		theAdapter.glListener().add(this);
	}
	
	public CCVideoTexture(CCGL2Adapter theAdapter,CCVideo theData){
		this(theData);
		theAdapter.glListener().add(this);
	}
	
	public CCVideoTexture(CCVideo theData){
		super(theData);
		video(theData);
	}
	
	public void video(CCVideo theData){
		if(_myMovie != null)_myMovie.removeListener(this);
		_myMovie = theData;
		_myMovie.addListener(this);
	}
	
	public CCVideo video(){
		return _myMovie;
	}
	
	private CCImage _myInitVideo = null;

	/* (non-Javadoc)
	 * @see cc.creativecomputing.texture_new.video.CCVideoTextureData.CCVideoTextureDataListener#onInit(cc.creativecomputing.texture_new.video.CCVideoTextureData)
	 */
	public void onInit(CCImage theData) {
		_myInitVideo = theData;
	}
	
	private CCImage _myUpdateVideo = null;

	/* (non-Javadoc)
	 * @see cc.creativecomputing.texture_new.video.CCVideoTextureData.CCVideoTextureDataListener#onUpdate(cc.creativecomputing.texture_new.video.CCVideoTextureData)
	 */
	public void onUpdate(CCImage theData) {
		_myUpdateVideo = theData;
	}

	@Override
	public void reshape(CCGraphics theContext) {}

	@Override
	public void init(CCGraphics theContext) {}

	@Override
	public void dispose(CCGraphics theContext) {}

	@Override
	public void display(CCGraphics theContext) {
		if(_myInitVideo != null){
			allocateData(_myInitVideo.width(), _myInitVideo.height());
			data(_myInitVideo);
			_myInitVideo = null;
		}
		if(_myUpdateVideo != null){
			data(_myUpdateVideo);
			_myUpdateVideo = null;
		}
	}

}

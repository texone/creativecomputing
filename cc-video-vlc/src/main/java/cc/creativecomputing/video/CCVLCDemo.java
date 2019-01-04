package cc.creativecomputing.video;

import java.nio.ByteBuffer;

import com.sun.jna.Memory;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.io.CCNIOUtil;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DefaultDirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

public class CCVLCDemo extends CCGL2Adapter {
	

    private DirectMediaPlayerComponent mediaPlayerComponent;
    
    private int _myWidth = 0;
    private int _myHeight = 0;
    private boolean _myCreateTexture = false;
    private CCTexture2D _myTexture;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		 new NativeDiscovery().discover();
		 
		 BufferFormatCallback bufferFormatCallback = new BufferFormatCallback() {
	            @Override
	            public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
	            	CCLog.info(sourceWidth, sourceHeight);
	            	_myWidth = sourceWidth;
	            	_myHeight = sourceHeight;
	            	_myCreateTexture = true;
	                return new RV32BufferFormat(sourceWidth, sourceHeight);
	            }
	        };
	        mediaPlayerComponent = new DirectMediaPlayerComponent(bufferFormatCallback);
	        mediaPlayerComponent.getMediaPlayer().prepareMedia(CCNIOUtil.dataPath("sintel_trailer-1080p.mp4").toString());
	        
	        CCTextureAttributes myAttributes = new CCTextureAttributes();
			myAttributes.format(CCPixelFormat.BGRA);
			myAttributes.pixelType(CCPixelType.UNSIGNED_BYTE);
			 _myTexture = new CCTexture2D(myAttributes);
	}
	
	public void play() {
		
	}
	
	public void pause() {
		
	}
	
	private ByteBuffer myBuffer;

	@Override
	public void update(CCAnimator theAnimator) {
		CCLog.info(mediaPlayerComponent.getMediaPlayer().getFps());
		CCLog.info(mediaPlayerComponent.getMediaPlayer().getTime());
		CCLog.info(mediaPlayerComponent.getMediaPlayer().getLength());
		myBuffer = null;
		Memory[] nativeBuffers = mediaPlayerComponent.getMediaPlayer().lock();
        if (nativeBuffers != null) {
            // FIXME there may be more efficient ways to do this...
            // Since this is now being called by a specific rendering time, independent of the native video callbacks being
            // invoked, some more defensive conditional checks are needed
            Memory nativeBuffer = nativeBuffers[0];
            if (nativeBuffer != null) {
            	myBuffer = nativeBuffer.getByteBuffer(0, nativeBuffer.size());
                BufferFormat bufferFormat = ((DefaultDirectMediaPlayer) mediaPlayerComponent.getMediaPlayer()).getBufferFormat();
                CCLog.info(bufferFormat.getWidth(), bufferFormat.getHeight());
                if (bufferFormat.getWidth() > 0 && bufferFormat.getHeight() > 0) {
//                    pixelWriter.setPixels(0, 0, bufferFormat.getWidth(), bufferFormat.getHeight(), pixelFormat, byteBuffer, bufferFormat.getPitches()[0]);
                }
            }
        }
        mediaPlayerComponent.getMediaPlayer().unlock();
	}

	@Override
	public void display(CCGraphics g) {
		if(_myCreateTexture) {
			_myTexture.allocateData(_myWidth, _myHeight, null);
			 _myCreateTexture = false;
		}
		if(myBuffer != null && _myTexture != null) {
			_myTexture.texImage2D(myBuffer);
		}
		
		g.clear();
		g.ortho2D();
		if(_myTexture != null)g.image(_myTexture, 0,0);
	}

	public static void main(String[] args) {

		CCVLCDemo demo = new CCVLCDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

package cc.creativecomputing.video;

import java.nio.ByteBuffer;
import java.nio.file.Path;

import com.sun.jna.Memory;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.video.CCMovieData.CCMovieEvent;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DefaultDirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallback;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

public class CCVLCTexture extends CCTexture2D {

	private ByteBuffer _myBuffer;

	private DirectMediaPlayerComponent mediaPlayerComponent;

	private boolean _myAllocateTexture;

	public CCListenerManager<CCMovieEvent> endEvents = CCListenerManager.create(CCMovieEvent.class);
	
	private int _myFrame = 0;

	public CCVLCTexture(CCAnimator theAnimator) {
		super(new CCTextureAttributes().format(CCPixelFormat.BGRA).pixelType(CCPixelType.UNSIGNED_BYTE));

		new NativeDiscovery().discover();
		
		mustFlipVertically(true);

		BufferFormatCallback bufferFormatCallback = new BufferFormatCallback() {
			@Override
			public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
				_myAllocateTexture = _myWidth != sourceWidth || _myHeight != sourceHeight;

				_myWidth = sourceWidth;
				_myHeight = sourceHeight;
				return new RV32BufferFormat(sourceWidth, sourceHeight);
			}
		};
		mediaPlayerComponent = new DirectMediaPlayerComponent(bufferFormatCallback) {
			@Override
			public void finished(MediaPlayer mediaPlayer) {
				endEvents.proxy().event();
			}
			
			@Override
			protected RenderCallback onGetRenderCallback() {
				return new RenderCallback() {
					
					@Override
					public void display(DirectMediaPlayer arg0, Memory[] arg1, BufferFormat arg2) {
						_myBuffer = null;
						if( !mediaPlayerComponent.getMediaPlayer().isPlaying())return;
						_myFrame++;
						Memory[] nativeBuffers = mediaPlayerComponent.getMediaPlayer().lock();
						if (nativeBuffers != null) {
							// FIXME there may be more efficient ways to do this...
							// Since this is now being called by a specific rendering time, independent of
							// the native video callbacks being
							// invoked, some more defensive conditional checks are needed
							Memory nativeBuffer = nativeBuffers[0];
							if (nativeBuffer != null) {
								BufferFormat bufferFormat = ((DefaultDirectMediaPlayer) mediaPlayerComponent.getMediaPlayer()).getBufferFormat();

								_myAllocateTexture = _myWidth != bufferFormat.getWidth() || _myHeight != bufferFormat.getHeight();

								_myWidth = bufferFormat.getWidth();
								_myHeight = bufferFormat.getHeight();
								
								_myBuffer = nativeBuffer.getByteBuffer(0, nativeBuffer.size());
								
							}
						}
						mediaPlayerComponent.getMediaPlayer().unlock();
					}
				};
			}
		};
		
		

		theAnimator.updateEvents().add(a -> {
			
		});
	}
	
	public int frame() {
		return _myFrame;
	}

	public void media(Path thePath) {
		_myFrame = 0;
        mediaPlayerComponent.getMediaPlayer().prepareMedia(thePath.toString());
	}

	public void playMedia(Path thePath) {
		_myFrame = 0;
        mediaPlayerComponent.getMediaPlayer().playMedia(thePath.toString());
	}
	
	public void play() {
		mediaPlayerComponent.getMediaPlayer().play();
	}
	
	public void pause() {
		mediaPlayerComponent.getMediaPlayer().pause();
	}
	
	public void stop() {
		_myFrame = 0;
		mediaPlayerComponent.getMediaPlayer().stop();
	}

	public boolean isRunning() {
		return mediaPlayerComponent.getMediaPlayer().isPlaying();
	}

	public void volume(double theVolume) {
		mediaPlayerComponent.getMediaPlayer().setVolume((int)(theVolume / 200d));
		
	}

	public double duration() {
		return mediaPlayerComponent.getMediaPlayer().getLength() / 1000d;
	}

	public double time() {
		return mediaPlayerComponent.getMediaPlayer().getTime() / 1000d;
	}

	public double position() {
		return mediaPlayerComponent.getMediaPlayer().getPosition();
	}
	
	public void preDisplay(CCGraphics g) {
		if(_myAllocateTexture) {
			allocateData(_myWidth, _myHeight, null);
			_myAllocateTexture = false;
		}
		if(_myBuffer != null) {
			texImage2D(_myBuffer);
		}
	}

	public double fps() {
		return mediaPlayerComponent.getMediaPlayer().getFps();
	}
}

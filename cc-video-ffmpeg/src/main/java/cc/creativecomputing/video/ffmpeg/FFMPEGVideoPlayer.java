package cc.creativecomputing.video.ffmpeg;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCCollectionUtil;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCPixelFormat;
import cc.creativecomputing.image.CCPixelType;
import cc.creativecomputing.video.ffmpeg.FrameGrabber.Exception;

public class FFMPEGVideoPlayer  {
    
	public static FFMPEGVideoPlayer fromURL(String url) {
		return new FFMPEGVideoPlayer(url);
	}

	public static FFMPEGVideoPlayer  fromFile(String filename) {
		try {
			return new FFMPEGVideoPlayer(new File(filename).toURI().toURL().toExternalForm());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public static List<String>  listDevices()  {
		try {
			return CCCollectionUtil.createList(FFmpegFrameGrabber.getDeviceDescriptions());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String defaultDevice() {
		String osName = System.getProperty("os.name").toLowerCase();
		String device = null;
            
		if(osName.contains("windows")) {
			device =  "video=Integrated Webcam";
		}else if(osName.contains("mac os x")) {
			device = "0";
		}else if(osName.contains("linux")) {
			device = "/dev/video0";
		}else {
			throw new RuntimeException("unsupported os: $osName");
		}
           
		return device;
	}

	public static String defaultInputFormat() {
		String osName = System.getProperty("os.name").toLowerCase();
		String format = null;
		
		if(osName.contains("windows")) {
			format = null;
		}else if(osName.contains("mac os x")) {
			format = null;
		}else if(osName.contains("linux")) {
			format = "mjpeg";
		}else {
			throw new RuntimeException("unsupported os: $osName");
		}
            
		return format;
	}

	public static FFMPEGVideoPlayer fromDevice(
		String deviceName, 
		int width, 
		int height, 
		double framerate, 
		String inputFormat
	) {
        	
		String osName = System.getProperty("os.name").toLowerCase();
		String format = null;
            
		if(osName.contains("windows")) {
			format = "dshow";
		}else if(osName.contains("mac os x")) {
			format = "avfoundation";
		}else if(osName.contains("linux")) {
			format = "video4linux2";
		}else {
			throw new RuntimeException("unsupported os: $osName");
		}
    
		FFMPEGVideoPlayer player = new FFMPEGVideoPlayer(deviceName);
		player.frameGrabber.inputFormat = inputFormat;
		player.frameGrabber.format = format;
		if (width != -1 && height != -1) {
			player.frameGrabber.imageWidth = width;
			player.frameGrabber.imageHeight = height;
		}
		if (framerate != -1.0) {
			player.frameGrabber.frameRate = framerate;
		}
		return player;
	}
	
	public static FFMPEGVideoPlayer fromDevice(
		String deviceName, 
		int width, 
		int height, 
		double framerate
	) {
		return fromDevice(deviceName, width, height, framerate, defaultInputFormat());
	}
	
	public static FFMPEGVideoPlayer fromDevice(
		String deviceName, 
		int width, 
		int height
	) {
		return fromDevice(deviceName, width, height, -1);
	}
	
	public static FFMPEGVideoPlayer fromDevice(
		String deviceName
	) {
		return fromDevice(deviceName, -1, -1);
	}
    
	public static FFMPEGVideoPlayer fromDevice() {
		return fromDevice(defaultDevice());
	}
	
	FFmpegFrameGrabber frameGrabber;
    private CCImage colorBuffer = null;
    
    private FFMPEGVideoPlayer(String url ) {
		frameGrabber = new FFmpegFrameGrabber(url);
    }


    public int width(){
    	return colorBuffer != null?colorBuffer.width() : 0;
    }

    public int height(){
        return colorBuffer != null?colorBuffer.height() : 0;
    }

    public void start() {
        try {
			frameGrabber.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }


    public void next() {
    	try {
	        Frame frame = frameGrabber.grabImage();
			
	        if(frame == null)return;
	     
	        if (colorBuffer == null && frame.imageWidth > 0 && frame.imageHeight > 0) {
	        	colorBuffer = new CCImage(frame.imageWidth, frame.imageHeight, CCPixelFormat.RGB, CCPixelType.UNSIGNED_BYTE);
	        	colorBuffer.mustFlipVertically(true);
	        }
	        
	        colorBuffer.buffer(frame.image[0]);
	    } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
}
package cc.creativecomputing.kinect;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCPixelFormat;
import cc.creativecomputing.image.CCPixelInternalFormat;
import cc.creativecomputing.image.CCPixelType;
import KinectPV2.Device.KDataListener;
import KinectPV2.Device.KFloatDataListener;

public class CCKinectImage extends CCImage implements KDataListener, KFloatDataListener{
	
	public CCKinectImage(int theWidth, int theHeight, CCPixelInternalFormat theInternalFormat, CCPixelFormat theFormat, CCPixelType theType){
		super(theWidth, theHeight, theInternalFormat, theFormat, theType);
	}

	@Override
	public void onData(int[] theData) {
		ByteBuffer myBuffer = (ByteBuffer)buffer();
		myBuffer.order(ByteOrder.LITTLE_ENDIAN);
		myBuffer.rewind();
		myBuffer.asIntBuffer().put(theData);
		myBuffer.rewind();
	}

	@Override
	public void onData(float[] theData) {
		FloatBuffer myBuffer = (FloatBuffer)buffer();
		myBuffer.rewind();
		myBuffer.put(theData);
		myBuffer.rewind();
	}

}

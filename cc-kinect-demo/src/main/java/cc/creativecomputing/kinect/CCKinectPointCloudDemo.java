package cc.creativecomputing.kinect;

import java.nio.IntBuffer;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl4.GLGraphics;
import cc.creativecomputing.gl4.GLGraphics.GLColorBuffer;
import cc.creativecomputing.gl4.GLPixelDataInternalFormat;
import cc.creativecomputing.gl4.GLSampler.GLTextureMagFilter;
import cc.creativecomputing.gl4.GLSampler.GLTextureMinFilter;
import cc.creativecomputing.gl4.texture.GLTexture2D;
import cc.creativecomputing.graphics.scene.CCCamera;
import cc.creativecomputing.graphics.scene.CCRenderer;
import cc.creativecomputing.graphics.scene.CCSpatial;
import cc.creativecomputing.graphics.scene.effect.CCSingleTextureEffect;
import cc.creativecomputing.graphics.scene.shape.CCPointGrid;
import cc.creativecomputing.graphics.scene.shape.CCQuad;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCPixelFormat;
import cc.creativecomputing.image.CCPixelInternalFormat;
import cc.creativecomputing.image.CCPixelType;
import cc.creativecomputing.kinect.graphics.CCKinectGLAdapter;
import cc.creativecomputing.kinect.graphics.CCKinectGraphicsApplication;
import cc.creativecomputing.kinect.graphics.CCPointCloudFromTextureEffect;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

public class CCKinectPointCloudDemo extends CCKinectGLAdapter {
	
		private CCKinectImage _myDepthImage;
		
		@Override
		public void start(CCKinectModule theKinectModule) {
			_myDepthImage = theKinectModule.depthPositionImage();
		}
		
		private CCSpatial _myPrimitive;
		
		private GLTexture2D _myTexture;

		private CCCamera _myCamera;

		private CCRenderer _myRenderer;

		@Override
		public void init(GLGraphics g) {
			CCLog.info("init gl");
			CCImage myImage = new CCImage(100, 100, CCPixelInternalFormat.RGB, CCPixelFormat.RGB, CCPixelType.INT);
			IntBuffer myBuf = (IntBuffer)myImage.buffer();
			for(int x = 0;x < 100;x++){
				for(int y = 0;y < 100;y++){
					myBuf.put((int)CCMath.random(Integer.MAX_VALUE));
					myBuf.put((int)CCMath.random(Integer.MAX_VALUE));
					myBuf.put((int)CCMath.random(Integer.MAX_VALUE));
				}
			}
			myBuf.rewind();
			_myTexture = new GLTexture2D(_myDepthImage);
			_myTexture.minFilter(GLTextureMinFilter.LINEAR);
			_myTexture.magFilter(GLTextureMagFilter.LINEAR);

			_myPrimitive = new CCPointGrid(-0.5f, -0.5f, 1f, 1f, _myDepthImage.width(), _myDepthImage.height());
			_myPrimitive.effect(new CCPointCloudFromTextureEffect(_myTexture));

			_myCamera = new CCCamera(false);
			
			_myCamera.lookAt(new CCVector3(0.0f, 0.0f, 2.0f), new CCVector3(0.0f, 0.0f, 0.0f), new CCVector3(0.0f, 1.0f, 0.0f));
			_myRenderer = new CCRenderer(_myCamera, g);
		}

		float myAngle = 0;

		@Override
		public void update(CCAnimator theAnimator) {
			myAngle += theAnimator.deltaTime();

//			_myPrimitive.localTransform().rotation(myAngle, 0.33f, 0.7f, 0);
			 _myPrimitive.localTransform().scale(-0.5f);
			 CCLog.info(_myDepthImage.width() + " , " + _myDepthImage.height());
			 _myPrimitive.localTransform().translation(_myDepthImage.width()/2, _myDepthImage.height()/2, 0);
			_myPrimitive.updateGeometricState(theAnimator);

		}

		@Override
		public void reshape(GLGraphics g) {
			g.viewport(0, 0, g.width(), g.height());
//			_myCamera.perpective(70.0f, g.aspectRatio(), 0.3f, 100.0f);
			_myCamera.frustum(-10, 10, 0, g.height(), 0, g.width());
		}

		@Override
		public void display(GLGraphics g) {
//			IntBuffer myBuffer = (IntBuffer)_myDepthImage.buffer();
			
//			while(myBuffer.hasRemaining()){
//				int myVal = myBuffer.get();
//				if(myVal != 0)CCLog.info(myVal);
//			}
//			myBuffer.rewind();
			_myTexture.bind();
			_myTexture.texImage2D(_myDepthImage);
			g.clearBufferfv(GLColorBuffer.COLOR, 0, 0f, 0f, 0f, 1f);
			g.clearDepthBuffer(1f);
			g.depthTest();

			g.pointSize(3);
			_myPrimitive.draw(_myRenderer);
		}

		public static void main(String[] args) {
			CCKinectGraphicsApplication myAppManager = new CCKinectGraphicsApplication(new CCKinectPointCloudDemo());
			myAppManager.animator().framerate = 30;
			myAppManager.animator().animationMode = CCAnimationMode.FRAMERATE_PRECISE;
			
			myAppManager.glcontext().size(1920, 1080);
			myAppManager.start();
		}

}

package cc.creativecomputing.demo.net.osc;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.io.netty.CCClient;
import cc.creativecomputing.io.netty.CCUDPClient;
import cc.creativecomputing.io.netty.CCUDPServer;
import cc.creativecomputing.io.netty.codec.osc.CCOSCCodec;
import cc.creativecomputing.io.netty.codec.osc.CCOSCMessage;
import cc.creativecomputing.io.netty.codec.osc.CCOSCPacket;

public class CCOSCFraport extends CCGL2Adapter {

	// @CCProperty(name = "CCUDPOut")
	private CCClient<CCOSCPacket> _myOSCOut;

	private class CCOSCKaleidoscope {
		@CCProperty(name = "nt euclidian", min = 0, max = 1)
		private double _NoiseEuclidian = 0;
		@CCProperty(name = "nt triangular", min = 0, max = 1)
		private double _NoiseTriangular = 0;

		@CCProperty(name = "noise speed", min = 0, max = 5)
		private double _NoiseSpeed = 1;
		@CCProperty(name = "no scale", min = 0, max = 50)
		private double _NoiseScale = 0.1;
		@CCProperty(name = "no gain", min = 0, max = 1)
		private double _NoiseGain = 0.5;
		@CCProperty(name = "no octaves", min = 1, max = 10)
		private double _NoiseOctaves = 1;
		@CCProperty(name = "no lacunarity", min = 0, max = 4)
		private double _NoiseLacunarity = 2;

		@CCProperty(name = "noise smooth", min = 0, max = 1)
		private double _NoiseSmooth = 0;

		@CCProperty(name = "bt range", min = 0, max = 1)
		private double _BlendRange = 0.5;
		@CCProperty(name = "bt global amount", min = 0, max = 1)
		private double _GlobalBlend = 0;
		@CCProperty(name = "bt x amount", min = 0, max = 1)
		private double _X_Blend = 0;
		@CCProperty(name = "bt xmod amount", min = 0, max = 1)
		private double _X_ModBlend = 0;
		@CCProperty(name = "bt xmod", min = 1, max = 10)
		private double _X_Mod = 2;
		@CCProperty(name = "bt xmod offset", min = -1, max = 1)
		private double _X_ModOffset = 0;
		@CCProperty(name = "bt flip xmod", min = 0, max = 1)
		private double _X_FlipMod = 0;

		@CCProperty(name = "refraction", min = 0, max = 1)
		private double _Refraction = 0.5;

		@CCProperty(name = "Blend Refract", min = 0, max = 1)
		private double _BlendRefract = 0;

		@CCProperty(name = "dt0 refraction", min = 0, max = 1)
		private double _DistanceTypeRefraction0 = 1;
		@CCProperty(name = "dt1 refraction", min = 0, max = 1)
		private double _DistanceTypeRefraction1 = 0;
		@CCProperty(name = "dt2 refraction", min = 0, max = 1)
		private double _DistanceTypeRefraction2 = 0;
		@CCProperty(name = "dt3 refraction", min = 0, max = 1)
		private double _DistanceTypeRefraction3 = 0;

		@CCProperty(name = "Blend Progress", min = 0, max = 1)
		private double _BlendProgress = 0;

		@CCProperty(name = "dt0 blend", min = 0, max = 1)
		private double _DistanceTypeBlend0 = 1;
		@CCProperty(name = "dt1 blend", min = 0, max = 1)
		private double _DistanceTypeBlend1 = 0;
		@CCProperty(name = "dt2 blend", min = 0, max = 1)
		private double _DistanceTypeBlend2 = 0;
		@CCProperty(name = "dt3 blend", min = 0, max = 1)
		private double _DistanceTypeBlend3 = 0;

		@CCProperty(name = "BlendOffset", min = 0, max = 1)
		private double _BlendOffset = 0;
		@CCProperty(name = "Blend Random", min = 0, max = 1)
		private double _BlendRandom = 0;
		@CCProperty(name = "Octaves Random", min = 0, max = 1)
		private double _OctavesRandom = 0;

		@CCProperty(name = "Texture Slices", min = 0, max = 100)
		private double _TextureSlices = 20;
		@CCProperty(name = "Y Scroll Scale Factor")
		private double _Y_ScrollScale = 1;
		@CCProperty(name = "Image Blend Smoothness", min = 0, max = 1)
		private double _ImageBlendSmoothness = 1;

		@CCProperty(name = "Debug Show Noise", min = 0, max = 1)
		private double _DebugShowNoise = 0;

		private Map<String, Double> _myMap = new HashMap<>();

		public void send() {
			for (Field myField : getClass().getDeclaredFields()) {
				myField.setAccessible(true);
				try {
					if (!_myMap.containsKey(myField.getName())) {
						_myMap.put(myField.getName(), Double.NaN);
					}
					if (_myMap.get(myField.getName()) != myField.getDouble(this)) {
						_myOSCOut.write(new CCOSCMessage("/ArtComLiveEditing/ShaderSettings/Kaleidoscope." + myField.getName(), myField.getDouble(this)));
						_myMap.put(myField.getName(), myField.getDouble(this));
					}
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				}
				// 
			}

		}
	}

	@CCProperty(name = "kaleidoscope")
	private CCOSCKaleidoscope _myKaleidoscope = new CCOSCKaleidoscope();

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myOSCOut = new CCUDPClient<>(new CCOSCCodec(), "127.0.0.1", 50482);
		// _myOSCIN.events().add(message -> {
		// CCLog.info(message.message);
		// });
		_myOSCOut.connect();
	}

	@Override
	public void update(CCAnimator theAnimator) {
		_myKaleidoscope.send();
	}

	@Override
	public void display(CCGraphics g) {
	}

	public static void main(String[] args) {

		CCOSCFraport demo = new CCOSCFraport();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

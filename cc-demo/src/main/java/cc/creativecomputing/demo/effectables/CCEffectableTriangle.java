package cc.creativecomputing.demo.effectables;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.effects.CCEffectManager;
import cc.creativecomputing.effects.CCEffectable;
import cc.creativecomputing.effects.CCOffsetEffect;
import cc.creativecomputing.effects.CCSignalEffect;
import cc.creativecomputing.effects.modulation.CCColumnRowRingSource;
import cc.creativecomputing.effects.modulation.CCColumnRowSpiralSource;
import cc.creativecomputing.effects.modulation.CCPositionSource;
import cc.creativecomputing.effects.modulation.CCXYEuclidianDistanceSource;
import cc.creativecomputing.effects.modulation.CCXYManhattanDistanceSource;
import cc.creativecomputing.effects.modulation.CCXYRadialSource;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCShapeMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCVector2;

public class CCEffectableTriangle extends CCGL2Adapter {
	
	private class CCQuadEffectable extends CCEffectable{
		
		private double _myAlpha = 1;

		public CCQuadEffectable(int theId, double theX, double theY) {
			super(theId);
			position().x = theX;
			position().y = theY;
		}
		
		@Override
		public void apply(double...theValues) {
			_myAlpha = theValues[0];
		}
		
		public void draw(CCGraphics g) {
			g.pushMatrix();
			g.translate(position());
			g.color(1d,_myAlpha);
			g.rect(0,0, 20, 20);
			g.popMatrix();
		}
		
	}

	@CCProperty(name = "effects")
	private CCEffectManager<CCQuadEffectable> _myEffectManager;
	private List<CCQuadEffectable> _myCubes = new ArrayList<>();

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		int i = 0;
		for(int c = 0; c < 10; c++){
			for(int r = 0; r < 10; r++){
				CCQuadEffectable myCube = new CCQuadEffectable(i, c * 25 - 5 * 25, r * 25 - 5 * 25);
				myCube.column(c);
				myCube.row(r);
				_myCubes.add(myCube);
				i++;
			}
		}
		
		g.rectMode(CCShapeMode.CENTER);
		
		_myEffectManager = new CCEffectManager<CCQuadEffectable>(_myCubes, "a");
		_myEffectManager.addIdSources(CCEffectable.COLUMN_SOURCE, CCEffectable.ROW_SOURCE);
		_myEffectManager.addRelativeSources(
			new CCColumnRowRingSource(),
			new CCColumnRowSpiralSource(),
			new CCPositionSource("position"),
			new CCXYEuclidianDistanceSource("euclidian", 200, new CCVector2()),
			new CCXYManhattanDistanceSource("manhattan", 200, 200, new CCVector2()),
			new CCXYRadialSource("radial", new CCVector2())
		);
		_myEffectManager.put("offset", new CCOffsetEffect());
		_myEffectManager.put("signal", new CCSignalEffect());
		
	}

	@Override
	public void update(CCAnimator theAnimator) {
		_myEffectManager.update(theAnimator);
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		for(CCQuadEffectable myCube:_myCubes){
			myCube.draw(g);
		}
	}

	public static void main(String[] args) {

		CCEffectableTriangle demo = new CCEffectableTriangle();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

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
package cc.creativecomputing.graphics.util;

import java.util.Arrays;

import cc.creativecomputing.app.util.CCStopWatch;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;

/**
 * Use the stop watch class to measure the time of certain part of your application. The Stop watch watch makes it easy
 * to measure different processes and print out the values.
 * 
 * @author artcom
 * 
 */
public class CCStopWatchGraph extends CCStopWatch{

	@CCProperty(name = "width", min = 0, max = 1)
	private float _cWidth = 0;

	@CCProperty(name = "height", min = 0, max = 1)
	private float _cHeight = 0;

	@CCProperty(name = "scale", min = 0f, max = 1000f)
	private float _cScale = 20f;

	private CCColor _myColors[] = new CCColor[] { 
		new CCColor(11, 175, 181), 
		new CCColor(167, 184, 145), 
		new CCColor(59, 149, 178), 
		new CCColor(187, 173, 106),
		new CCColor(170, 143, 154), 
		new CCColor(155, 143, 170), 
		new CCColor(143, 154, 170), 
		new CCColor(143, 170, 153)
	};

	public CCStopWatchGraph() {
		super();
	}
	
	private static CCStopWatchGraph instance = null;

	public static CCStopWatchGraph instance() {
		if (instance == null) {
			instance = new CCStopWatchGraph();
		}
		return instance;
	}

	private int _myLastColorIdx = 0;
	private float[] _myLastHeights;
	private float _myLastTextHeight = 0;
	
	private void drawItem(CCGraphics g, CCStopWatchItem theItem) {

		// label
		CCColor myColor = _myColors[_myLastColorIdx];
		g.color(myColor);
		_myLastColorIdx++;
		_myLastColorIdx %= 8;

		float myWidth = g.width() * _cWidth;

		if (theItem.history().size() > 0) {
			g.text(theItem.name() + " : " + theItem.history().peek(), 10 + myWidth, 10 + (float) (theItem._myLastHeight) * _cScale);
		}

		g.color(myColor.r, myColor.g, myColor.b, 0.25f);

		// draw fill
		g.beginShape(CCDrawMode.QUAD_STRIP);
		for (int i = 0; i < theItem.history().size(); i++) {
			double myX = CCMath.map(i, 0, theItem.history().size() - 1, 0, myWidth);
			double myY = (theItem.history().get(i) + _myLastHeights[i]) * _cScale;
			double myLastY = _myLastHeights[i] * _cScale;

			g.vertex(myX, myY);
			g.vertex(myX, myLastY);
		}

		g.endShape();

		// draw lines
		g.color(myColor.r / 2, myColor.g / 2, myColor.b / 2, 0.95f);

		g.lineWidth(2.0f);
		g.beginShape(CCDrawMode.LINE_STRIP);
		_myLastTextHeight = 0;
		for (int i = 0; i < theItem.history().size(); i++) {
			double myX = CCMath.map(i, 0, theItem.history().size() - 1, 0, myWidth);
			double myY = (theItem.history().get(i) + _myLastHeights[i]) * _cScale;

			g.vertex(myX, myY);
			_myLastHeights[i] += theItem.history().get(i);
			_myLastTextHeight += _myLastHeights[i];
		}
		_myLastTextHeight /= _myLastHeights.length;
		if(theItem._myLastHeight < 0){
			theItem._myLastHeight = _myLastTextHeight;
		}else{
			theItem._myLastHeight = theItem._myLastHeight * 0.99 + _myLastTextHeight * 0.01;
		}
		g.endShape();
	}

	public void draw(CCGraphics g) {
	//	CCLog.info(_cActive, _cSamples);
		if (!active()) {
			return;
		}

		g.pushAttribute();
		float myWidth = g.width() * _cWidth;
		float myHeight = g.height() * _cHeight;

		_myLastHeights = new float[_cSamples];
		Arrays.fill(_myLastHeights, 0);
		_myLastColorIdx = 0;

		g.beginOrtho2D();

			for (CCStopWatchItem myItem : items()) {
				drawItem(g, myItem);
			}

		// draw frame and grid
		g.color(1.0f);
		g.beginShape(CCDrawMode.LINES);

		g.vertex(0, myHeight);
		g.vertex(0, 0);
		g.vertex(myWidth, 0);

		g.endShape();

		float myLockBorder = (1000.0f / 60.0f) * _cScale;

		g.text("60Hz", 3, myLockBorder);
		g.color(1.0f, 0.5f);
		g.beginShape(CCDrawMode.LINES);

		g.vertex(30, myLockBorder);
		g.vertex(myWidth, myLockBorder);

		g.endShape();

		g.endOrtho2D();
		g.popAttribute();
	}
}

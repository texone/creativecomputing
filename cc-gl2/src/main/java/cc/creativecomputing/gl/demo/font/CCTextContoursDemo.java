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
package cc.creativecomputing.gl.demo.font;


import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLApplicationManager;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCOutlineFont;
import cc.creativecomputing.graphics.font.CCTextAlign;
import cc.creativecomputing.graphics.font.CCTextContours;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CCTextContoursDemo extends CCGLApp{
	
	private class Letter{
		private Path _myPath;
		
		private double _myMax;
		private double _myMin;
		
		private double _myDelay;
		
		public Letter(final Path thePath) {
			_myPath = thePath;
			
			_myMin = CCMath.random(0.9f);
			_myMax = _myMin + CCMath.random(0.1f);
			
			_myDelay = -CCMath.random(5);
		}
		
		public void update(final double theDeltaTime) {
			_myDelay += theDeltaTime;
			
			if(_myDelay < 0)return;
			_myMin -= theDeltaTime * 0.5f;
			_myMin = CCMath.max(0, _myMin);

			_myMax += theDeltaTime * 0.5f;
			_myMax = CCMath.min(1, _myMax);
			
			if(_myMin <= 0 && _myMax >= 1){
				_myMin = CCMath.random(0.9f);
				_myMax = _myMin + CCMath.random(0.1f);
			}
		}
		
		public void draw(CCGraphics g) {
			_myPath.draw(g, _myMin, _myMax);
		}
	}
	
	private static class Path{
		
		private double _myLength = 0;
		private CCVector2 _myLastPoint;
		
		private List<Double> _myLengths = new ArrayList<>();
		private List<Double> _myDistances = new ArrayList<>();
		private List<CCVector2> _myPoints = new ArrayList<>();
		
		public void addPoint(CCVector2 thePoint) {
			if(_myLastPoint == null) {
				_myLengths.add(0d);
			}else {
				double myDistance = _myLastPoint.distance(thePoint);
				_myLength += myDistance;
				_myLengths.add(_myLength);
				_myDistances.add(myDistance);
			}
			_myLastPoint = thePoint;
			_myPoints.add(thePoint);
		}
		
		public int pointIndex(final double theBlend) {
			double myPointLength = theBlend * _myLength;
			
			int myIndex = 0;
			for(double myLength:_myLengths) {
				if(myPointLength < myLength) {
					break;
				}
				myIndex++;
			}
			
			return myIndex;
		}
		
		public CCVector2 point(final double theBlend) {
			double myPointLength = theBlend * _myLength;
			
			if(theBlend == 1f)return _myPoints.get(_myPoints.size()-1);
			if(theBlend == 0f)return _myPoints.get(0);
			
			int myIndex = 0;
			double myPosition = 0;
			for(double myLength:_myLengths) {
				if(myPointLength < myLength) {
					myPosition = myLength - myPointLength;
					break;
				}
				myIndex++;
			}
			double myBlend = myPosition / _myDistances.get(myIndex - 1);

			CCVector2 myV1 = _myPoints.get(myIndex - 1);
			CCVector2 myV2 = _myPoints.get(myIndex);
			
			return myV1.lerp(myV2, 1 - myBlend);
		}
		
		public void draw(CCGraphics g, double theMin, double theMax) {
			CCVector2 myStartPoint = point(theMin);
			CCVector2 myEndPoint = point(theMax);
			
			int myStartIndex = pointIndex(theMin);
			int myEndIndex = pointIndex(theMax);
			
			g.beginShape(CCDrawMode.LINE_STRIP);
			g.vertex(myStartPoint);
			for(int i = myStartIndex; i < myEndIndex; i++) {
				CCVector2 myVertex = _myPoints.get(i);
				g.vertex(myVertex);
			}
			g.vertex(myEndPoint);
			g.endShape();
		}
	}

	int nNumPoints = 4;

	private List<Letter> _myLetters;
	
	private CCTextContours _myTextContour;

	@Override
	public void setup() {
		CCOutlineFont font = new CCOutlineFont(null,CCNIOUtil.dataPath("fonts/Lato/Lato-Regular.ttf"), 150);
		
		_myTextContour = new CCTextContours(font, "");
		_myTextContour
			.align(CCTextAlign.CENTER)
			.text("font.getPath(myChar, CCTextAlign.CENTER, 50,myX, 0, 0);")
			.position( 0, 0);
		
		_myLetters = new ArrayList<Letter>();
		for(List<CCVector2> myContour:_myTextContour.contours()) {
			Path _myPath = new Path();
			for(CCVector2 myPoint:myContour) {
				_myPath.addPoint(myPoint);
			}
			
			_myPath.addPoint(myContour.get(0));

			_myLetters.add(new Letter(_myPath));
		}
		
		g.clearColor(0.3f);
		
	}
	
	private double _myTime = 0.001f;
	
	@Override
	public void update(final CCGLTimer theAnimator) {
		_myTime += theAnimator.deltaTime() * 0.1f;
		if(_myTime > 1)_myTime-=1;
		for(Letter myLetter:_myLetters) {
			myLetter.update(theAnimator.deltaTime());
		}
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		for(Letter myLetter:_myLetters) {
			myLetter.draw(g);
		}
		g.line(-g.width() / 2, 0, g.width() / 2, 0);
	}
	
	public static void main(String[] args) {
		CCTextContoursDemo demo = new CCTextContoursDemo();

		CCGLApplicationManager myApplicationManager = new CCGLApplicationManager(demo);
		myApplicationManager.run();
	}
}

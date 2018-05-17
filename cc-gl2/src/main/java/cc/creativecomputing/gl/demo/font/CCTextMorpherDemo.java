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

import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLApplicationManager;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCTextAlign;
import cc.creativecomputing.graphics.font.CCTextField;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.graphics.font.util.CCLoremIpsumGenerator;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;

public class CCTextMorpherDemo extends CCGLApp {
	
	public String morphString(final String theStart, final String theEnd, final double blend) {
		StringBuilder result = new StringBuilder();
		
		
		int stringLength = theStart.length() + (int)((theEnd.length() - theStart.length()) * blend);
		
		for(int i = 0; i < stringLength;i++) {
			char startChar = theStart.length() > i ? theStart.charAt(i) : 'a';
			char endChar = theEnd.length() > i ? theEnd.charAt(i) : 'a';
			
			int dif = endChar - startChar;
			char resultChar = (char)(startChar + (int)(dif * blend));
			result.append(resultChar);
		}
		
		return result.toString();
	}
	
	private String _myString1;
	private String _myString2;
	private String _myResult = "";
	
	private CCTextField _myTextField;

	@Override
	public void setup() {
		_myString1 = CCLoremIpsumGenerator.generate(10);
		_myString2 = CCLoremIpsumGenerator.generate(10);
		
		_myTextField = new CCTextField(new CCTextureMapFont(null,CCNIOUtil.dataPath("fonts/Roboto_Mono/RobotoMono-Regular.ttf"), 50, 2, 2), "")
			.position(-g.width()/2 + 10,0)
			.align(CCTextAlign.LEFT);
	}
	
	private double _myTime = 0;
	

	@Override
	public void update(final CCGLTimer theAnimator) {
		_myTime += theAnimator.deltaTime() * 0.2;
		double myBlend = CCMath.round((CCMath.sin(_myTime) + 1) / 2, 2);
		_myResult = morphString(_myString1, _myString2, myBlend);
	}

	@Override
	public void display(CCGraphics g) {
		
		g.clear();
		_myTextField.text(_myResult);
		_myTextField.draw(g);
	}

	public static void main(String[] args) {
		CCTextMorpherDemo myDemo = new CCTextMorpherDemo();

		CCGLApplicationManager myApplicationManager = new CCGLApplicationManager(myDemo);
		myApplicationManager.run();
	}
}


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
package learningopengl.chap01gettingstartet;

import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLApplicationManager;
import cc.creativecomputing.graphics.CCGraphics;

public class GL01HelloWindow extends CCGLApp {

	@Override
	public void setup() {
	}

	public void display(CCGraphics g) {
		g.clearColor(0.2, 0.3, 0.3, 1.0);
		g.clear();
	}
	
	public static void main(String[] args) {
		GL01HelloWindow demo = new GL01HelloWindow();
		
		CCGLApplicationManager myAppManager = new CCGLApplicationManager(demo);
		myAppManager.run();
	}
}

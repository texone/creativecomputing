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
package cc.creativecomputing.graphics.shader.postprocess;

import cc.creativecomputing.graphics.CCGraphics;

/**
 * @author christianriekoff
 *
 */
public abstract class CCPostProcessEffect {
	
	public abstract void initialize(int theWidth, int theHeight);

	public abstract void apply(CCGeometryBuffer theGeometryBuffer, CCGraphics g);
}

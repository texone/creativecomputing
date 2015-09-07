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
package cc.creativecomputing.model.svg;


public class CCSVGDocument extends CCSVGGroup{
	/**
	 * The width of the CCSVGDocument document.
	 */
	public double width;
	
	/**
	 * The height of the CCSVGDocument document.
	 */
	public double height;
	
	public CCSVGDocument(){
		super(null);
	}
}

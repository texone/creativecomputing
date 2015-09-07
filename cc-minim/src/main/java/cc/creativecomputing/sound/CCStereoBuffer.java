/*
 *  Copyright (c) 2007 - 2008 by Damien Di Fede <ddf@compartmental.net>
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package cc.creativecomputing.sound;


public class CCStereoBuffer implements CCAudioListener {
	public CCAudioBuffer left;
	public CCAudioBuffer right;
	public CCAudioBuffer mix;

	private CCAudioController parent;

	CCStereoBuffer(int type, int bufferSize, CCAudioController c) {
		left = new CCAudioBuffer(bufferSize);
		if (type == CCSoundIO.MONO) {
			right = left;
			mix = left;
		} else {
			right = new CCAudioBuffer(bufferSize);
			mix = new CCAudioBuffer(bufferSize);
		}
		parent = c;
	}

	@Override
	public void samples(float[] samp) {
		// Minim.debug("Got samples!");
		left.set(samp);
		parent.update();
	}

	@Override
	public void samples(float[] sampL, float[] sampR) {
		left.set(sampL);
		right.set(sampR);
		mix.mix(sampL, sampR);
		parent.update();
	}
}

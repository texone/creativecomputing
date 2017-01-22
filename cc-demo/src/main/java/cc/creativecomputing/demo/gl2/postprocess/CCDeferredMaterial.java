package cc.creativecomputing.demo.gl2.postprocess;

import cc.creativecomputing.math.CCColor;

public class CCDeferredMaterial {
	protected CCColor mColorAmbient = new CCColor(0.2);
	protected CCColor mColorDiffuse = new CCColor(0.8);
	protected CCColor mColorEmission = new CCColor(0.);
	protected CCColor mColorSpecular = new CCColor(0.);
	protected float mShininess = 0;
	protected int mPad0 = 0;
	protected int mPad1 = 0;
	protected int mPad2 = 0;

	public CCDeferredMaterial colorAmbient(final CCColor c) {
		mColorAmbient = c;
		return this;
	}

	public CCDeferredMaterial colorDiffuse(final CCColor c) {
		mColorDiffuse = c;
		return this;
	}

	public CCDeferredMaterial colorEmission(final CCColor c) {
		mColorEmission = c;
		return this;
	}

	public CCDeferredMaterial colorSpecular(final CCColor c) {
		mColorSpecular = c;
		return this;
	}

	public CCDeferredMaterial shininess(float v) {
		mShininess = v;
		return this;
	}

	public CCColor getColorAmbient() {
		return mColorAmbient;
	}

	public CCColor getColorDiffuse() {
		return mColorDiffuse;
	}

	public CCColor getColorEmission() {
		return mColorEmission;
	}

	public CCColor getColorSpecular() {
		return mColorSpecular;
	}

	public float getShininess() {
		return mShininess;
	}

	public void setColorAmbient(final CCColor c) {
		mColorAmbient = c;
	}

	public void setColorDiffuse(final CCColor c) {
		mColorDiffuse = c;
	}

	public void setColorEmission(final CCColor c) {
		mColorEmission = c;
	}

	public void setColorSpecular(final CCColor c) {
		mColorSpecular = c;
	}

	public void setShininess(float v) {
		mShininess = v;
	}
}

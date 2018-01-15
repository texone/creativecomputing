package cc.creativecomputing.demo.gl2.postprocess;

import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector3;

public class CCDeferredLight {
	protected CCColor mColorAmbient = CCColor.BLACK.clone();
	protected CCColor mColorDiffuse = CCColor.WHITE.clone();
	protected CCColor mColorSpecular = CCColor.WHITE.clone();
	protected CCVector3 mPosition = new CCVector3();
	protected float mIntensity = 1f;
	protected float mRadius = 1f;
	protected float mVolume = 10f;
    protected int mPad0 = 0;
	protected int mPad1 = 0;

	public CCDeferredLight() {
		setPosition(new CCVector3(0, 0, 0));
	}

	public CCDeferredLight colorAmbient(final CCColor c) {
		mColorAmbient = c;
		return this;
	}

	public CCDeferredLight colorDiffuse(final CCColor c) {
		mColorDiffuse = c;
		return this;
	}

	public CCDeferredLight colorSpecular(final CCColor c) {
		mColorSpecular = c;
		return this;
	}

	public CCDeferredLight position(final CCVector3 v) {
		mPosition = v;
		return this;
	}

	public CCDeferredLight intensity(float v) {
		mIntensity = v;
		return this;
	}

	public CCDeferredLight radius(float v) {
		mRadius = v;
		return this;
	}

	public CCDeferredLight volume(float v) {
		mVolume = v;
		return this;
	}

	public CCColor getColorAmbient() {
		return mColorAmbient;
	}

	public CCColor getColorDiffuse() {
		return mColorDiffuse;
	}

	public CCColor getColorSpecular() {
		return mColorSpecular;
	}

	public float getIntensity() {
		return mIntensity;
	}

	final CCVector3 getPosition() {
		return mPosition;
	}

	public float getRadius() {
		return mRadius;
	}

	public float getVolume() {
		return mVolume;
	}

	public void setColorAmbient(final CCColor c) {
		mColorAmbient = c;
	}

	public void setColorDiffuse(final CCColor c) {
		mColorDiffuse = c;
	}

	public void setColorSpecular(final CCColor c) {
		mColorSpecular = c;
	}

	public void setPosition(final CCVector3 v) {
		mPosition = v;
	}

	public void setIntensity(float v) {
		mIntensity = v;
	}

	public void setRadius(float v) {
		mRadius = v;
	}

	public void setVolume(float v) {
		mVolume = v;
	}
}

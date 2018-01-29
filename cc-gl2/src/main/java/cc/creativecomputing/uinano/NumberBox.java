package cc.creativecomputing.uinano;

import java.util.*;

import cc.creativecomputing.gl.app.CCGLAction;
import cc.creativecomputing.gl.app.CCGLMouseButton;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector2i;

/**
 * \class FloatBox textbox.h nanogui/textbox.h
 *
 * \brief A specialization of TextBox representing floating point values.
 * 
 * Template parameters should be float types, e.g. ``float``, ``double``,
 * ``float64_t``, etc.
 */
public abstract class NumberBox<NumberType extends Number> extends ValueBox<NumberType> {

	private String mNumberFormat;
	private NumberType mMouseDownValue;
	private NumberType mValueIncrement;
	private NumberType mMinValue;
	private NumberType mMaxValue;

	public NumberBox(CCWidget parent, NumberType value, NumberType theDefault, NumberType theMin, NumberType theMax,
			NumberType theIncrement, String theFormat) {
		super(parent, value, theDefault);

		mNumberFormat = createFormat();
		mFormat = theFormat;
		setValueIncrement(theIncrement);
		setMinMaxValues(theMin, theMax);
		_myText = value;
		setSpinnable(false);
	}

	/**
	 * sizeof(double) == (Float.SIZE / Byte.SIZE) ? "%.4g" : "%.7g"
	 * 
	 * @return
	 */
	public abstract String createFormat();

	public final String numberFormat() {
		return mNumberFormat;
	}

	public final void numberFormat(String format) {
		mNumberFormat = format;
	}

	public abstract NumberType clamp(NumberType theValue, NumberType theMin, NumberType theMax);

	public final void setValue(NumberType value) {
		NumberType clampedValue = clamp(value, mMinValue, mMaxValue);
		super.setValue(clampedValue);
	}

	public final void setValueIncrement(NumberType incr) {
		mValueIncrement = incr;
	}

	public final void setMinValue(NumberType minValue) {
		mMinValue = minValue;
	}

	public final void setMaxValue(NumberType maxValue) {
		mMaxValue = maxValue;
	}

	public final void setMinMaxValues(NumberType minValue, NumberType maxValue) {
		setMinValue(minValue);
		setMaxValue(maxValue);
	}

	public abstract NumberType increment(NumberType theValue, NumberType theIncrement);

	public abstract NumberType decrement(NumberType theValue, NumberType theIncrement);

	@Override
	public boolean mouseButtonEvent(CCVector2i p, CCGLMouseEvent theEvent) {
		if ((mEditable || mSpinnable) && theEvent.action == CCGLAction.PRESS) {
			mMouseDownValue = value();
		}

		SpinArea area = spinArea(p);
		if (mSpinnable && area != SpinArea.None && theEvent.action == CCGLAction.PRESS && !focused()) {
			if (area == SpinArea.Top) {
				increment(_myText, mValueIncrement);
				mCallback.proxy().event(_myText);
			} else if (area == SpinArea.Bottom) {
				decrement(_myText, mValueIncrement);
				mCallback.proxy().event(_myText);
			}
			return true;
		}

		return super.mouseButtonEvent(p, theEvent);
	}

	/**
	 * mMouseDownValue + valueDelta * mValueIncrement
	 * 
	 * @param theVal
	 * @param theDelta
	 * @param theIncrement
	 * @return
	 */
	public abstract NumberType deltaChange(NumberType theVal, int theDelta, NumberType theIncrement);

	@Override
	public boolean mouseDragEvent(CCVector2i p, CCVector2i rel, CCGLMouseEvent theEvent) {
		if (super.mouseDragEvent(p, rel, theEvent)) {
			return true;
		}
		if (mSpinnable && !focused() && theEvent.button == CCGLMouseButton.BUTTON_2 && mMouseDownPos.x != -1) {
			int valueDelta = (int) ((p.x - mMouseDownPos.x) / (float) 10);
			setValue(deltaChange(mMouseDownValue, valueDelta, mValueIncrement));
			mCallback.proxy().event(_myText);
			return true;
		}
		return false;
	}

	@Override
	public boolean scrollEvent(CCVector2i p, CCVector2 rel) {
		if (super.scrollEvent(p, rel)) {
			return true;
		}
		if (mSpinnable && !focused()) {
			int valueDelta = (rel.y > 0) ? 1 : -1;
			setValue(deltaChange(value(), valueDelta, mValueIncrement));

			mCallback.proxy().event(_myText);

			return true;
		}
		return false;
	}

}

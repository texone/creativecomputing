package cc.creativecomputing.uinano;

import org.lwjgl.nanovg.NVGPaint;

import cc.creativecomputing.core.events.CCDoubleEvent;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.gl.app.CCGLAction;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.gl.nanovg.NanoVG;
import cc.creativecomputing.gl.nanovg.NanoVG.NVGwinding;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector2i;

/**
 * \class Slider slider.h nanogui/slider.h
 *
 * \brief Fractional slider widget with mouse control.
 */
public class Slider extends CCWidget {

	protected double mValue;
	protected CCListenerManager<CCDoubleEvent> mCallback;
	protected CCListenerManager<CCDoubleEvent> mFinalCallback;
	protected CCVector2 mRange = new CCVector2();
	protected CCVector2 mHighlightedRange = new CCVector2();
	protected CCColor mHighlightCCColor = new CCColor();

	public Slider(CCWidget parent) {
		super(parent);
		this.mValue = 0.0f;
		this.mRange = new CCVector2(0.0f, 1.0f);
		this.mHighlightedRange = new CCVector2(0.0f, 0.0f);
		mHighlightCCColor = new CCColor(255, 80, 80, 70);
	}

	public final double value() {
		return mValue;
	}

	public final void setValue(double value) {
		mValue = value;
	}

	public final CCColor highlightCCColor() {
		return mHighlightCCColor;
	}

	public final void setHighlightCCColor(CCColor highlightCCColor) {
		mHighlightCCColor = highlightCCColor;
	}

	// C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in Java:
	// ORIGINAL LINE: tangible.Pair<double, double> range() const
	public final CCVector2 range() {
		return mRange;
	}

	public final void setRange(CCVector2 range) {
		mRange = range;
	}

	// C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in Java:
	// ORIGINAL LINE: tangible.Pair<double, double> highlightedRange() const
	public final CCVector2 highlightedRange() {
		return mHighlightedRange;
	}

	public final void setHighlightedRange(CCVector2 highlightedRange) {
		mHighlightedRange = highlightedRange;
	}

	@Override
	public CCVector2i preferredSize(NanoVG ctx) {
		return new CCVector2i(70, 16);
	}

	@Override
	public boolean mouseDragEvent(CCVector2i p, CCVector2i rel, CCGLMouseEvent theEvent) {
		if (!_myIsEnabled) {
			return false;
		}

		final double kr = (int) (_mySize.y * 0.4f);
		final double kshadow = 3F;
		final double startX = kr + kshadow + _myPosition.x - 1;
		final double widthX = _mySize.x - 2 * (kr + kshadow);

		double value = (p.x - startX) / widthX;
		value = value * (mRange.y - mRange.x) + mRange.x;
		mValue = Math.min(Math.max(value, mRange.x), mRange.y);
		mCallback.proxy().event(mValue);
		return true;
	}

	@Override
	public boolean mouseButtonEvent(CCVector2i p, CCGLMouseEvent theEvent) {
		if (!_myIsEnabled) {
			return false;
		}

		final double kr = (int) (_mySize.y * 0.4f);
		final double kshadow = 3F;
		final double startX = kr + kshadow + _myPosition.x - 1;
		final double widthX = _mySize.x - 2 * (kr + kshadow);

		double value = (p.x - startX) / widthX;
		value = value * (mRange.y - mRange.x) + mRange.x;
		mValue = Math.min(Math.max(value, mRange.x), mRange.y);

		mCallback.proxy().event(mValue);

		if (theEvent.action != CCGLAction.PRESS) {
			mFinalCallback.proxy().event(mValue);
		}
		return true;
	}

	@Override
	public void draw(NanoVG ctx) {
		CCVector2 center = new CCVector2(_myPosition.x + _mySize.x * 0.5, _myPosition.y + _mySize.y * 0.5);
		double kr = (int) (_mySize.y * 0.4f);
		double kshadow = 3F;

		double startX = kr + kshadow + _myPosition.x;
		double widthX = _mySize.x - 2 * (kr + kshadow);

		CCVector2 knobPos = new CCVector2(startX + (mValue - mRange.x) / (mRange.y - mRange.x) * widthX,
				center.y + 0.5f);

		NVGPaint bg = ctx.boxGradient(startX, center.y - 3 + 1, widthX, 6, 3, 3, new CCColor(0, _myIsEnabled ? 32 : 10),
				new CCColor(0, _myIsEnabled ? 128 : 210));

		ctx.beginPath();
		ctx.roundedRect(startX, center.y - 3 + 1, widthX, 6, 2);
		ctx.fillPaint(bg);
		ctx.fill();

		if (mHighlightedRange.y != mHighlightedRange.x) {
			ctx.beginPath();
			ctx.roundedRect(startX + mHighlightedRange.x * _mySize.x, center.y - kshadow + 1,
					widthX * (mHighlightedRange.y - mHighlightedRange.x), kshadow * 2, 2);
			ctx.fillColor(mHighlightCCColor);
			ctx.fill();
		}

		NVGPaint knobShadow = ctx.radialGradient(knobPos.x, knobPos.y, kr - kshadow, kr + kshadow,
				new CCColor(0, 64), _myTheme.mTransparent);

		ctx.beginPath();
		ctx.rect(knobPos.x - kr - 5, knobPos.y - kr - 5, kr * 2 + 10, kr * 2 + 10 + kshadow);
		ctx.circle(knobPos.x, knobPos.y, kr);
		ctx.pathWinding(NVGwinding.CW);
		ctx.fillPaint(knobShadow);
		ctx.fill();

		NVGPaint knob = ctx.linearGradient(_myPosition.x, center.y - kr, _myPosition.x, center.y + kr, _myTheme.mBorderLight,
				_myTheme.mBorderMedium);
		NVGPaint knobReverse = ctx.linearGradient(_myPosition.x, center.y - kr, _myPosition.x, center.y + kr,
				_myTheme.mBorderMedium, _myTheme.mBorderLight);

		ctx.beginPath();
		ctx.circle(knobPos.x, knobPos.y, kr);
		ctx.strokeColor(_myTheme.mBorderDark);
		ctx.fillPaint(knob);
		ctx.stroke();
		ctx.fill();
		ctx.beginPath();
		ctx.circle(knobPos.x, knobPos.y, kr / 2);
		ctx.fillColor(new CCColor(150, _myIsEnabled ? 255 : 100));
		ctx.strokePaint(knobReverse);
		ctx.stroke();
		ctx.fill();
	}

	@Override
	public void save(CCDataElement s) {
		super.save(s);
		s.addAttribute("value", mValue);
		s.add("range", mRange);
		s.add("highlightedRange", mHighlightedRange);
		s.add("highlightCCColor", mHighlightCCColor);
	}

	@Override
	public boolean load(CCDataElement s) {
		if (!super.load(s)) {
			return false;
		}
		try {
			mValue = s.doubleAttribute("value");
			mRange = s.vector2("range");
			mHighlightedRange = s.vector2("highlightedRange");
			mHighlightCCColor = s.color("highlightCCColor");
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
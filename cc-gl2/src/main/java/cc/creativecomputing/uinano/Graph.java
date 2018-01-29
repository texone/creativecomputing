package cc.creativecomputing.uinano;

import java.util.*;

import cc.creativecomputing.gl.nanovg.NanoVG;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2i;

/**
 * \class Graph graph.h nanogui/graph.h
 *
 * \brief Simple graph widget for showing a function plot.
 */
public class Graph extends CCWidget {

	protected String mCaption;
	protected String mHeader;
	protected String mFooter;
	protected CCColor mBackgroundColor = new CCColor();
	protected CCColor mForegroundColor = new CCColor();
	protected CCColor mTextColor = new CCColor();
	protected List<Double> mValues = new ArrayList<>();

	public Graph(CCWidget parent) {
		this(parent, "Untitled");
	}

	public Graph(CCWidget parent, String caption) {
		super(parent);
		this.mCaption = caption;
		mBackgroundColor = new CCColor(20, 128);
		mForegroundColor = new CCColor(255, 192, 0, 128);
		mTextColor = new CCColor(240, 192);
	}

	public final String caption() {
		return mCaption;
	}

	public final void setCaption(String caption) {
		mCaption = caption;
	}

	public final String header() {
		return mHeader;
	}

	public final void setHeader(String header) {
		mHeader = header;
	}

	public final String footer() {
		return mFooter;
	}

	public final void setFooter(String footer) {
		mFooter = footer;
	}

	public final CCColor backgroundColor() {
		return mBackgroundColor;
	}

	public final void setBackgroundColor(CCColor backgroundColor) {
		mBackgroundColor = backgroundColor;
	}

	public final CCColor foregroundColor() {
		return mForegroundColor;
	}

	public final void setForegroundColor(CCColor foregroundColor) {
		mForegroundColor = foregroundColor;
	}

	public final CCColor textColor() {
		return mTextColor;
	}

	public final void setTextColor(CCColor textColor) {
		mTextColor = textColor;
	}

	public final List<Double> values() {
		return mValues;
	}

	public final void setValues(List<Double> values) {
		mValues = values;
	}

	// C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in Java:
	// ORIGINAL LINE: virtual CCVector2i preferredSize(NanoVG * ctx) const
	// override
	@Override
	public CCVector2i preferredSize(NanoVG ctx) {
		return new CCVector2i(180, 45);
	}

	@Override
	public void draw(NanoVG ctx) {
		super.draw(ctx);

		ctx.beginPath();
		ctx.rect(_myPosition.x, _myPosition.y, _mySize.x, _mySize.y);
		ctx.fillColor(mBackgroundColor);
		ctx.fill();

		if (mValues.size() < 2) {
			return;
		}

		ctx.beginPath();
		ctx.moveTo(_myPosition.x, _myPosition.y + _mySize.y);
		for (int i = 0; i < mValues.size(); i++) {
			double value = mValues.get(i);
			double vx = _myPosition.x + i * _mySize.x / (double) (mValues.size() - 1);
			double vy = _myPosition.y + (1 - value) * _mySize.y;
			ctx.lineTo(vx, vy);
		}

		ctx.lineTo(_myPosition.x + _mySize.x, _myPosition.y + _mySize.y);
		ctx.strokeColor(new CCColor(100, 255));
		ctx.stroke();
		ctx.fillColor(mForegroundColor);
		ctx.fill();

		ctx.fontFace("sans");

		if (!mCaption.isEmpty()) {
			ctx.fontSize(14.0f);
			ctx.textAlign(NanoVG.ALIGN_LEFT | NanoVG.ALIGN_TOP);
			ctx.fillColor(mTextColor);
			ctx.text(_myPosition.x + 3, _myPosition.y + 1, mCaption);
		}

		if (!mHeader.isEmpty()) {
			ctx.fontSize(18.0f);
			ctx.textAlign(NanoVG.ALIGN_RIGHT | NanoVG.ALIGN_TOP);
			ctx.fillColor(mTextColor);
			ctx.text(_myPosition.x + _mySize.x - 3, _myPosition.y + 1, mHeader);
		}

		if (!mFooter.isEmpty()) {
			ctx.fontSize(15.0f);
			ctx.textAlign(NanoVG.ALIGN_RIGHT | NanoVG.ALIGN_BOTTOM);
			ctx.fillColor(mTextColor);
			ctx.text(_myPosition.x + _mySize.x - 3, _myPosition.y + _mySize.y - 1, mFooter);
		}

		ctx.beginPath();
		ctx.rect(_myPosition.x, _myPosition.y, _mySize.x, _mySize.y);
		ctx.strokeColor(new CCColor(100, 255));
		ctx.stroke();
	}

	@Override
	public void save(CCDataElement s) {
		super.save(s);
		s.addAttribute("caption", mCaption);
		s.addAttribute("header", mHeader);
		s.addAttribute("footer", mFooter);
		s.add("backgroundColor", mBackgroundColor);
		s.add("foregroundColor", mForegroundColor);
		s.add("textColor", mTextColor);
		// s.set("values", mValues);
	}

	@Override
	public boolean load(CCDataElement s) {
		if (!super.load(s)) {
			return false;
		}
		try {
			mCaption = s.attribute("caption");
			mHeader = s.attribute("header");
			mFooter = s.attribute("footer");
			mBackgroundColor = s.color("backgroundColor");
			mForegroundColor = s.color("foregroundColor");
			mTextColor = s.color("textColor");
			// s.get("values", mValues)
		} catch (Exception e) {
			return false;
		}

		return true;
	}
}
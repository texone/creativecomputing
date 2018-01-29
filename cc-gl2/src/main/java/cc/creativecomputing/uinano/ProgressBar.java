package cc.creativecomputing.uinano;

import org.lwjgl.nanovg.NVGPaint;

import cc.creativecomputing.gl.nanovg.NanoVG;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2i;

/**
 * \class ProgressBar progressbar.h nanogui/progressbar.h
 *
 * \brief Standard widget for visualizing progress.
 */
public class ProgressBar extends CCWidget {

	protected float mValue;

	public ProgressBar(CCWidget parent) {
		super(parent);
		this.mValue = 0.0f;
	}

	public final float value() {
		return mValue;
	}

	public final void setValue(float value) {
		mValue = value;
	}

	@Override
	public CCVector2i preferredSize(NanoVG ctx) {
		return new CCVector2i(70, 12);
	}

	@Override
	public void draw(NanoVG ctx) {
		super.draw(ctx);

		NVGPaint paint = ctx.boxGradient(_myPosition.x + 1, _myPosition.y + 1, _mySize.x - 2, _mySize.y, 3, 4, new CCColor(0, 32),
				new CCColor(0, 92));
		ctx.beginPath();
		ctx.roundedRect(_myPosition.x, _myPosition.y, _mySize.x, _mySize.y, 3);
		ctx.fillPaint(paint);
		ctx.fill();

		float value = Math.min(Math.max(0.0f, mValue), 1.0f);
		int barPos = (int) Math.round((_mySize.x - 2) * value);

		paint = ctx.boxGradient(_myPosition.x, _myPosition.y, barPos + 1.5f, _mySize.y - 1, 3, 4, new CCColor(220, 100),
				new CCColor(128, 100));

		ctx.beginPath();
		ctx.roundedRect(_myPosition.x + 1, _myPosition.y + 1, barPos, _mySize.y - 2, 3);
		ctx.fillPaint(paint);
		ctx.fill();
	}

	@Override
	public void save(CCDataElement s) {
		super.save(s);
		s.addAttribute("value", mValue);
	}

	@Override
	public boolean load(CCDataElement s) {
		if (!super.load(s)) {
			return false;
		}
		try{
			mValue = s.floatAttribute("value");
		}catch(Exception e){
			return false;
		}
		return true;
	}
}
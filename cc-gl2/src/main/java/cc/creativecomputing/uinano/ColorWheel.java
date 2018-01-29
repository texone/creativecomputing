package cc.creativecomputing.uinano;
import java.util.*;

import org.lwjgl.nanovg.NVGPaint;

import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.gl.app.CCGLAction;
import cc.creativecomputing.gl.app.CCGLMouseButton;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.gl.nanovg.NanoVG;
import cc.creativecomputing.gl.nanovg.NanoVG.NVGwinding;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCColor.CCColorEvent;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2i;



/**
 * \class ColorWheel colorwheel.h nanogui/colorwheel.h
 *
 * \brief Fancy analog widget to select a color value.  This widget was
 *        contributed by Dmitriy Morozov.
 */
public class ColorWheel extends CCWidget
{

	/// The current Hue in the HSV color model.
	protected double mHue;

	/**
	 * The implicit Value component of the HSV color model.  See implementation
	 * \ref nanogui::ColorWheel::color for its usage.  Valid values are in the
	 * range ``[0, 1]``.
	 */
	protected double mWhite;

	/**
	 * The implicit Saturation component of the HSV color model.  See implementation
	 * \ref nanogui::ColorWheel::color for its usage.  Valid values are in the
	 * range ``[0, 1]``.
	 */
	protected double mBlack;

	/// The current region the mouse is interacting with.
	protected Region mDragRegion;

	/// The current callback to execute when the color value has changed.
	protected final CCListenerManager<CCColorEvent> mCallback = CCListenerManager.create(CCColorEvent.class);
	
	/**
	 * Adds a ColorWheel to the specified parent.
	 *
	 * \param parent
	 *     The Widget to add this ColorWheel to.
	 *
	 * \param color
	 *     The initial color of the ColorWheel (default: Red).
	 */
	public ColorWheel(CCWidget parent)
	{
		this(parent, new CCColor(1.0f, 0.0f, 0.0f, 1.0f));
	}
	public ColorWheel(CCWidget parent, CCColor rgb)
	{
		super(parent);
		this.mDragRegion = Region.None;
		setColor(rgb);
	}



	/// The current CCColor this ColorWheel has selected.
	public final CCColor color()
	{
		CCColor rgb = CCColor.createFromHSB(mHue, 1d, 1d);
		return new CCColor(
			rgb.r * (1 - mWhite - mBlack) + mBlack + mWhite,
			rgb.g * (1 - mWhite - mBlack) + mBlack + mWhite,
			rgb.b * (1 - mWhite - mBlack) + mBlack + mWhite
		);
	}

	/// Sets the current CCColor this ColorWheel has selected.
	public final void setColor(CCColor rgb)
	{
		double r = rgb.r;
		double g = rgb.g;
		double b = rgb.b;

		double max = CCMath.max(r, g, b);
		double min = CCMath.min(r, g, b);
		double l = (max + min) / 2;

		if (max == min)
		{
			mHue = 0.0;
			mBlack = 1.0 - l;
			mWhite = l;
		}
		else
		{
			double d = max - min;
			double h;
			/* double s = l > 0.5 ? d / (2 - max - min) : d / (max + min); */
			if (max == r)
			{
				h = (g - b) / d + (g < b ? 6 : 0);
			}
			else if (max == g)
			{
				h = (b - r) / d + 2;
			}
			else
			{
				h = (r - g) / d + 4;
			}
			h /= 6;

			mHue = h;

			//TODO replace this
//			Eigen.Matrix<Float, 4, 3> M = new Eigen.Matrix<Float, 4, 3>();
//			M.<3, 1>topLeftCorner() = hue2rgb(h).head < 3>();
//			M(3, 0) = 1.0;
//			M.col(1) = Vector4f
//			{
//				0.0, 0.0, 0.0, 1.
//			};
//			M.col(2) = Vector4f
//			{
//				1.0, 1.0, 1.0, 1.
//			};
//
//			Vector4f rgb4 = new Vector4f(rgb[0], rgb[1], rgb[2], 1.0);
//			Vector3f bary = M.colPivHouseholderQr().solve(rgb4);
//
//			mBlack = bary[1];
//			mWhite = bary[2];
		}
	}

	/// The preferred size of this ColorWheel.
	@Override
	public CCVector2i preferredSize(NanoVG ctx)
	{
		return new CCVector2i(100, 100);
	}

	/// Draws the ColorWheel.
	@Override
	public void draw(NanoVG vg)
	{
		super.draw(vg);

		if (!_myIsVisible)
		{
			return;
		}

		double x = _myPosition.x;
		double y = _myPosition.y;
		double w = _mySize.x;
		double h = _mySize.y;


		int i;
		double r0;
		double r1;
		double ax;
		double ay;
		double bx;
		double by;
		double cx;
		double cy;
		double aeps;
		double r;
		double hue = mHue;
		NVGPaint paint = NVGPaint.create();

		vg.save();

		cx = x + w * 0.5f;
		cy = y + h * 0.5f;
		r1 = (w < h ? w : h) * 0.5f - 5.0f;
		r0 = r1 * .75f;

		aeps = 0.5f / r1; // half a pixel arc length in radians (2pi cancels out).

		for (i = 0; i < 6; i++)
		{
			double a0 = (double)i / 6.0f * CCMath.PI * 2.0f - aeps;
			double a1 = (double)(i + 1.0f) / 6.0f * CCMath.PI * 2.0f + aeps;
			vg.beginPath();
			vg.arc(cx,cy, r0, a0, a1, NVGwinding.CW);
			vg.arc(cx,cy, r1, a1, a0, NVGwinding.CCW);
			vg.closePath();
			ax = cx + CCMath.cos(a0) * (r0 + r1) * 0.5f;
			ay = cy + CCMath.sin(a0) * (r0 + r1) * 0.5f;
			bx = cx + CCMath.cos(a1) * (r0 + r1) * 0.5f;
			by = cy + CCMath.sin(a1) * (r0 + r1) * 0.5f;
			paint = vg.linearGradient(ax, ay, bx, by, CCColor.createFromHSB(a0 / (CCMath.PI * 2), 1.0f, 0.55f, 1f), CCColor.createFromHSB(a1 / (CCMath.PI * 2), 1.0f, 0.55f, 1f));
			vg.fillPaint(paint);
			vg.fill();
		}

		vg.beginPath();
		vg.circle(cx,cy, r0 - 0.5f);
		vg.circle(cx,cy, r1 + 0.5f);
		vg.strokeColor(new CCColor(0,0,0,64));
		vg.strokeWidth(1.0f);
		vg.stroke();

		// Selector
		vg.save();
		vg.translate(cx,cy);
		vg.rotate(hue * CCMath.PI * 2);

		// Marker on
		double u = CCMath.max(r1 / 50, 1.5f);
			  u = CCMath.min(u, 4.0f);
		vg.strokeWidth(u);
		vg.beginPath();
		vg.rect(r0 - 1,-2 * u,r1 - r0 + 2,4 * u);
		vg.strokeColor(new CCColor(255,255,255,192));
		vg.stroke();

		paint = vg.boxGradient(r0 - 3,-5,r1 - r0 + 6,10, 2,4, new CCColor(0,0,0,128), new CCColor(0,0,0,0));
		vg.beginPath();
		vg.rect(r0 - 2 - 10,-4 - 10,r1 - r0 + 4 + 20,8 + 20);
		vg.rect(r0 - 2,-4,r1 - r0 + 4,8);
		vg.pathWinding(NVGwinding.CW);
		vg.fillPaint(paint);
		vg.fill();

		// Center triangle
		r = r0 - 6;
		ax = CCMath.cos(120.0f / 180.0f * CCMath.PI) * r;
		ay = CCMath.sin(120.0f / 180.0f * CCMath.PI) * r;
		bx = CCMath.cos(-120.0f / 180.0f * CCMath.PI) * r;
		by = CCMath.sin(-120.0f / 180.0f * CCMath.PI) * r;
		vg.beginPath();
		vg.moveTo(r,0);
		vg.lineTo(ax, ay);
		vg.lineTo(bx, by);
		vg.closePath();
		paint = vg.linearGradient(r, 0, ax, ay, CCColor.createFromHSB(hue, 1.0f, 0.5f, 1d), new CCColor(255, 255, 255, 255));
		vg.fillPaint(paint);
		vg.fill();
		paint = vg.linearGradient((r + ax) * 0.5f, (0 + ay) * 0.5f, bx, by, new CCColor(0, 0, 0, 0), new CCColor(0, 0, 0, 255));
		vg.fillPaint(paint);
		vg.fill();
		vg.strokeColor(new CCColor(0, 0, 0, 64));
		vg.stroke();

		// Select circle on triangle
		double sx = r * (1 - mWhite - mBlack) + ax * mWhite + bx * mBlack;
		double sy = ay * mWhite + by * mBlack;

		vg.strokeWidth(u);
		vg.beginPath();
		vg.circle(sx,sy,2 * u);
		vg.strokeColor(new CCColor(255,255,255,192));
		vg.stroke();

		vg.restore();

		vg.restore();
	}

	/// Handles mouse button click events for the ColorWheel.
	@Override
	public boolean mouseButtonEvent(CCVector2i p, CCGLMouseEvent theEvent)
	{
		super.mouseButtonEvent(p, theEvent);
		if (!_myIsEnabled || theEvent.button != CCGLMouseButton.BUTTON_1)
		{
			return false;
		}

		if (theEvent.action == CCGLAction.PRESS)
		{
			mDragRegion = adjustPosition(p);
			return mDragRegion != Region.None;
		}
		else
		{
			mDragRegion = Region.None;
			return true;
		}
	}

	/// Handles mouse drag events for the ColorWheel.
	@Override
	public boolean mouseDragEvent(CCVector2i p, CCVector2i rel, CCGLMouseEvent theEvent)
	{
		return adjustPosition(p, mDragRegion) != Region.None;
	}

	/// Saves the current state of this ColorWheel to the specified Serializer.
	@Override
	public void save(CCDataElement s)
	{
		super.save(s);
		s.addAttribute("hue", mHue);
		s.addAttribute("white", mWhite);
		s.addAttribute("black", mBlack);
	}

	/// Sets the state of this ColorWheel using the specified Serializer.
	@Override
	public boolean load(CCDataElement s)
	{
		if (!super.load(s))
		{
			return false;
		}
		try {
			mHue = s.doubleAttribute("hue");
			mWhite = s.doubleAttribute("white");
			mBlack = s.doubleAttribute("black");
		} catch (Exception e) {
			return false;
		}
		mDragRegion = Region.None;
		return true;
	}

	// Used to describe where the mouse is interacting
	private enum Region
	{
		None,
		InnerTriangle,
		OuterCircle,
		Both;

		
	}

	

	// Manipulates the positioning of the different regions of the ColorWheel.
	private ColorWheel.Region adjustPosition(CCVector2i p)
	{
		return adjustPosition(p, Region.Both);
	}
	private ColorWheel.Region adjustPosition(CCVector2i p, Region consideredRegions)
	{
		double x = p.x - _myPosition.x;
		double y = p.y - _myPosition.y;
		double w = _mySize.x;
		double h = _mySize.y;

		double cx = w * 0.5f;
		double cy = h * 0.5f;
		double r1 = (w < h ? w : h) * 0.5f - 5.0f;
		double r0 = r1 * .75f;

		x -= cx;
		y -= cy;

		double mr = CCMath.sqrt(x * x + y * y);

//		if ((consideredRegions & OuterCircle) && ((mr >= r0  && mr <= r1) || (consideredRegions == Region.OuterCircle)))
//		{
//			if ((consideredRegions & OuterCircle) == null)
//			{
//				return Region.None;
//			}
//			mHue = CCMath.atan(y / x);
//			if (x < 0F)
//			{
//				mHue += CCMath.PI;
//			}
//			mHue /= 2 * CCMath.PI;
//
//			
//				mCallback.proxy().event(color());
//			
//
//			return Region.OuterCircle;
//		}

		double r = r0 - 6;

		double ax = CCMath.cos(120.0f / 180.0f * CCMath.PI) * r;
		double ay = CCMath.sin(120.0f / 180.0f * CCMath.PI) * r;
		double bx = CCMath.cos(-120.0f / 180.0f * CCMath.PI) * r;
		double by = CCMath.sin(-120.0f / 180.0f * CCMath.PI) * r;


//		Eigen.Matrix<Float, 2, 3> triangle = new Eigen.Matrix<Float, 2, 3>();
//		triangle << ax,bx,r, ay,by,0;
//		triangle = Eigen.<Float>Rotation2D(mHue * 2 * CCCCMath.PI).matrix * triangle;
//
//		Eigen.Matrix<Float,2,2> T = new Eigen.Matrix<Float,2,2>();
//		T << triangle(0,0) - triangle(0,2), triangle(0,1) - triangle(0,2), triangle(1,0) - triangle(1,2), triangle(1,1) - triangle(1,2);
//		Vector2f pos = new Vector2f(x - triangle(0,2), y - triangle(1,2));
//
//		Vector2f bary = T.colPivHouseholderQr().solve(pos);
//		double l0 = bary[0];
//		double l1 = bary[1];
//		double l2 = 1 - l0 - l1;
//		boolean triangleTest = l0 >= 0F && l0 <= 1.0f && l1 >= 0.0f && l1 <= 1.0f && l2 >= 0.0f && l2 <= 1.0f;
//
//		if ((consideredRegions & InnerTriangle) && (triangleTest || consideredRegions == InnerTriangle))
//		{
//			if ((consideredRegions & InnerTriangle) == null)
//			{
//				return None;
//			}
//			l0 = CCMath.min(CCMath.max(0.0f, l0), 1.0f);
//			l1 = CCMath.min(CCMath.max(0.0f, l1), 1.0f);
//			l2 = CCMath.min(CCMath.max(0.0f, l2), 1.0f);
//			double sum = l0 + l1 + l2;
//			l0 /= sum;
//			l1 /= sum;
//			mWhite = l0;
//			mBlack = l1;
//			if (mCallback)
//			{
//				mCallback(color());
//			}
//			return InnerTriangle;
//		}

		return Region.None;
	}


}
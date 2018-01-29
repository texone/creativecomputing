package cc.creativecomputing.gl.nanovg;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_BASELINE;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_BOTTOM;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_MIDDLE;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_RIGHT;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_TOP;
import static org.lwjgl.nanovg.NanoVG.NVG_BEVEL;
import static org.lwjgl.nanovg.NanoVG.NVG_BUTT;
import static org.lwjgl.nanovg.NanoVG.NVG_CCW;
import static org.lwjgl.nanovg.NanoVG.NVG_CW;
import static org.lwjgl.nanovg.NanoVG.NVG_MITER;
import static org.lwjgl.nanovg.NanoVG.NVG_ROUND;
import static org.lwjgl.nanovg.NanoVG.NVG_SQUARE;
import static org.lwjgl.nanovg.NanoVG.nvgAddFallbackFont;
import static org.lwjgl.nanovg.NanoVG.nvgAddFallbackFontId;
import static org.lwjgl.nanovg.NanoVG.nvgArc;
import static org.lwjgl.nanovg.NanoVG.nvgArcTo;
import static org.lwjgl.nanovg.NanoVG.nvgBeginFrame;
import static org.lwjgl.nanovg.NanoVG.nvgBeginPath;
import static org.lwjgl.nanovg.NanoVG.nvgBezierTo;
import static org.lwjgl.nanovg.NanoVG.nvgBoxGradient;
import static org.lwjgl.nanovg.NanoVG.nvgCancelFrame;
import static org.lwjgl.nanovg.NanoVG.nvgCircle;
import static org.lwjgl.nanovg.NanoVG.nvgClosePath;
import static org.lwjgl.nanovg.NanoVG.nvgCreateFont;
import static org.lwjgl.nanovg.NanoVG.nvgCreateFontMem;
import static org.lwjgl.nanovg.NanoVG.nvgCreateImage;
import static org.lwjgl.nanovg.NanoVG.nvgCreateImageMem;
import static org.lwjgl.nanovg.NanoVG.nvgCreateImageRGBA;
import static org.lwjgl.nanovg.NanoVG.nvgCurrentTransform;
import static org.lwjgl.nanovg.NanoVG.nvgDeleteImage;
import static org.lwjgl.nanovg.NanoVG.nvgEllipse;
import static org.lwjgl.nanovg.NanoVG.nvgEndFrame;
import static org.lwjgl.nanovg.NanoVG.nvgFill;
import static org.lwjgl.nanovg.NanoVG.nvgFillColor;
import static org.lwjgl.nanovg.NanoVG.nvgFillPaint;
import static org.lwjgl.nanovg.NanoVG.nvgFindFont;
import static org.lwjgl.nanovg.NanoVG.nvgFontBlur;
import static org.lwjgl.nanovg.NanoVG.nvgFontFace;
import static org.lwjgl.nanovg.NanoVG.nvgFontFaceId;
import static org.lwjgl.nanovg.NanoVG.nvgFontSize;
import static org.lwjgl.nanovg.NanoVG.nvgGlobalAlpha;
import static org.lwjgl.nanovg.NanoVG.nvgGlobalCompositeBlendFunc;
import static org.lwjgl.nanovg.NanoVG.nvgGlobalCompositeBlendFuncSeparate;
import static org.lwjgl.nanovg.NanoVG.nvgGlobalCompositeOperation;
import static org.lwjgl.nanovg.NanoVG.nvgImagePattern;
import static org.lwjgl.nanovg.NanoVG.nvgImageSize;
import static org.lwjgl.nanovg.NanoVG.nvgIntersectScissor;
import static org.lwjgl.nanovg.NanoVG.nvgLineCap;
import static org.lwjgl.nanovg.NanoVG.nvgLineJoin;
import static org.lwjgl.nanovg.NanoVG.nvgLineTo;
import static org.lwjgl.nanovg.NanoVG.nvgLinearGradient;
import static org.lwjgl.nanovg.NanoVG.nvgMiterLimit;
import static org.lwjgl.nanovg.NanoVG.nvgMoveTo;
import static org.lwjgl.nanovg.NanoVG.nvgPathWinding;
import static org.lwjgl.nanovg.NanoVG.nvgQuadTo;
import static org.lwjgl.nanovg.NanoVG.nvgRadialGradient;
import static org.lwjgl.nanovg.NanoVG.nvgRect;
import static org.lwjgl.nanovg.NanoVG.nvgReset;
import static org.lwjgl.nanovg.NanoVG.nvgResetScissor;
import static org.lwjgl.nanovg.NanoVG.nvgResetTransform;
import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.nanovg.NanoVG.nvgRotate;
import static org.lwjgl.nanovg.NanoVG.nvgRoundedRect;
import static org.lwjgl.nanovg.NanoVG.nvgRoundedRectVarying;
import static org.lwjgl.nanovg.NanoVG.nvgSave;
import static org.lwjgl.nanovg.NanoVG.nvgScale;
import static org.lwjgl.nanovg.NanoVG.nvgScissor;
import static org.lwjgl.nanovg.NanoVG.nvgShapeAntiAlias;
import static org.lwjgl.nanovg.NanoVG.nvgSkewX;
import static org.lwjgl.nanovg.NanoVG.nvgSkewY;
import static org.lwjgl.nanovg.NanoVG.nvgStroke;
import static org.lwjgl.nanovg.NanoVG.nvgStrokeColor;
import static org.lwjgl.nanovg.NanoVG.nvgStrokePaint;
import static org.lwjgl.nanovg.NanoVG.nvgStrokeWidth;
import static org.lwjgl.nanovg.NanoVG.nvgText;
import static org.lwjgl.nanovg.NanoVG.nvgTextAlign;
import static org.lwjgl.nanovg.NanoVG.nvgTextBounds;
import static org.lwjgl.nanovg.NanoVG.nvgTextBox;
import static org.lwjgl.nanovg.NanoVG.nvgTextBoxBounds;
import static org.lwjgl.nanovg.NanoVG.nvgTextGlyphPositions;
import static org.lwjgl.nanovg.NanoVG.nvgTextLetterSpacing;
import static org.lwjgl.nanovg.NanoVG.nvgTextLineHeight;
import static org.lwjgl.nanovg.NanoVG.nvgTransform;
import static org.lwjgl.nanovg.NanoVG.nvgTranslate;
import static org.lwjgl.nanovg.NanoVG.nvgUpdateImage;
import static org.lwjgl.nanovg.NanoVGGL3.NVG_ANTIALIAS;
import static org.lwjgl.nanovg.NanoVGGL3.nvgCreate;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGGlyphPosition;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.system.MemoryStack;

import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMatrix32;
import cc.creativecomputing.math.CCVector2i;

public class NanoVG {
	
	public static int ALIGN_BASELINE = NVG_ALIGN_BASELINE;
	public static int ALIGN_BOTTOM = NVG_ALIGN_BOTTOM;
	public static int ALIGN_CENTER = NVG_ALIGN_CENTER;
	public static int ALIGN_MIDDLE = NVG_ALIGN_MIDDLE;
	public static int ALIGN_RIGHT = NVG_ALIGN_RIGHT;
	public static int ALIGN_LEFT = NVG_ALIGN_LEFT;
	public static int ALIGN_TOP = NVG_ALIGN_TOP;

	private long _myContext;
	
	public NanoVG(){
		_myContext = nvgCreate(NVG_ANTIALIAS);
		
		
		
	}
	
	private NVGColor convertColor(CCColor theColor){
		NVGColor myColor = NVGColor.create();
		myColor.r((float)theColor.r);
		myColor.g((float)theColor.g);
		myColor.b((float)theColor.b);
		myColor.a((float)theColor.a);
		return myColor;
	}
	
	// Begin drawing a new frame
	// Calls to nanovg drawing API should be wrapped in nvgBeginFrame() & nvgEndFrame()
	// nvgBeginFrame() defines the size of the window to render to in relation currently
	// set viewport (i.e. glViewport on GL backends). Device pixel ration allows to
	// control the rendering on Hi-DPI devices.
	// For example, GLFW returns two dimension for an opened window: window size and
	// frame buffer size. In that case you would set windowWidth/Height to the window size
	// devicePixelRatio to: frameBufferWidth / windowWidth.
	public void beginFrame(int windowWidth, int windowHeight, float devicePixelRatio){
		nvgBeginFrame(_myContext, windowWidth, windowHeight, devicePixelRatio);
	}

	// Cancels drawing the current frame.
	public void cancelFrame(){
		nvgCancelFrame(_myContext);
	}

	// Ends drawing flushing remaining render state.
	public void endFrame(){
		nvgEndFrame(_myContext);
	}

	public static enum NVGcompositeOperation {
		SOURCE_OVER(NVG_SOURCE_OVER),
		SOURCE_IN(NVG_SOURCE_IN),
		SOURCE_OUT(NVG_SOURCE_OUT),
		ATOP(NVG_ATOP),
		DESTINATION_OVER(NVG_DESTINATION_OVER),
		DESTINATION_IN(NVG_DESTINATION_IN),
		DESTINATION_OUT(NVG_DESTINATION_OUT),
		DESTINATION_ATOP(NVG_DESTINATION_ATOP),
		LIGHTER(NVG_LIGHTER),
		COPY(NVG_COPY),
		XOR(NVG_XOR);
		
		private final int _myID;

		private NVGcompositeOperation(int theID) {
			_myID = theID;
		}

		public int id() {
			return _myID;
		}
	}
	
	//
	// Composite operation
	//
	// The composite operations in NanoVG are modeled after HTML Canvas API, and
	// the blend func is based on OpenGL (see corresponding manuals for more info).
	// The colors in the blending state have premultiplied alpha.

	// Sets the composite operation. The op parameter should be one of NVGcompositeOperation.
	public void globalCompositeOperation(NVGcompositeOperation op){
		nvgGlobalCompositeOperation(_myContext, op.id());
	}
	
	public static enum NVGblendFactor {
		ZERO(NVG_ZERO),
		ONE(NVG_ONE),
		SRC_COLOR(NVG_SRC_COLOR),
		ONE_MINUS_SRC_COLOR(NVG_ONE_MINUS_SRC_COLOR),
		DST_COLOR(NVG_DST_COLOR),
		ONE_MINUS_DST_COLOR(NVG_ONE_MINUS_DST_COLOR),
		SRC_ALPHA(NVG_SRC_ALPHA),
		ONE_MINUS_SRC_ALPHA(NVG_ONE_MINUS_SRC_ALPHA),
		DST_ALPHA(NVG_DST_ALPHA),
		ONE_MINUS_DST_ALPHA(NVG_ONE_MINUS_DST_ALPHA),
		SRC_ALPHA_SATURATE(NVG_SRC_ALPHA_SATURATE);
		
		private final int _myID;

		private NVGblendFactor(int theID) {
			_myID = theID;
		}

		public int id() {
			return _myID;
		}
	}

	// Sets the composite operation with custom pixel arithmetic. The parameters should be one of NVGblendFactor.
	public void globalCompositeBlendFunc(NVGblendFactor sfactor, NVGblendFactor dfactor){
		nvgGlobalCompositeBlendFunc(_myContext, sfactor.id(), dfactor.id());
	}

	// Sets the composite operation with custom pixel arithmetic for RGB and alpha components separately. The parameters should be one of NVGblendFactor.
	public void globalCompositeBlendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha){
		nvgGlobalCompositeBlendFuncSeparate(_myContext, srcRGB, dstRGB, srcAlpha, dstAlpha);
	}

	//
	// State Handling
	//
	// NanoVG contains state which represents how paths will be rendered.
	// The state contains transform, fill and stroke styles, text and font styles,
	// and scissor clipping.

	// Pushes and saves the current render state into a state stack.
	// A matching nvgRestore() must be used to restore the state.
	public void save() {
		nvgSave(_myContext);
	}

	// Pops and restores current render state.
	public void restore(){
		nvgRestore(_myContext);
	}

	// Resets current render state to default values. Does not affect the render state stack.
	public void reset(){
		nvgReset(_myContext);
	}

	//
	// Render styles
	//
	// Fill and stroke render style can be either a solid color or a paint which is a gradient or a pattern.
	// Solid color is simply defined as a color value, different kinds of paints can be created
	// using nvgLinearGradient(), nvgBoxGradient(), nvgRadialGradient() and nvgImagePattern().
	//
	// Current render style can be saved and restored using nvgSave() and nvgRestore().

	// Sets whether to draw antialias for nvgStroke() and nvgFill(). It's enabled by default.
	public void shapeAntiAlias(boolean enabled){
		nvgShapeAntiAlias(_myContext, enabled);
	}
	
	// Sets current stroke style to a solid color.
	public void strokeColor(CCColor theColor){
		nvgStrokeColor(_myContext, convertColor(theColor));
	}

	// Sets current stroke style to a paint, which can be a one of the gradients or a pattern.
	public void strokePaint(NVGPaint thePaint) {
		nvgStrokePaint(_myContext, thePaint);
	}

	// Sets current fill style to a solid color.
	public void fillColor(CCColor theColor){
		nvgFillColor(_myContext, convertColor(theColor));
	}

	// Sets current fill style to a paint, which can be a one of the gradients or a pattern.
	public void fillPaint(NVGPaint thePaint) {
		nvgFillPaint(_myContext, thePaint);
	}

	// Sets the miter limit of the stroke style.
	// Miter limit controls when a sharp corner is beveled.
	public void miterLimit(float limit){
		nvgMiterLimit(_myContext, limit);
	}

	// Sets the stroke width of the stroke style.
	public void strokeWidth(double theStrokeWeight){
		nvgStrokeWidth(_myContext, (float)theStrokeWeight);
	}
	
	/**
	 * @author christianr
	 *
	 */
	public enum NVGlineCap {
		
		BUTT(NVG_BUTT),
		ROUND(NVG_ROUND),
		SQUARE(NVG_SQUARE);
		
		private final int _myID;

		private NVGlineCap(int theID) {
			_myID = theID;
		}

		public int id() {
			return _myID;
		}
		
	}

	// Sets how the end of the line (cap) is drawn,
	// Can be one of: NVG_BUTT (default), NVG_ROUND, NVG_SQUARE.
	public void lineCap(NVGlineCap cap){
		nvgLineCap(_myContext, cap.id());
	}
	
	public enum NVGlineJoin {
		
		ROUND(NVG_ROUND),
		BEVEL(NVG_BEVEL),
		MITER(NVG_MITER);
		
		private final int _myID;

		private NVGlineJoin(int theID) {
			_myID = theID;
		}

		public int id() {
			return _myID;
		}
		
	}

	// Sets how sharp path corners are drawn.
	// Can be one of NVG_MITER (default), NVG_ROUND, NVG_BEVEL.
	public void lineJoin(NVGlineJoin join){
		nvgLineJoin(_myContext, join.id());
	}

	// Sets the transparency applied to all rendered shapes.
	// Already transparent paths will get proportionally more transparent as well.
	public void globalAlpha(double alpha){
		nvgGlobalAlpha(_myContext, (float)alpha);
	}

	//
	// Transforms
	//
	// The paths, gradients, patterns and scissor region are transformed by an transformation
	// matrix at the time when they are passed to the API.
	// The current transformation matrix is a affine matrix:
	//   [sx kx tx]
	//   [ky sy ty]
	//   [ 0  0  1]
	// Where: sx,sy define scaling, kx,ky skewing, and tx,ty translation.
	// The last row is assumed to be 0,0,1 and is not stored.
	//
	// Apart from nvgResetTransform(), each transformation function first creates
	// specific transformation matrix and pre-multiplies the current transformation by it.
	//
	// Current coordinate system (transformation) can be saved and restored using nvgSave() and nvgRestore().

	// Resets current transform to a identity matrix.
	public void resetTransform(){
		nvgResetTransform(_myContext);
	}

	// Premultiplies current coordinate system by specified matrix.
	// The parameters are interpreted as matrix as follows:
	//   [a c e]
	//   [b d f]
	//   [0 0 1]
	public void transform(float a, float b, float c, float d, float e, float f){
		nvgTransform(_myContext, a, b, c, d, e, f);
	}

	// Translates current coordinate system.
	public void translate(double x, double y) {
		nvgTranslate(_myContext, (float)x, (float)y);
	}

	// Rotates current coordinate system. Angle is specified in radians.
	public void rotate(double angle){
		nvgRotate(_myContext, (float)angle);
	}

	// Skews the current coordinate system along X axis. Angle is specified in radians.
	public void akewX(double angle){
		nvgSkewX(_myContext, (float)angle);
	}

	// Skews the current coordinate system along Y axis. Angle is specified in radians.
	public void skewY(double angle){
		nvgSkewY(_myContext, (float)angle);
	}

	// Scales the current coordinate system.
	public void scale(double x, double y){
		nvgScale(_myContext, (float)x, (float)y);
	}

	// Stores the top part (a-f) of the current transformation matrix in to the specified buffer.
	//   [a c e]
	//   [b d f]
	//   [0 0 1]
	// There should be space for 6 floats in the return buffer for the values a-f.
	public CCMatrix32 currentTransform(){
		try(MemoryStack stack = MemoryStack.stackPush()){
			FloatBuffer xform = stack.mallocFloat(6);
			nvgCurrentTransform(_myContext, xform);
			
			return new CCMatrix32(xform);
		}
	}

	//
	// Images
	//
	// NanoVG allows you to load jpg, png, psd, tga, pic and gif files to be used for rendering.
	// In addition you can upload your own image. The image loading is provided by stb_image.
	// The parameter imageFlags is combination of flags defined in NVGimageFlags.

	// Creates image by loading it from the disk from specified file name.
	// Returns handle to the image.
	public int createImage(String filename, int imageFlags){
		return nvgCreateImage(_myContext, filename, imageFlags);
	}

	// Creates image by loading it from the specified chunk of memory.
	// Returns handle to the image.
	public int createImageMem(int imageFlags, ByteBuffer data){
		return nvgCreateImageMem(_myContext, imageFlags, data);
	}

	// Creates image from specified image data.
	// Returns handle to the image.
	public int createImageRGBA(int w, int h, int imageFlags, ByteBuffer data){
		return nvgCreateImageRGBA(_myContext, w, h, imageFlags, data);
	}

	// Updates image data specified by image handle.
	public void updateImage(int image, ByteBuffer data){
		nvgUpdateImage(_myContext, image, data);
	}

	// Returns the dimensions of a created image.
	public CCVector2i imageSize(int theImage) {
		try(MemoryStack stack = MemoryStack.stackPush()){
			IntBuffer myWidth = stack.mallocInt(1);
			IntBuffer myHeight = stack.mallocInt(1);
			nvgImageSize(_myContext, theImage, myWidth, myHeight);
			return new CCVector2i(myWidth.get(0), myHeight.get(0));
		}
	}

	// Deletes created image.
	public void deleteImage(int image){
		nvgDeleteImage(_myContext, image);
	}

	//
	// Paints
	//
	// NanoVG supports four types of paints: linear gradient, box gradient, radial gradient and image pattern.
	// These can be used as paints for strokes and fills.

	// Creates and returns a linear gradient. Parameters (sx,sy)-(ex,ey) specify the start and end coordinates
	// of the linear gradient, icol specifies the start color and ocol the end color.
	// The gradient is transformed by the current transform when it is passed to nvgFillPaint() or nvgStrokePaint().
	public NVGPaint linearGradient(double theStartX, double theStartY, double theEndX, double theEndY, CCColor theStartColor, CCColor theEndColor) {
		NVGPaint myResult = NVGPaint.create();
		nvgLinearGradient(_myContext, (float)theStartX, (float)theStartY, (float)theEndX, (float)theEndY, convertColor(theStartColor), convertColor(theEndColor), myResult);
		return myResult;
	}

	// Creates and returns a box gradient. Box gradient is a feathered rounded rectangle, it is useful for rendering
	// drop shadows or highlights for boxes. Parameters (x,y) define the top-left corner of the rectangle,
	// (w,h) define the size of the rectangle, r defines the corner radius, and f feather. Feather defines how blurry
	// the border of the rectangle is. Parameter icol specifies the inner color and ocol the outer color of the gradient.
	// The gradient is transformed by the current transform when it is passed to nvgFillPaint() or nvgStrokePaint().
	public NVGPaint boxGradient(double x, double y, double x2, double y2, double i, double j, CCColor icol, CCColor ocol) {
		NVGPaint myResult = NVGPaint.create();
		nvgBoxGradient(_myContext, (float)x, (float)y, (float)x2, (float)y2, (float)i, (float)j, convertColor(icol), convertColor(ocol), myResult);
		return myResult;
	}

	// Creates and returns a radial gradient. Parameters (cx,cy) specify the center, inr and outr specify
	// the inner and outer radius of the gradient, icol specifies the start color and ocol the end color.
	// The gradient is transformed by the current transform when it is passed to nvgFillPaint() or nvgStrokePaint().
	public NVGPaint radialGradient(double cx, double cy, double inr, double outr,CCColor icol, CCColor ocol){
		NVGPaint myResult = NVGPaint.create();
		nvgRadialGradient(_myContext, (float)cx, (float)cy, (float)inr, (float)outr, convertColor(icol), convertColor(ocol), myResult);
		return myResult;
	}

	// Creates and returns an image patter. Parameters (ox,oy) specify the left-top location of the image pattern,
	// (ex,ey) the size of one image, angle rotation around the top-left corner, image is handle to the image to render.
	// The gradient is transformed by the current transform when it is passed to nvgFillPaint() or nvgStrokePaint().
	public NVGPaint imagePattern(double theOriginX, double theOriginY, double theEndX, double theEndY, double theAngle, int theImage, double theAlpha) {
		NVGPaint myResult = NVGPaint.create();
		nvgImagePattern(_myContext, (float)theOriginX, (float)theOriginY, (float)theEndX, (float)theEndY, (float)theAngle, theImage, (float)theAlpha, myResult);
		return myResult;
	}

	//
	// Scissoring
	//
	// Scissoring allows you to clip the rendering into a rectangle. This is useful for various
	// user interface cases like rendering a text edit or a timeline.

	// Sets the current scissor rectangle.
	// The scissor rectangle is transformed by the current transform.
	public void scissor(float x, float y, float w, float h){
		nvgScissor(_myContext, x, y, w, h);
	}

	// Intersects current scissor rectangle with the specified rectangle.
	// The scissor rectangle is transformed by the current transform.
	// Note: in case the rotation of previous scissor rect differs from
	// the current one, the intersection will be done between the specified
	// rectangle and the previous scissor rectangle transformed in the current
	// transform space. The resulting shape is always rectangle.
	public void intersectScissor(double x, double y, double w, double h) {
		nvgIntersectScissor(_myContext, (float)x, (float)y, (float)w, (float)h);
	}

	// Reset and disables scissoring.
	public void resetScissor(){
		nvgResetScissor(_myContext);
	}

	//
	// Paths
	//
	// Drawing a new shape starts with nvgBeginPath(), it clears all the currently defined paths.
	// Then you define one or more paths and sub-paths which describe the shape. The are functions
	// to draw common shapes like rectangles and circles, and lower level step-by-step functions,
	// which allow to define a path curve by curve.
	//
	// NanoVG uses even-odd fill rule to draw the shapes. Solid shapes should have counter clockwise
	// winding and holes should have counter clockwise order. To specify winding of a path you can
	// call nvgPathWinding(). This is useful especially for the common shapes, which are drawn CCW.
	//
	// Finally you can fill the path using current fill style by calling nvgFill(), and stroke it
	// with current stroke style by calling nvgStroke().
	//
	// The curve segments and sub-paths are transformed by the current transform.

	// Clears the current path and sub-paths.
	public void beginPath(){
		nvgBeginPath(_myContext);
	}

	// Starts new sub-path with specified point as first point.
	public void moveTo(double theX, double theY) {
		nvgMoveTo(_myContext, (float)theX, (float)theY);
	}

	// Adds line segment from the last point in the path to the specified point.
	public void lineTo(double theX, double theY) {
		nvgLineTo(_myContext, (float)theX, (float)theY);
	}
	
	// Adds cubic bezier segment from last point in the path via two control points to the specified point.
	public void bezierTo(float c1x, float c1y, float c2x, float c2y, float x, float y){
		nvgBezierTo(_myContext, c1x, c1y, c2x, c2y, x, y);
	}

	// Adds quadratic bezier segment from last point in the path via a control point to the specified point.
	public void quadTo(float cx, float cy, float x, float y){
		nvgQuadTo(_myContext, cx, cy, x, y);
	}

	// Adds an arc segment at the corner defined by the last path point, and two specified points.
	public void arcTo(float x1, float y1, float x2, float y2, float radius){
		nvgArcTo(_myContext, x1, y1, x2, y2, radius);
	}

	// Closes current sub-path with a line segment.
	public void closePath(){
		nvgClosePath(_myContext);
	}
	
	public enum NVGwinding {
		/**
		 * Winding for solid shapes
		 */
		CCW(NVG_CCW),
		/**
		 * Winding for holes
		 */
		CW(NVG_CW);
		
		private final int _myID;

		private NVGwinding(int theID) {
			_myID = theID;
		}

		public int id() {
			return _myID;
		}
	}

	// Sets the current sub-path winding, see NVGwinding and NVGsolidity.
	public void pathWinding(NVGwinding dir){
		nvgPathWinding(_myContext, dir.id());
	}

	// Creates new circle arc shaped sub-path. The arc center is at cx,cy, the arc radius is r,
	// and the arc is drawn from angle a0 to a1, and swept in direction dir (NVG_CCW, or NVG_CW).
	// Angles are specified in radians.
	public void arc(double cx, double cy, double r, double a0, double a1, NVGwinding dir){
		nvgArc(_myContext, (float)cx, (float)cy, (float)r, (float)a0, (float)a1, dir.id());
	}

	// Creates new rectangle shaped sub-path.
	public void rect(double theX, double theY, double theWidth, double theHeight) {
		nvgRect(_myContext, (float)theX, (float)theY, (float)theWidth, (float)theHeight);
	}

	// Creates new rounded rectangle shaped sub-path.
	public void roundedRect(double theX, double theY, double theWidth, double theHeight, double theRadius) {
		nvgRoundedRect(_myContext, (float)theX, (float)theY, (float)theWidth, (float)theHeight, (float)theRadius);
	}

	// Creates new rounded rectangle shaped sub-path with varying radii for each corner.
	public void roundedRectVarying(double x, double y, double w, double h, double radTopLeft, double radTopRight, double radBottomRight, double radBottomLeft){
		nvgRoundedRectVarying(_myContext, (float)x, (float)y, (float)w, (float)h, (float)radTopLeft, (float)radTopRight, (float)radBottomRight, (float)radBottomLeft);
	}

	// Creates new ellipse shaped sub-path.
	public void ellipse(double cx, double cy, double rx, double ry){
		nvgEllipse(_myContext, (float)cx, (float)cy, (float)rx, (float)ry);
	}

	// Creates new circle shaped sub-path.
	public void circle(double cx, double cy, double r){
		nvgCircle(_myContext, (float)cx, (float)cy, (float)r);
	}

	// Fills the current path with current fill style.
	public void fill() {
		nvgFill(_myContext);
	}

	// Fills the current path with current stroke style.
	public void stroke() {
		nvgStroke(_myContext);
	}


	//
	// Text
	//
	// NanoVG allows you to load .ttf files and use the font to render text.
	//
	// The appearance of the text can be defined by setting the current text style
	// and by specifying the fill color. Common text and font settings such as
	// font size, letter spacing and text align are supported. Font blur allows you
	// to create simple text effects such as drop shadows.
	//
	// At render time the font face can be set based on the font handles or name.
	//
	// Font measure functions return values in local space, the calculations are
	// carried in the same resolution as the final rendering. This is done because
	// the text glyph positions are snapped to the nearest pixels sharp rendering.
	//
	// The local space means that values are not rotated or scale as per the current
	// transformation. For example if you set font size to 12, which would mean that
	// line height is 16, then regardless of the current scaling and rotation, the
	// returned line height is always 16. Some measures may vary because of the scaling
	// since aforementioned pixel snapping.
	//
	// While this may sound a little odd, the setup allows you to always render the
	// same way regardless of scaling. I.e. following works regardless of scaling:
	//
//			const char* txt = "Text me up.";
//			nvgTextBounds(vg, x,y, txt, NULL, bounds);
//			nvgBeginPath(vg);
//			nvgRoundedRect(vg, bounds[0],bounds[1], bounds[2]-bounds[0], bounds[3]-bounds[1]);
//			nvgFill(vg);
	//
	// Note: currently only solid color fill is supported for text.

	// Creates font by loading it from the disk from specified file name.
	// Returns handle to the font.
	public int createFont(String name, String filename){
		return nvgCreateFont(_myContext, name, filename);
	}

	// Creates font by loading it from the specified memory chunk.
	// Returns handle to the font.
	public int createFontMem(String name, ByteBuffer data, int freeData){
		return nvgCreateFontMem(_myContext, name, data, freeData);
	}

	// Finds a loaded font of specified name, and returns handle to it, or -1 if the font is not found.
	public int findFont(String name){
		return nvgFindFont(_myContext, name);
	}

	// Adds a fallback font by handle.
	public int addFallbackFontId(int baseFont, int fallbackFont){
		return nvgAddFallbackFontId(_myContext, baseFont, fallbackFont);
	}

	// Adds a fallback font by name.
	public int addFallbackFont(String baseFont, String fallbackFont){
		return nvgAddFallbackFont(_myContext, baseFont, fallbackFont);
	}

	// Sets the font size of current text style.
	public void fontSize(double theFontSize){
		nvgFontSize(_myContext, (float)theFontSize);
	}

	// Sets the blur of current text style.
	public void fontBlur(float blur){
		nvgFontBlur(_myContext, blur);
	}

	// Sets the letter spacing of current text style.
	public void textLetterSpacing(float spacing){
		nvgTextLetterSpacing(_myContext, spacing);
	}

	// Sets the proportional line height of current text style. The line height is specified as multiple of font size.
	public void textLineHeight(float lineHeight){
		nvgTextLineHeight(_myContext, lineHeight);
	}

	// Sets the text align of current text style, see NVGalign for options.
	public void textAlign(int theAlign) {
		nvgTextAlign(_myContext, theAlign);
	}

	// Sets the font face based on specified id of current text style.
	public void fontFaceId(int font){
		nvgFontFaceId(_myContext, font);
	}

	// Sets the font face based on specified name of current text style.
	public void fontFace(String theFont){
		nvgFontFace(_myContext, theFont);
	}

	// Draws text string at specified location. If end is specified only the sub-string up to the end is drawn.
	public void text(double x, double y, String mCaption) {
		nvgText(_myContext,  (float)x, (float)y, mCaption);
	}

	// Draws multi-line text string at specified location wrapped at the specified width. If end is specified only the sub-string up to the end is drawn.
	// White space is stripped at the beginning of the rows, the text is split at word boundaries or when new-line characters are encountered.
	// Words longer than the max width are slit at nearest character (i.e. no hyphenation).
	public void textBox(double x, double y, double x2, String mCaption) {
		nvgTextBox(_myContext,  (float)x, (float)y, (float)x2, mCaption);
	}

	// Measures the specified text string. Parameter bounds should be a pointer to float[4],
	// if the bounding box of the text should be returned. The bounds value are [xmin,ymin, xmax,ymax]
	// Returns the horizontal advance of the measured text (i.e. where the next character should drawn).
	// Measured values are returned in local coordinate space.
	public float textBounds(double x, double y, String mCaption) {
		return nvgTextBounds(_myContext, (float)x, (float)y, mCaption, (FloatBuffer)null);
	}
	
	public float textBounds(double x, double y, String mCaption, float[] theBounds) {
		return nvgTextBounds(_myContext, (float)x, (float)y, mCaption, theBounds);
	}

	// Measures the specified multi-text string. Parameter bounds should be a pointer to float[4],
	// if the bounding box of the text should be returned. The bounds value are [xmin,ymin, xmax,ymax]
	// Measured values are returned in local coordinate space.
	public float[] textBoxBounds(double x, double y, double x2, String mCaption) {
		float[] myResult = new float[4];
		nvgTextBoxBounds(_myContext, (float)x, (float)y, (float)x2, mCaption, myResult);
		return myResult;
	}

	// Calculates the glyph x positions of the specified text. If end is specified only the sub-string will be used.
	// Measured values are returned in local coordinate space.
	public NVGGlyphPosition.Buffer textGlyphPositions(float x, float y, String string, int maxPositions){
		NVGGlyphPosition.Buffer myPositions = NVGGlyphPosition.malloc(maxPositions);
		nvgTextGlyphPositions(_myContext, x, y, string, myPositions);
		return myPositions;
	}
	
	public int textGlyphPositions(float x, float y, String string, NVGGlyphPosition.Buffer glyphs){
		return nvgTextGlyphPositions(_myContext, x, y, string, glyphs);
	}

//	// Returns the vertical metrics based on the current text style.
//	// Measured values are returned in local coordinate space.
//	public void nvgTextMetrics(float* ascender, float* descender, float* lineh){
//		try{
//		nvgTextMetrics(ctx, ascender, descender, lineh);
//	}
//
//	// Breaks the specified text into lines. If end is specified only the sub-string will be used.
//	// White space is stripped at the beginning of the rows, the text is split at word boundaries or when new-line characters are encountered.
//	// Words longer than the max width are slit at nearest character (i.e. no hyphenation).
//	int nvgTextBreakLines(const char* string, const char* end, float breakRowWidth, NVGtextRow* rows, int maxRows);

}

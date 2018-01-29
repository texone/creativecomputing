package cc.creativecomputing.uinano;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cc.creativecomputing.core.util.CCBitUtil;
import cc.creativecomputing.gl.app.CCGLAction;
import cc.creativecomputing.gl.app.CCGLCursorShape;
import cc.creativecomputing.gl.app.CCGLKeyEvent;
import cc.creativecomputing.gl.app.CCGLMouseButton;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.uinano.layout.GroupLayout;
import cc.creativecomputing.uinano.layout.Layout;


/**
 * Base class of all widgets.
 * <p>
 * Widget is the base class of all widgets. It can also be
 * used as an panel to arrange an arbitrary number of child widgets using a
 * layout generator {@linkplain Layout}.
 */

public class CCWidget{
	
	public static  boolean nvgIsFontIcon(int value) { return value >= 1024; }
	
	public static String utf8(int theInt){try {
		return new String(CCBitUtil.split(theInt), "UTF-8");
	} catch (UnsupportedEncodingException e) {
		return new String(CCBitUtil.split(theInt));
	}};
	
	protected CCWidget _myParent;
	protected Theme _myTheme;
	protected Layout _myLayout;
	protected String _myId;
	protected CCVector2 _myPosition = new CCVector2();
	protected CCVector2 _mySize = new CCVector2();
	protected CCVector2 _myFixedSize = new CCVector2();
	protected ArrayList<CCWidget> _myChildren = new ArrayList<>();

	/**
	 * Whether or not this Widget is currently visible. When a Widget is not
	 * currently visible, no time is wasted executing its drawing method.
	 */
	protected boolean _myIsVisible;

	/**
	 * Whether or not this Widget is currently enabled. Various different kinds
	 * of derived types use this to determine whether or not user input will be
	 * accepted. For example, when ``mEnabled == false``, the state of a
	 * CheckBox cannot be changed, or a TextBox will not allow new input.
	 */
	protected boolean _myIsEnabled;
	protected boolean _myIsFocused;
	protected boolean _myMouseFocus;
	protected String _myTooltip;
	protected int _myFontSize;

	/**
	 * The amount of extra icon scaling used in addition the the theme's default
	 * icon font scale. Default value is 1.0, which implies that
	 * {@linkplain CCWidget#icon_scale()} simply returns the value of
	 * {@linkplain Theme#mIconScale}.
	 * <p>
	 * Most widgets do not need extra scaling, but some (e.g., CheckBox,
	 * TextBox) need to adjust the Theme's default icon scaling to properly
	 * display icons within their bounds (upscale, or downscale).
	 * <p>
	 * When using ``nvgFontSize`` for icons in subclasses, make sure to call the
	 * {@linkplain CCWidget#icon_scale()} function. Expected usage when drawing
	 * icon fonts is something like:
	 * 
	 * <pre>
	 * virtual void draw(NVGcontext *ctx) { 
	 * 	// fontSize depends on the kind of Widget. Search for `FontSize` 
	 * 	// in the Theme class (e.g., standard vs button) 
	 * 	float ih = fontSize; 
	 * 		
	 * 	// assuming your Widget has a declared `mIcon` 
	 * 	if (nvgIsFontIcon(mIcon)) { 
	 * 		ih *= icon_scale(); 
	 * 		nvgFontFace(ctx,"icons"); 
	 * 		nvgFontSize(ctx, ih); 
	 * 		/// remaining drawing code (see button.cpp for more) 
	 * 	} 
	 * }
	 * </pre>
	 */
	protected float _myIconExtraScale;
	protected CCGLCursorShape _myCursor;

	public CCWidget(CCWidget theParent) {
		_myParent = null;
		_myTheme = null;
		_myLayout = new GroupLayout();
		_myPosition = new CCVector2();
		_mySize = new CCVector2();
		_myFixedSize = new CCVector2();
		_myIsVisible = true;
		_myIsEnabled = true;
		_myIsFocused = false;
		_myMouseFocus = false;
		_myTooltip = "";
		_myFontSize = -1;
		_myIconExtraScale = 1.0f;
		_myCursor = CCGLCursorShape.ARROW;
		
		if (theParent != null)
			theParent.addChild(this);
	}
	
	public CCWidget(){
		this(null);
	}

	/**
	 * Return the parent widget
	 * @return the parent widget
	 */
	public final CCWidget parent() {
		return _myParent;
	}

	/**
	 * Set the parent widget
	 * @param theParent the parent widget
	 */
	public final void setParent(CCWidget theParent) {
		_myParent = theParent;
	}
	
	/**
	 * Return the used {@linkplain Layout} generator
	 * @return
	 */
	public final Layout layout() {
		return _myLayout;
	}

	/**
	 * Set the used {@linkplain Layout} generator
	 * @param layout
	 */
	public final void setLayout(Layout layout) {
		_myLayout = layout;
	}

	/**
	 * Return the {@linkplain Theme} used to draw this widget
	 * @return
	 */
	public final Theme theme() {
		return _myTheme;
	}

	/**
	 * Set the {@linkplain Theme} used to draw this widget
	 * @param theme
	 */
	public void setTheme(Theme theme) {
		if (_myTheme == theme) {
			return;
		}
		_myTheme = theme;
		for (CCWidget child : _myChildren) {
			child.setTheme(theme);
		}
	}

	/**
	 * Return the position relative to the parent widget
	 * @return position relative to the parent widget
	 */
	public final CCVector2 position() {
		return _myPosition;
	}

	/**
	 * Set the position relative to the parent widget
	 * @param thePosition position relative to the parent widget
	 * @return
	 */
	public final CCWidget position(CCVector2 thePosition) {
		_myPosition.set(thePosition);
		return this;
	}
	
	public final CCWidget position(double theX, double theY) {
		_myPosition.set(theX, theY);
		return this;
	}
	
	/**
	 * Return the absolute position on screen
	 * @return absolute position on screen
	 */
	public final CCVector2 absolutePosition() {
		return _myParent != null ? (parent().absolutePosition().add(_myPosition)) : _myPosition;
	}

	/**
	 * Return the size of the widget
	 * @return size of the widget
	 */
	public final CCVector2 size() {
		return _mySize;
	}
	
	/**
	 * set the size of the widget
	 * @param theSize
	 * @return
	 */
	public CCWidget size(CCVector2 theSize) {
		_mySize.set(theSize);
		return this;
	}
	
	/**
	 * set the size of the widget
	 * @param theX
	 * @param theY
	 * @return
	 */
	public CCWidget size(double theX, double theY) {
		_mySize.set(theX, theY);
		return this;
	}

	/**
	 * Return the width of the widget
	 * @return width of the widget
	 */
	public final double width() {
		return _mySize.x;
	}

	/**
	 * Set the width of the widget
	 * @param theWidth width of the widget
	 */
	public final void setWidth(double theWidth) {
		_mySize.x = theWidth;
	}

	/**
	 * Return the height of the widget
	 * @return height of the widget
	 */
	public final double height() {
		return _mySize.y;
	}

	/**
	 * Set the height of the widget
	 * @param theHeight height of the widget
	 */
	public final void setHeight(int theHeight) {
		_mySize.y = theHeight;
	}

	/**
	 * Set the fixed size of this widget
	 * <p>
	 * If nonzero, components of the fixed size attribute override any values
	 * computed by a layout generator associated with this widget. Note that
	 * just setting the fixed size alone is not enough to actually change its
	 * size; this is done with a call to {@linkplain #size(CCVector2)} or a call
	 * to {@linkplain #performLayout(CCGraphics)} in the parent widget.
	 * 
	 * @param fixedSize fixed size of this widget
	 */
	public final void setFixedSize(CCVector2 fixedSize) {
		_myFixedSize.set(fixedSize);
	}

	/**
	 * Return the fixed size
	 * @return the fixed size
	 * @see #setFixedSize(CCVector2)
	 */
	public final CCVector2 fixedSize() {
		return _myFixedSize;
	}

	// Return the fixed width (see \ref setFixedSize())
	public final double fixedWidth() {
		return _myFixedSize.x;
	}

	// Return the fixed height (see \ref setFixedSize())
	public final double fixedHeight() {
		return _myFixedSize.y;
	}

	/// Set the fixed width (see \ref setFixedSize())
	public final void setFixedWidth(int width) {
		_myFixedSize.x = width;
	}

	/// Set the fixed height (see \ref setFixedSize())
	public final void setFixedHeight(int height) {
		_myFixedSize.y = height;
	}

	/// Return whether or not the widget is currently visible (assuming all
	/// parents are visible)
	public final boolean visible() {
		return _myIsVisible;
	}

	/// Set whether or not the widget is currently visible (assuming all parents
	/// are visible)
	public void setVisible(boolean visible) {
		_myIsVisible = visible;
	}

	/// Check if this widget is currently visible, taking parent widgets into
	/// account
	public final boolean visibleRecursive() {
		boolean visible = true;
		CCWidget widget = this;
		while (widget != null) {
			visible &= widget.visible();
			widget = widget.parent();
		}
		return visible;
	}

	/// Return the number of child widgets
	public final int childCount() {
		return _myChildren.size();
	}

	/// Return the list of child widgets of the current widget
	public final ArrayList<CCWidget> children() {
		return _myChildren;
	}

	/**
	 * \brief Add a child widget to the current widget at the specified index.
	 *
	 * This function almost never needs to be called by hand, since the
	 * constructor of \ref Widget automatically adds the current widget to its
	 * parent
	 */
	public void addChild(int index, CCWidget widget) {
		assert index <= childCount();
		_myChildren.add(index, widget);
		widget.setParent(this);
		widget.setTheme(_myTheme);
	}

	/// Convenience function which appends a widget at the end
	public void addChild(CCWidget widget) {
		addChild(childCount(), widget);
	}

	/// Remove a child widget by value
	public void removeChild(CCWidget widget) {
		_myChildren.remove(widget);

	}

	/// Remove a child widget by index
	public void removeChild(int index) {
		_myChildren.remove(index);
	}

	/// Retrieves the child at the specific position
	public final CCWidget childAt(int index) {
		return _myChildren.get(index);
	}

	/// Returns the index of a specific child or -1 if not found
	public int childIndex(CCWidget widget) {
		return _myChildren.indexOf(widget);
	}

	/// Variadic shorthand notation to construct and add a child widget
	// C++ TO JAVA CONVERTER TODO TASK: There is no equivalent in Java to C++11
	// variadic templates:
	// public final <WidgetClass, typename... Args> WidgetClass add(Args ...
	// args)
	// {
	// return new WidgetClass(this, args...);
	// }

	/// Walk up the hierarchy and return the parent window
	public Window window() {
		CCWidget widget = this;
		while (true) {
			if (widget == null) {
				throw new RuntimeException("Widget:internal error (could not find parent window)");
			}
			Window window = (Window) ((widget instanceof Window) ? widget : null);
			if (window != null) {
				return window;
			}
			widget = widget.parent();
		}
	}

	/// Walk up the hierarchy and return the parent screen
	public Screen screen() {
		CCWidget widget = this;
		while (true) {
			if (widget == null) {
				throw new RuntimeException("Widget:internal error (could not find parent screen)");
			}
			Screen screen = (Screen) ((widget instanceof Screen) ? widget : null);
			if (screen != null) {
				return screen;
			}
			widget = widget.parent();
		}
	}

	/// Associate this widget with an ID value (optional)
	public final void setId(String id) {
		_myId = id;
	}

	/// Return the ID value associated with this widget, if any
	public final String id() {
		return _myId;
	}

	/// Return whether or not this widget is currently enabled
	public final boolean enabled() {
		return _myIsEnabled;
	}

	/// Set whether or not this widget is currently enabled
	public final void setEnabled(boolean enabled) {
		_myIsEnabled = enabled;
	}

	/// Return whether or not this widget is currently focused
	public final boolean focused() {
		return _myIsFocused;
	}

	/// Set whether or not this widget is currently focused
	public final void setFocused(boolean focused) {
		_myIsFocused = focused;
	}

	/// Request the focus to be moved to this widget
	public void requestFocus() {
		CCWidget widget = this;
		while (widget.parent() != null) {
			widget = widget.parent();
		}
		((Screen) widget).updateFocus(this);
	}

	public final String tooltip() {
		return _myTooltip;
	}

	public final void setTooltip(String tooltip) {
		_myTooltip = tooltip;
	}

	/// Return current font size. If not set the default of the current theme
	/// will be returned
	public int fontSize() {
		return (_myFontSize < 0 && _myTheme != null) ? _myTheme.mStandardFontSize : _myFontSize;
	}

	/// Set the font size of this widget
	public final void setFontSize(int fontSize) {
		_myFontSize = fontSize;
	}

	/// Return whether the font size is explicitly specified for this widget
	public final boolean hasFontSize() {
		return _myFontSize > 0;
	}

	/**
	 * The amount of extra scaling applied to *icon* fonts. See \ref
	 * nanogui::Widget::mIconExtraScale.
	 */
	public final float iconExtraScale() {
		return _myIconExtraScale;
	}

	/**
	 * Sets the amount of extra scaling applied to *icon* fonts. See \ref
	 * nanogui::Widget::mIconExtraScale.
	 */
	public final void setIconExtraScale(float scale) {
		_myIconExtraScale = scale;
	}

	/// Return a pointer to the cursor of the widget
	public final CCGLCursorShape cursor() {
		return _myCursor;
	}

	/// Set the cursor of the widget
	public final void cursor(CCGLCursorShape cursor) {
		_myCursor = cursor;
	}

	/// Check if the widget contains a certain position
	public final boolean contains(CCVector2 p) {
		return p.x >= _myPosition.x && p.y >= _myPosition.y && p.x < _myPosition.x + _mySize.x && p.y < _myPosition.y + _mySize.y;
	}

	/**
	 * Determine the widget located at the given position value (recursive)
	 * @param thePosition
	 * @return
	 */
	public CCWidget findWidget(CCVector2 thePosition) {
		for (CCWidget myChild : _myChildren) {
			CCVector2 myChildPosition = thePosition.subtract(_myPosition);
			if (myChild.visible() && myChild.contains(myChildPosition)) {
				return myChild.findWidget(myChildPosition);
			}
		}
		return contains(thePosition) ? this : null;
	}

	
	
	/**
	 * Handle a mouse button event (default implementation: propagate to children)
	 * @param theMousePosition
	 * @param theEvent
	 * @return
	 */
	public boolean mouseButtonEvent(CCVector2 theMousePosition, CCGLMouseEvent theEvent) {
		for (CCWidget child : _myChildren) {
			CCVector2 myChildVector = theMousePosition.subtract(_myPosition);
			if (child.visible() && child.contains(myChildVector) && child.mouseButtonEvent(myChildVector, theEvent)) {
				return true;
			}
		}
		if (theEvent.button == CCGLMouseButton.BUTTON_1 && theEvent.action == CCGLAction.PRESS && !_myIsFocused) {
			requestFocus();
		}
		return false;
	}

	/// Handle a mouse motion event (default implementation: propagate to
	/// children)
	/**
	 * 
	 * @param p
	 * @param rel
	 * @param theEvent
	 * @return
	 */
	public boolean mouseMotionEvent(CCVector2 p, CCVector2 rel, CCGLMouseEvent theEvent) {
		for (CCWidget myChild : _myChildren) {
			if (!myChild.visible()) {
				continue;
			}
			CCVector2 myChildVector = p.subtract(_myPosition);
			boolean contained = myChild.contains(myChildVector);
			CCVector2 myPrevChildVector = myChildVector.subtract(rel);
			boolean prevContained = myChild.contains(myPrevChildVector);
			if (contained != prevContained) {
				myChild.mouseEnterEvent(p, contained);
			}
			if ((contained || prevContained) && myChild.mouseMotionEvent(myChildVector, rel, theEvent)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Handle a mouse drag event (default implementation: do nothing)
	 * @param theDragPosition
	 * @param theDragMotion
	 * @param theEvent
	 * @return
	 */
	public boolean mouseDragEvent(CCVector2 theDragPosition, CCVector2 theDragMotion, CCGLMouseEvent theEvent) {
		return false;
	}

	/// Handle a mouse enter/leave event (default implementation: record this
	/// fact, but do nothing)
	public boolean mouseEnterEvent(CCVector2 UnnamedParameter1, boolean enter) {
		_myMouseFocus = enter;
		return false;
	}

	/// Handle a mouse scroll event (default implementation: propagate to
	/// children)
	public boolean scrollEvent(CCVector2 p, CCVector2 rel) {
		// C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit
		// typing in Java:
		for (CCWidget child : _myChildren) {
			if (!child.visible()) {
				continue;
			}
			CCVector2 myChildVector = p.subtract(_myPosition);
			if (child.contains(myChildVector) && child.scrollEvent(myChildVector, rel)) {
				return true;
			}
		}
		return false;
	}

	/// Handle a focus change event (default implementation: record the focus
	/// status, but do nothing)
	public boolean focusEvent(boolean focused) {
		_myIsFocused = focused;
		return false;
	}

	/// Handle a keyboard event (default implementation: do nothing)
	public boolean keyboardEvent(CCGLKeyEvent theEvent) {
		return false;
	}

	/// Handle text input (UTF-32 format) (default implementation: do nothing)
	public boolean keyboardCharacterEvent(int UnnamedParameter1) {
		return false;
	}

	/// Compute the preferred size of the widget
	public CCVector2 preferredSize(CCGraphics g) {
		if (_myLayout != null) {
			return _myLayout.preferredSize(g, this);
		} else {
			return _mySize;
		}
	}

	/// Invoke the associated layout generator to properly place child widgets,
	/// if any
	public void performLayout(CCGraphics g) {
		if (_myLayout != null) {
			_myLayout.performLayout(g, this);
		} else {
			for (CCWidget c : _myChildren) {
				CCVector2 pref = c.preferredSize(g);
				CCVector2 fix = c.fixedSize();
				c.size(new CCVector2(fix.x > 0 ? fix.x : pref.x, fix.y > 0 ? fix.y : pref.y));
				c.performLayout(g);
			}
		}
	}

	/// Draw the widget (and all child widgets)
	public void draw(CCGraphics g) {
		if(CCUI.SHOW_WIDGET_BOUNDS){
			g.strokeWeight(1.0d);
			g.beginShape(CCDrawMode.LINE_LOOP);
			g.color(new CCColor(1f, 0f, 0f, 1f));
			g.vertex(_myPosition.x - 0.5f, _myPosition.y - 0.5f);
			g.vertex(_myPosition.x - 0.5f + _mySize.x + 1, _myPosition.y - 0.5f);
			g.vertex(_myPosition.x - 0.5f + _mySize.x + 1, _myPosition.y - 0.5f + _mySize.y + 1);
			g.vertex(_myPosition.x - 0.5f, _myPosition.y - 0.5f + _mySize.y + 1);
			g.endShape();
		}

		if (_myChildren.isEmpty()) {
			return;
		}

		g.pushMatrix();
		g.translate(_myPosition.x, _myPosition.y);
		
		for (CCWidget child : _myChildren) {
			if (child.visible()) {
				g.pushMatrix();
				g.scissor(child._myPosition.x, child._myPosition.y, child._mySize.x, child._mySize.y);
				child.draw(g);
				g.popMatrix();
			}
		}
		g.popMatrix();
	}

	/// Save the state of the widget into the given \ref Serializer instance
	public void save(CCDataElement s) {
		s.add("position", _myPosition);
		s.add("size", _mySize);
		s.add("fixedSize", _myFixedSize);
		
		s.addAttribute("visible", _myIsVisible);
		s.addAttribute("enabled", _myIsEnabled);
		s.addAttribute("focused", _myIsFocused);
		s.addAttribute("tooltip", _myTooltip);
		s.addAttribute("fontSize", _myFontSize);
		s.addAttribute("cursor", _myCursor.hashCode());
	}

	/// Restore the state of the widget from the given \ref Serializer instance
	public boolean load(CCDataElement s) {
		try{
			_myPosition.set(s.vector2("position"));
			_mySize.set(s.vector2("size"));
			_myFixedSize.set(s.vector2("fixedSize"));
			
			_myIsVisible = s.booleanAttribute("visible");
			_myIsEnabled = s.booleanAttribute("enabled");
			_myIsFocused = s.booleanAttribute("focused");
			_myTooltip = s.attribute("tooltip");
			_myFontSize = s.intAttribute("fontSize");
//			mVisible = s.intAttribute("cursor");
		}catch(Exception e){
			return false;
		}
		return true;

	}

	/// Free all resources used by the widget and any children
	public void close() {
//		for (auto child : mChildren) {
//			if (child != null) {
//				child.decRef();
//			}
//		}
	}

	/**
	 * Convenience definition for subclasses to get the full icon scale for this
	 * class of Widget. It simple returns the value ``mTheme->mIconScale *
	 * this->mIconExtraScale``.
	 *
	 * \remark See also: \ref nanogui::Theme::mIconScale and \ref
	 * nanogui::Widget::mIconExtraScale. This tiered scaling strategy may not be
	 * appropriate with fonts other than ``entypo.ttf``.
	 */
	protected final float icon_scale() {
		return _myTheme.mIconScale * _myIconExtraScale;
	}

}

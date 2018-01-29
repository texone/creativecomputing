package cc.creativecomputing.uinano;


import org.lwjgl.nanovg.NVGGlyphPosition;
import org.lwjgl.nanovg.NVGPaint;

import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.events.CCStringEvent;
import cc.creativecomputing.core.events.CCTypeEvent;
import cc.creativecomputing.gl.app.CCGLAction;
import cc.creativecomputing.gl.app.CCGLCursorShape;
import cc.creativecomputing.gl.app.CCGLKey;
import cc.creativecomputing.gl.app.CCGLKeyEvent;
import cc.creativecomputing.gl.app.CCGLMouseButton;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.gl.nanovg.NanoVG;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector2i;

/**
 * \class TextBox textbox.h nanogui/textbox.h
 *
 * \brief Fancy text box with builtin regular expression-based validation.
 *
 * \remark This class overrides \ref nanogui::Widget::mIconExtraScale to be
 * ``0.8f``, which affects all subclasses of this Widget. Subclasses must
 * explicitly set a different value if needed (e.g., in their constructor).
 */
public abstract class ValueBox<Type> extends CCWidget {
	/// How to align the text in the text box.
	public enum Alignment {
		Left, Center, Right;
	}

	protected boolean mEditable;
	protected boolean mSpinnable;
	protected boolean mCommitted;
	protected Type _myText;
	protected Type mDefaultValue;
	protected Alignment mAlignment;
	protected String mUnits;
	protected String mFormat;
	protected int mUnitsImage;

	protected final CCListenerManager<CCTypeEvent> mCallback = CCListenerManager.create(CCTypeEvent.class);
	protected boolean mValidFormat;
	protected StringBuilder _myTextTmp;
	protected String mPlaceholder;
	protected int mCursorPos;
	protected int mSelectionPos;
	protected CCVector2i mMousePos;
	protected CCVector2i mMouseDownPos;
	protected CCVector2i mMouseDragPos;
	protected CCGLMouseEvent mMouseDownModifier;
	protected float mTextOffset;
	protected double mLastClick;

	public ValueBox(CCWidget parent, Type value, Type theDefault) {
		super(parent);
		mEditable = false;
		mSpinnable = false;
		mCommitted = true;
		_myText = value;
		mDefaultValue = theDefault;
		mAlignment = Alignment.Center;
		mUnits = "";
		mFormat = "";
		mUnitsImage = -1;
		mValidFormat = true;
		_myTextTmp = new StringBuilder(value.toString());
		mCursorPos = -1;
		mSelectionPos = -1;
		mMousePos = new CCVector2i(-1, -1);
		mMouseDownPos = new CCVector2i(-1, -1);
		mMouseDragPos = new CCVector2i(-1, -1);
		mTextOffset = 0;
		mLastClick = 0;

		if (_myTheme != null)
			_myFontSize = _myTheme.mTextBoxFontSize;
		_myIconExtraScale = 0.8f;// widget override
	}

	public final boolean editable() {
		return mEditable;
	}

	public void setEditable(boolean editable) {
		mEditable = editable;
		cursor(editable ? CCGLCursorShape.IBEAM : CCGLCursorShape.ARROW);
	}

	public final boolean spinnable() {
		return mSpinnable;
	}

	public final void setSpinnable(boolean spinnable) {
		mSpinnable = spinnable;
	}

	public final Type value() {
		return _myText;
	}

	public void setValue(Type value) {
		_myText = value;
	}

	public final Type defaultValue() {
		return mDefaultValue;
	}

	public final void setDefaultValue(Type defaultValue) {
		mDefaultValue = defaultValue;
	}

	public final Alignment alignment() {
		return mAlignment;
	}

	public final void setAlignment(Alignment align) {
		mAlignment = align;
	}

	public final String units() {
		return mUnits;
	}

	public final void setUnits(String units) {
		mUnits = units;
	}

	public final int unitsImage() {
		return mUnitsImage;
	}

	public final void setUnitsImage(int image) {
		mUnitsImage = image;
	}

	/// Return the underlying regular expression specifying valid formats
	public final String format() {
		return mFormat;
	}

	/// Specify a regular expression specifying valid formats
	public final void setFormat(String format) {
		mFormat = format;
	}

	/// Return the placeholder text to be displayed while the text box is
	/// isEmpty.
	public final String placeholder() {
		return mPlaceholder;
	}

	/// Specify a placeholder text to be displayed while the text box is
	/// isEmpty.
	public final void setPlaceholder(String placeholder) {
		mPlaceholder = placeholder;
	}

	/// Set the \ref Theme used to draw this widget
	public void setTheme(Theme theme) {
		super.setTheme(theme);
		if (_myTheme != null) {
			_myFontSize = _myTheme.mTextBoxFontSize;
		}
	}

	public CCVector2i preferredSize(NanoVG ctx) {
		CCVector2i size = new CCVector2i(0, fontSize() * 1.4f);

		float uw = 0F;
		if (mUnitsImage > 0) {

			CCVector2i imageSize = ctx.imageSize(mUnitsImage);
			float uh = size.y * 0.4f;
			uw = imageSize.x * uh / imageSize.y;
		} else if (!mUnits.isEmpty()) {
			uw = ctx.textBounds(0, 0, mUnits);
		}
		float sw = 0F;
		if (mSpinnable) {
			sw = 14.0f;
		}

		float ts = ctx.textBounds(0, 0, _myText.toString());
		size.x = (int) (size.y + ts + uw + sw);
		return size;
	}

	public void draw(NanoVG ctx) {
		super.draw(ctx);

		NVGPaint bg = ctx.boxGradient(_myPosition.x + 1, _myPosition.y + 1 + 1.0f, _mySize.x - 2, _mySize.y - 2, 3, 4,
				new CCColor(255, 32), new CCColor(32, 32));
		NVGPaint fg1 = ctx.boxGradient(_myPosition.x + 1, _myPosition.y + 1 + 1.0f, _mySize.x - 2, _mySize.y - 2, 3, 4,
				new CCColor(150, 32), new CCColor(32, 32));
		NVGPaint fg2 = ctx.boxGradient(_myPosition.x + 1, _myPosition.y + 1 + 1.0f, _mySize.x - 2, _mySize.y - 2, 3, 4,
				new CCColor(255, 0, 0, 100), new CCColor(255, 0, 0, 50));

		ctx.beginPath();
		ctx.roundedRect(_myPosition.x + 1, _myPosition.y + 1 + 1.0f, _mySize.x - 2, _mySize.y - 2, 3);

		if (mEditable && focused()) {
			if (mValidFormat) {
				ctx.fillPaint(fg1);
			} else {
				ctx.fillPaint(fg2);
			}
		} else if (mSpinnable && mMouseDownPos.x != -1) {
			ctx.fillPaint(fg1);
		} else {
			ctx.fillPaint(bg);
		}

		ctx.fill();

		ctx.beginPath();
		ctx.roundedRect(_myPosition.x + 0.5f, _myPosition.y + 0.5f, _mySize.x - 1, _mySize.y - 1, 2.5f);
		ctx.strokeColor(new CCColor(0, 48));
		ctx.stroke();

		ctx.fontSize(fontSize());
		ctx.fontFace("sans");
		CCVector2i drawPos = new CCVector2i(_myPosition.x, _myPosition.y + _mySize.y * 0.5f + 1);

		float xSpacing = _mySize.y * 0.3f;

		float unitWidth = 0F;

		if (mUnitsImage > 0) {
			CCVector2i imageSize = ctx.imageSize(mUnitsImage);
			float unitHeight = _mySize.y * 0.4f;
			unitWidth = imageSize.x * unitHeight / imageSize.y;
			NVGPaint imgPaint = ctx.imagePattern(_myPosition.x + _mySize.x - xSpacing - unitWidth, drawPos.y - unitHeight * 0.5f,
					unitWidth, unitHeight, 0, mUnitsImage, _myIsEnabled ? 0.7f : 0.35f);
			ctx.beginPath();
			ctx.rect(_myPosition.x + _mySize.x - xSpacing - unitWidth, drawPos.y - unitHeight * 0.5f, unitWidth, unitHeight);
			ctx.fillPaint(imgPaint);
			ctx.fill();
			unitWidth += 2;
		} else if (!mUnits.isEmpty()) {
			unitWidth = ctx.textBounds(0, 0, mUnits);
			ctx.fillColor(new CCColor(255, _myIsEnabled ? 64 : 32));
			ctx.textAlign(NanoVG.ALIGN_RIGHT | NanoVG.ALIGN_MIDDLE);
			ctx.text(_myPosition.x + _mySize.x - xSpacing, drawPos.y, mUnits);
			unitWidth += 2;
		}

		float spinArrowsWidth = 0.0f;

		if (mSpinnable && !focused()) {
			spinArrowsWidth = 14.0f;

			ctx.fontFace("icons");
			ctx.fontSize(((_myFontSize < 0) ? _myTheme.mButtonFontSize : _myFontSize) * icon_scale());

			boolean spinning = mMouseDownPos.x != -1;

			/* up button */
			{
				boolean hover = _myMouseFocus && spinArea(mMousePos) == SpinArea.Top;
				ctx.fillColor((_myIsEnabled && (hover || spinning)) ? _myTheme.mTextColor : _myTheme.mDisabledTextColor);
				String icon = utf8(_myTheme.mTextBoxUpIcon);
				ctx.textAlign(NanoVG.ALIGN_LEFT | NanoVG.ALIGN_MIDDLE);
				CCVector2 iconPos = new CCVector2(_myPosition.x + 4.0f, _myPosition.y + _mySize.y / 2.0f - xSpacing / 2.0f);
				ctx.text(iconPos.x, iconPos.y, icon);
			}

			/* down button */
			{
				boolean hover = _myMouseFocus && spinArea(mMousePos) == SpinArea.Bottom;
				ctx.fillColor((_myIsEnabled && (hover || spinning)) ? _myTheme.mTextColor : _myTheme.mDisabledTextColor);
				String icon = utf8(_myTheme.mTextBoxDownIcon);
				ctx.textAlign(NanoVG.ALIGN_LEFT | NanoVG.ALIGN_MIDDLE);
				CCVector2 iconPos = new CCVector2(_myPosition.x + 4.0f, _myPosition.y + _mySize.y / 2.0f + xSpacing / 2.0f + 1.5f);
				ctx.text(iconPos.x, iconPos.y, icon);
			}

			ctx.fontSize(fontSize());
			ctx.fontFace("sans");
		}

		switch (mAlignment) {
		case Left:
			ctx.textAlign(NanoVG.ALIGN_LEFT | NanoVG.ALIGN_MIDDLE);
			drawPos.x += xSpacing + spinArrowsWidth;
			break;
		case Right:
			ctx.textAlign(NanoVG.ALIGN_RIGHT | NanoVG.ALIGN_MIDDLE);
			drawPos.x += _mySize.x - unitWidth - xSpacing;
			break;
		case Center:
			ctx.textAlign(NanoVG.ALIGN_CENTER | NanoVG.ALIGN_MIDDLE);
			drawPos.x += _mySize.x * 0.5f;
			break;
		}

		ctx.fontSize(fontSize());
		ctx.fillColor(_myIsEnabled && (!mCommitted || !_myText.toString().isEmpty()) ? _myTheme.mTextColor : _myTheme.mDisabledTextColor);

		// clip visible text area
		float clipX = _myPosition.x + xSpacing + spinArrowsWidth - 1.0f;
		float clipY = _myPosition.y + 1.0f;
		float clipWidth = _mySize.x - unitWidth - spinArrowsWidth - 2 * xSpacing + 2.0f;
		float clipHeight = _mySize.y - 3.0f;

		ctx.save();
		ctx.intersectScissor(clipX, clipY, clipWidth, clipHeight);

		CCVector2i oldDrawPos = new CCVector2i(drawPos);
		drawPos.x += mTextOffset;

		if (mCommitted) {
			ctx.text(drawPos.x, drawPos.y, _myText.toString().isEmpty() ? mPlaceholder : _myText.toString());
		} else {
			final int maxGlyphs = 1024;
			NVGGlyphPosition.Buffer glyphs = NVGGlyphPosition.malloc(maxGlyphs);
			float[] textBound = new float[4];
			ctx.textBounds(drawPos.x, drawPos.y, _myTextTmp.toString(), textBound);
			float lineh = textBound[3] - textBound[1];

			// find cursor positions
			int nglyphs = ctx.textGlyphPositions(drawPos.x, drawPos.y, _myTextTmp.toString(), glyphs);
			updateCursor(ctx, textBound[2], glyphs, nglyphs);

			// compute text offset
			int prevCPos = mCursorPos > 0 ? mCursorPos - 1 : 0;
			int nextCPos = mCursorPos < nglyphs ? mCursorPos + 1 : nglyphs;
			float prevCX = cursorIndex2Position(prevCPos, textBound[2], glyphs, nglyphs);
			float nextCX = cursorIndex2Position(nextCPos, textBound[2], glyphs, nglyphs);

			if (nextCX > clipX + clipWidth) {
				mTextOffset -= nextCX - (clipX + clipWidth) + 1;
			}
			if (prevCX < clipX) {
				mTextOffset += clipX - prevCX + 1;
			}

			drawPos.x = (int) (oldDrawPos.x + mTextOffset);

			// draw text with offset
			ctx.text(drawPos.x, drawPos.y, _myTextTmp.toString());
			ctx.textBounds(drawPos.x, drawPos.y, _myTextTmp.toString(), textBound);

			// recompute cursor positions
			nglyphs = ctx.textGlyphPositions(drawPos.x, drawPos.y, _myTextTmp.toString(), glyphs);

			if (mCursorPos > -1) {
				if (mSelectionPos > -1) {
					float caretx = cursorIndex2Position(mCursorPos, textBound[2], glyphs, nglyphs);
					float selx = cursorIndex2Position(mSelectionPos, textBound[2], glyphs, nglyphs);

					if (caretx > selx) {
						float tmp = caretx;
						caretx = selx;
						selx = tmp;
					}

					// draw selection
					ctx.beginPath();
					ctx.fillColor(new CCColor(255, 255, 255, 80));
					ctx.rect(caretx, drawPos.y - lineh * 0.5f, selx - caretx, lineh);
					ctx.fill();
				}

				float caretx = cursorIndex2Position(mCursorPos, textBound[2], glyphs, nglyphs);

				// draw cursor
				ctx.beginPath();
				ctx.moveTo(caretx, drawPos.y - lineh * 0.5f);
				ctx.lineTo(caretx, drawPos.y + lineh * 0.5f);
				ctx.strokeColor(new CCColor(255, 192, 0, 255));
				ctx.strokeWidth(1.0f);
				ctx.stroke();
			}
		}
		ctx.restore();
	}

	@Override
	public boolean mouseButtonEvent(CCVector2i p, CCGLMouseEvent event) {

		if (event.button == CCGLMouseButton.BUTTON_1 && event.action == CCGLAction.PRESS && !_myIsFocused) {
			if (!mSpinnable || spinArea(p) == SpinArea.None) // not on scrolling
																// arrows
			{
				requestFocus();
			}
		}

		if (mEditable && focused()) {
			if (event.action == CCGLAction.PRESS) {
				mMouseDownPos = p;
				mMouseDownModifier = event;

				double time = event.time;
				if (time - mLastClick < 0.25) {
					/* Double-click: select all text */
					mSelectionPos = 0;
					mCursorPos = (int) _myTextTmp.length();
					mMouseDownPos = new CCVector2i(-1, -1);
				}
				mLastClick = time;
			} else {
				mMouseDownPos = new CCVector2i(-1, -1);
				mMouseDragPos = new CCVector2i(-1, -1);
			}
			return true;
		} else if (mSpinnable && !focused()) {
			if (event.action == CCGLAction.PRESS) {
				if (spinArea(p) == SpinArea.None) {
					mMouseDownPos = p;
					mMouseDownModifier = event;

					double time = event.time;
					if (time - mLastClick < 0.25) {
						/* Double-click: reset to default value */
						_myText = mDefaultValue;

						mCallback.proxy().event(_myText);

						mMouseDownPos = new CCVector2i(-1, -1);
					}
					mLastClick = time;
				} else {
					mMouseDownPos = new CCVector2i(-1, -1);
					mMouseDragPos = new CCVector2i(-1, -1);
				}
			} else {
				mMouseDownPos = new CCVector2i(-1, -1);
				mMouseDragPos = new CCVector2i(-1, -1);
			}
			return true;
		}

		return false;
	}

	@Override
	public boolean mouseMotionEvent(CCVector2i p, CCVector2i UnnamedParameter1, CCGLMouseEvent theEvent) {
		mMousePos = p;

		if (!mEditable) {
			cursor(CCGLCursorShape.ARROW);
		} else if (mSpinnable && !focused() && spinArea(mMousePos) != SpinArea.None) {
			cursor(CCGLCursorShape.HAND);
		} else {
			cursor(CCGLCursorShape.IBEAM);
		}

		if (mEditable && focused()) {
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseDragEvent(CCVector2i p, CCVector2i UnnamedParameter1, CCGLMouseEvent theEvent) {
		mMousePos = p;
		mMouseDragPos = p;

		if (mEditable && focused()) {
			return true;
		}
		return false;
	}
	
	public abstract Type stringToValue(String theString);

	@Override
	public boolean focusEvent(boolean focused) {
		super.focusEvent(focused);

		Type backup = _myText;

		if (mEditable) {
			if (focused) {
				_myTextTmp = new StringBuilder(_myText.toString());
				mCommitted = false;
				mCursorPos = 0;
			} else {
				if (mValidFormat) {
					if (_myTextTmp.toString().equals("")) {
						_myText = mDefaultValue;
					} else {
						_myText = stringToValue(_myTextTmp.toString());
					}
				}

				mCallback.proxy().event(_myText);

				mValidFormat = true;
				mCommitted = true;
				mCursorPos = -1;
				mSelectionPos = -1;
				mTextOffset = 0;
			}

			mValidFormat = (_myTextTmp.toString().equals("")) || checkFormat(_myTextTmp.toString(), mFormat);
		}

		return true;
	}

	@Override
	public boolean keyboardEvent(CCGLKeyEvent theEvent) {
		if (mEditable && focused()) {
			if (theEvent.action == CCGLAction.PRESS || theEvent.action == CCGLAction.REPEAT) {
				if (theEvent.key == CCGLKey.KEY_LEFT) {
					if (theEvent.isShiftDown()) {
						if (mSelectionPos == -1) {
							mSelectionPos = mCursorPos;
						}
					} else {
						mSelectionPos = -1;
					}

					if (mCursorPos > 0) {
						mCursorPos--;
					}
				} else if (theEvent.key == CCGLKey.KEY_RIGHT) {
					if (theEvent.isShiftDown()) {
						if (mSelectionPos == -1) {
							mSelectionPos = mCursorPos;
						}
					} else {
						mSelectionPos = -1;
					}

					if (mCursorPos < (int) _myTextTmp.length()) {
						mCursorPos++;
					}
				} else if (theEvent.key == CCGLKey.KEY_HOME) {
					if (theEvent.isShiftDown()) {
						if (mSelectionPos == -1) {
							mSelectionPos = mCursorPos;
						}
					} else {
						mSelectionPos = -1;
					}

					mCursorPos = 0;
				} else if (theEvent.key == CCGLKey.KEY_END) {
					if (theEvent.isShiftDown()) {
						if (mSelectionPos == -1) {
							mSelectionPos = mCursorPos;
						}
					} else {
						mSelectionPos = -1;
					}

					mCursorPos = (int) _myTextTmp.length();
				} else if (theEvent.key == CCGLKey.KEY_BACKSPACE) {
					if (!deleteSelection()) {
						if (mCursorPos > 0) {
							_myTextTmp.deleteCharAt(mCursorPos - 1);
							mCursorPos--;
						}
					}
				} else if (theEvent.key == CCGLKey.KEY_DELETE) {
					if (!deleteSelection()) {
						if (mCursorPos < (int) _myTextTmp.length()) {
							_myTextTmp.deleteCharAt(mCursorPos);
						}
					}
				} else if (theEvent.key == CCGLKey.KEY_ENTER) {
					if (!mCommitted) {
						focusEvent(false);
					}
				} else if (theEvent.key == CCGLKey.KEY_A && theEvent.isControlDown()) {
					mCursorPos = (int) _myTextTmp.length();
					mSelectionPos = 0;
				} else if (theEvent.key == CCGLKey.KEY_X && theEvent.isControlDown()) {
					copySelection();
					deleteSelection();
				} else if (theEvent.key == CCGLKey.KEY_C && theEvent.isControlDown()) {
					copySelection();
				} else if (theEvent.key == CCGLKey.KEY_V && theEvent.isControlDown()) {
					deleteSelection();
					pasteFromClipboard();
				}

				mValidFormat = (_myTextTmp.toString().equals("")) || checkFormat(_myTextTmp.toString(), mFormat);
			}

			return true;
		}

		return false;
	}

	@Override
	public boolean keyboardCharacterEvent(int codepoint) {
		if (mEditable && focused()) {

			deleteSelection();
			_myTextTmp.insert(mCursorPos, utf8(codepoint));
			mCursorPos++;

			mValidFormat = (_myTextTmp.toString().equals("")) || checkFormat(_myTextTmp.toString(), mFormat);

			return true;
		}

		return false;
	}

	public boolean checkFormat(String input, String format) {
		return true;
		// if (format.length() == 0)
		// {
		// return true;
		// }
		// try
		// {
		// std::regex regex = new std::regex(format);
		// return input.(input, regex);
		// }
		// catch (std::regex_error)
		// {
		// std::cerr << "Warning: cannot validate text field due to lacking
		// regular expression support. please compile with GCC >= 4.9" <<
		// std::endl;
		// return true;
		//
		// }
	}

	public boolean copySelection() {
		if (mSelectionPos > -1) {
			Screen sc = (Screen) ((this.window().parent() instanceof Screen) ? this.window().parent() : null);
			if (sc == null) {
				return false;
			}

			int begin = mCursorPos;
			int end = mSelectionPos;

			if (begin > end) {
				int tmp = begin;
				begin = end;
				end = tmp;
			}

			sc.glfwWindow().clipboardString(_myTextTmp.substring(begin, end));
			return true;
		}

		return false;
	}

	public void pasteFromClipboard() {
		Screen sc = (Screen) ((this.window().parent() instanceof Screen) ? this.window().parent() : null);
		if (sc == null) {
			return;
		}

		_myTextTmp.insert(mCursorPos, sc.glfwWindow().clipboardString());
	}

	public boolean deleteSelection() {
		if (mSelectionPos > -1) {
			int begin = mCursorPos;
			int end = mSelectionPos;

			if (begin > end) {
				int tmp = begin;
				begin = end;
				end = tmp;
			}

			if (begin == end - 1) {
				_myTextTmp.deleteCharAt(begin);
			} else {
				_myTextTmp.delete(begin, end);
			}

			mCursorPos = begin;
			mSelectionPos = -1;
			return true;
		}

		return false;
	}

	public void updateCursor(NanoVG UnnamedParameter1, float lastx, NVGGlyphPosition.Buffer glyphs, int size) {
		// handle mouse cursor events
		if (mMouseDownPos.x != -1) {
			if (mMouseDownModifier.isShiftDown()) {
				if (mSelectionPos == -1) {
					mSelectionPos = mCursorPos;
				}
			} else {
				mSelectionPos = -1;
			}

			mCursorPos = position2CursorIndex(mMouseDownPos.x, lastx, glyphs, size);

			mMouseDownPos = new CCVector2i(-1, -1);
		} else if (mMouseDragPos.x != -1) {
			if (mSelectionPos == -1) {
				mSelectionPos = mCursorPos;
			}

			mCursorPos = position2CursorIndex(mMouseDragPos.x, lastx, glyphs, size);
		} else {
			// set cursor to last character
			if (mCursorPos == -2) {
				mCursorPos = size;
			}
		}

		if (mCursorPos == mSelectionPos) {
			mSelectionPos = -1;
		}
	}

	public float cursorIndex2Position(int index, float lastx, NVGGlyphPosition.Buffer glyphs, int size) {
		float pos = 0F;
		if (index == size) {
			pos = lastx; // last character
		} else {
			pos = glyphs.get(index).x();
		}

		return pos;
	}

	public int position2CursorIndex(float posx, float lastx, NVGGlyphPosition.Buffer glyphs, int size) {
		int mCursorId = 0;
		float caretx = glyphs.get(mCursorId).x();
		for (int j = 1; j < size; j++) {
			if (Math.abs(caretx - posx) > Math.abs(glyphs.get(j).x() - posx)) {
				mCursorId = j;
				caretx = glyphs.get(mCursorId).x();
			}
		}
		if (Math.abs(caretx - posx) > Math.abs(lastx - posx)) {
			mCursorId = size;
		}

		return mCursorId;
	}

	public ValueBox.SpinArea spinArea(CCVector2i pos) {
		if (0 <= pos.x - _myPosition.x && pos.x - _myPosition.x < 14.0f) { // on scrolling
																// arrows
			if (_mySize.y >= pos.y - _myPosition.y && pos.y - _myPosition.y <= _mySize.y / 2.0f) { // top
																					// part
				return SpinArea.Top;
			} else if (0.0f <= pos.y - _myPosition.y && pos.y - _myPosition.y > _mySize.y / 2.0f) { // bottom
																					// part
				return SpinArea.Bottom;
			}
		}
		return SpinArea.None;
	}

	public void save(CCDataElement s) {
		super.save(s);
		s.addAttribute("editable", mEditable);
		s.addAttribute("spinnable", mSpinnable);
		s.addAttribute("committed", mCommitted);
		s.addAttribute("value", _myText.toString());
		s.addAttribute("defaultValue", mDefaultValue.toString());
		s.addAttribute("alignment", mAlignment.toString());
		s.addAttribute("units", mUnits);
		s.addAttribute("format", mFormat);
		s.addAttribute("unitsImage", mUnitsImage);
		s.addAttribute("validFormat", mValidFormat);
		s.addAttribute("valueTemp", _myTextTmp.toString());
		s.addAttribute("cursorPos", mCursorPos);
		s.addAttribute("selectionPos", mSelectionPos);
	}

	public boolean load(CCDataElement s) {
		if (!super.load(s)) {
			return false;
		}
		try {
			mEditable = s.booleanAttribute("editable");
			mSpinnable = s.booleanAttribute("spinnable");
			mCommitted = s.booleanAttribute("committed");
			_myText = stringToValue(s.attribute("value"));
			mDefaultValue = stringToValue(s.attribute("defaultValue"));
			mAlignment = Alignment.valueOf(s.attribute("alignment"));
			mUnits = s.attribute("units");
			mFormat = s.attribute("format");
			mUnitsImage = s.intAttribute("unitsImage");
			mValidFormat = s.booleanAttribute("validFormat");
			_myTextTmp = new StringBuilder(s.attribute("valueTemp"));
			mCursorPos = s.intAttribute("cursorPos");
			mSelectionPos = s.intAttribute("selectionPos");
		} catch (Exception e) {
			return false;
		}

		mMousePos = new CCVector2i(-1);
		mMouseDownPos = new CCVector2i(-1);
		mMouseDragPos = new CCVector2i(-1);
		mMouseDownModifier = null;
		mTextOffset = 0;
		return true;
	}

	/// The location (if any) for the spin area.
	protected enum SpinArea {
		None, Top, Bottom;

	}

}
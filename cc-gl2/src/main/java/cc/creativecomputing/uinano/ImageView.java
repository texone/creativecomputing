package cc.creativecomputing.uinano;
public class ImageView extends Widget
{
//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public ImageView(Widget parent, int imageID)
	{
		this.Widget = parent;
		this.mImageID = imageID;
		this.mScale = 1.0f;
		this.mOffset = Vector2f.Zero();
		this.mFixedScale = false;
		this.mFixedOffset = false;
		this.mPixelInfoCallback = null;
		updateImageParameters();
		mShader.init("ImageViewShader", defaultImageViewVertexShader, defaultImageViewFragmentShader);
    
		MatrixXu indices = new MatrixXu(3, 2);
		indices.col(0) << 0, 1, 2;
		indices.col(1) << 2, 3, 1;
    
		MatrixXf vertices = new MatrixXf(2, 4);
		vertices.col(0) << 0, 0;
		vertices.col(1) << 1, 0;
		vertices.col(2) << 0, 1;
		vertices.col(3) << 1, 1;
    
		mShader.bind();
		mShader.uploadIndices(indices);
		mShader.uploadAttrib("vertex", vertices);
	}

//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public void close()
	{
		mShader.free();
	}

//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public void bindImage(GLuint imageId)
	{
		mImageID = imageId;
		updateImageParameters();
		fit();
	}

//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public Vector2f imageCoordinateAt(Vector2f position)
	{
		Vector2f imagePosition = position - mOffset;
		return imagePosition / mScale;
	}

//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public Vector2f clampedImageCoordinateAt(Vector2f position)
	{
	//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		auto imageCoordinate = imageCoordinateAt(position);
		return imageCoordinate.cwiseMax(Vector2f.Zero()).cwiseMin(imageSizeF());
	}

//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public Vector2f positionForCoordinate(Vector2f imageCoordinate)
	{
		return mScale * imageCoordinate + mOffset;
	}

//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public void setImageCoordinateAt(Vector2f position, Vector2f imageCoordinate)
	{
		// Calculate where the new offset must be in order to satisfy the image position equation.
		// Round the floating point values to balance out the floating point to integer conversions.
		mOffset = position - (imageCoordinate * mScale);
    
		// Clamp offset so that the image remains near the screen.
		mOffset = mOffset.cwiseMin(sizeF()).cwiseMax(-scaledImageSizeF());
	}

//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public void center()
	{
		mOffset = (sizeF() - scaledImageSizeF()) / 2;
	}

//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public void fit()
	{
		// Calculate the appropriate scaling factor.
		mScale = (sizeF().cwiseQuotient(imageSizeF())).minCoeff();
		center();
	}

//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public void setScaleCentered(float scale)
	{
	//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		auto centerPosition = sizeF() / 2;
	//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		auto p = imageCoordinateAt(centerPosition);
		mScale = scale;
		setImageCoordinateAt(centerPosition, p);
	}

//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public void moveOffset(Vector2f delta)
	{
		// Apply the delta to the offset.
		mOffset += delta;
    
		// Prevent the image from going out of bounds.
	//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		auto scaledSize = scaledImageSizeF();
		if (mOffset.x() + scaledSize.x() < 0)
		{
			mOffset.x() = -scaledSize.x();
		}
		if (mOffset.x() > sizeF().x())
		{
			mOffset.x() = sizeF().x();
		}
		if (mOffset.y() + scaledSize.y() < 0)
		{
			mOffset.y() = -scaledSize.y();
		}
		if (mOffset.y() > sizeF().y())
		{
			mOffset.y() = sizeF().y();
		}
	}

//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public void zoom(int amount, Vector2f focusPosition)
	{
	//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		auto focusedCoordinate = imageCoordinateAt(focusPosition);
		float scaleFactor = Math.pow(mZoomSensitivity, amount);
		mScale = Math.max(0.01f, scaleFactor * mScale);
		setImageCoordinateAt(focusPosition, focusedCoordinate);
	}

//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public boolean mouseDragEvent(Vector2i p, Vector2i rel, int button, int UnnamedParameter1)
	{
		if ((button & (1 << GLFW_MOUSE_BUTTON_LEFT)) != 0 && !mFixedOffset)
		{
			setImageCoordinateAt((p + rel).cast<float>(), imageCoordinateAt(p.cast<float>()));
			return true;
		}
		return false;
	}

//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public boolean gridVisible()
	{
		return (mGridThreshold != -1) && (mScale > mGridThreshold);
	}

//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public boolean pixelInfoVisible()
	{
		return mPixelInfoCallback && (mPixelInfoThreshold != -1) && (mScale > mPixelInfoThreshold);
	}

//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public boolean helpersVisible()
	{
		return gridVisible() || pixelInfoVisible();
	}

//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public boolean scrollEvent(Vector2i p, Vector2f rel)
	{
		if (mFixedScale)
		{
			return false;
		}
		float v = rel.y();
		if (Math.abs(v) < 1)
		{
			v = std::copysign(1.0f, v);
		}
		zoom(v, (p - position()).cast<float>());
		return true;
	}

//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public boolean keyboardEvent(int key, int UnnamedParameter1, int action, int modifiers)
	{
		if (action != 0)
		{
			switch (key)
			{
			case GLFW_KEY_LEFT:
				if (!mFixedOffset)
				{
					if ((GLFW_MOD_CONTROL & modifiers) != 0)
					{
						moveOffset(Vector2f(30, 0));
					}
					else
					{
						moveOffset(Vector2f(10, 0));
					}
					return true;
				}
				break;
			case GLFW_KEY_RIGHT:
				if (!mFixedOffset)
				{
					if ((GLFW_MOD_CONTROL & modifiers) != 0)
					{
						moveOffset(Vector2f(-30, 0));
					}
					else
					{
						moveOffset(Vector2f(-10, 0));
					}
					return true;
				}
				break;
			case GLFW_KEY_DOWN:
				if (!mFixedOffset)
				{
					if ((GLFW_MOD_CONTROL & modifiers) != 0)
					{
						moveOffset(Vector2f(0, -30));
					}
					else
					{
						moveOffset(Vector2f(0, -10));
					}
					return true;
				}
				break;
			case GLFW_KEY_UP:
				if (!mFixedOffset)
				{
					if ((GLFW_MOD_CONTROL & modifiers) != 0)
					{
						moveOffset(Vector2f(0, 30));
					}
					else
					{
						moveOffset(Vector2f(0, 10));
					}
					return true;
				}
				break;
			}
		}
		return false;
	}

//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public boolean keyboardCharacterEvent(uint codepoint)
	{
		switch (codepoint)
		{
		case '-':
			if (!mFixedScale)
			{
				zoom(-1, sizeF() / 2);
				return true;
			}
			break;
		case '+':
			if (!mFixedScale)
			{
				zoom(1, sizeF() / 2);
				return true;
			}
			break;
		case 'c':
			if (!mFixedOffset)
			{
				center();
				return true;
			}
			break;
		case 'f':
			if (!mFixedOffset && !mFixedScale)
			{
				fit();
				return true;
			}
			break;
		case '1':
	case '2':
	case '3':
	case '4':
	case '5':
		case '6':
	case '7':
	case '8':
	case '9':
			if (!mFixedScale)
			{
				setScaleCentered(1 << (codepoint - '1'));
				return true;
			}
			break;
		default:
			return false;
		}
		return false;
	}

//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public Vector2i preferredSize(NVGcontext UnnamedParameter1)
	{
		return mImageSize;
	}

//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public void performLayout(NVGcontext ctx)
	{
		Widget.performLayout(ctx);
		center();
	}

//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public void draw(NVGcontext ctx)
	{
		Widget.draw(ctx);
		nvgEndFrame(ctx); // Flush the NanoVG draw stack, not necessary to call nvgBeginFrame afterwards.
    
		drawImageBorder(ctx);
    
		// Calculate several variables that need to be send to OpenGL in order for the image to be
		// properly displayed inside the widget.
		Screen screen = ( Screen)((this.window().parent() instanceof  Screen) ? this.window().parent() : null);
		assert screen;
		Vector2f screenSize = screen.size().cast<float>();
		Vector2f scaleFactor = mScale * imageSizeF().cwiseQuotient(screenSize);
		Vector2f positionInScreen = absolutePosition().cast<float>();
		Vector2f positionAfterOffset = positionInScreen + mOffset;
		Vector2f imagePosition = positionAfterOffset.cwiseQuotient(screenSize);
		glEnable(GL_SCISSOR_TEST);
		float r = screen.pixelRatio();
		glScissor(positionInScreen.x() * r, (screenSize.y() - positionInScreen.y() - size().y()) * r, size().x() * r, size().y() * r);
		mShader.bind();
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, mImageID);
		mShader.setUniform("image", 0);
		mShader.setUniform("scaleFactor", scaleFactor);
		mShader.setUniform("position", imagePosition);
		mShader.drawIndexed(GL_TRIANGLES, 0, 2);
		glDisable(GL_SCISSOR_TEST);
    
		if (helpersVisible())
		{
			drawHelpers(ctx);
		}
    
		drawWidgetBorder(ctx);
	}

//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public void updateImageParameters()
	{
		// Query the width of the OpenGL texture.
		glBindTexture(GL_TEXTURE_2D, mImageID);
		GLint w = new GLint();
		GLint h = new GLint();
		glGetTexLevelParameteriv(GL_TEXTURE_2D, 0, GL_TEXTURE_WIDTH, w);
		glGetTexLevelParameteriv(GL_TEXTURE_2D, 0, GL_TEXTURE_HEIGHT, h);
		mImageSize = Vector2i(w, h);
	}

//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public void drawWidgetBorder(NVGcontext ctx)
	{
		nvgBeginPath(ctx);
		nvgStrokeWidth(ctx, 1);
		nvgRoundedRect(ctx, mPos.x() + 0.5f, mPos.y() + 0.5f, mSize.x() - 1, mSize.y() - 1, 0);
		nvgStrokeColor(ctx, mTheme.mWindowPopup);
		nvgStroke(ctx);
    
		nvgBeginPath(ctx);
		nvgRoundedRect(ctx, mPos.x() + 0.5f, mPos.y() + 0.5f, mSize.x() - 1, mSize.y() - 1, mTheme.mButtonCornerRadius);
		nvgStrokeColor(ctx, mTheme.mBorderDark);
		nvgStroke(ctx);
	}

//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public void drawImageBorder(NVGcontext ctx)
	{
		nvgSave(ctx);
		nvgBeginPath(ctx);
		nvgScissor(ctx, mPos.x(), mPos.y(), mSize.x(), mSize.y());
		nvgStrokeWidth(ctx, 1.0f);
		Vector2i borderPosition = mPos + mOffset.cast<int>();
		Vector2i borderSize = scaledImageSizeF().cast<int>();
		nvgRect(ctx, borderPosition.x() - 0.5f, borderPosition.y() - 0.5f, borderSize.x() + 1, borderSize.y() + 1);
		nvgStrokeColor(ctx, Color(1.0f, 1.0f, 1.0f, 1.0f));
		nvgStroke(ctx);
		nvgResetScissor(ctx);
		nvgRestore(ctx);
	}

//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public void drawHelpers(NVGcontext ctx)
	{
		// We need to apply mPos after the transformation to account for the position of the widget
		// relative to the parent.
		Vector2f upperLeftCorner = positionForCoordinate(Vector2f.Zero()) + positionF();
		Vector2f lowerRightCorner = positionForCoordinate(imageSizeF()) + positionF();
		if (gridVisible())
		{
			drawPixelGrid(ctx, upperLeftCorner, lowerRightCorner, mScale);
		}
		if (pixelInfoVisible())
		{
			drawPixelInfo(ctx, mScale);
		}
	}

//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public void drawPixelGrid(NVGcontext ctx, Vector2f upperLeftCorner, Vector2f lowerRightCorner, float stride)
	{
		nvgBeginPath(ctx);
    
		// Draw the vertical grid lines
		float currentX = upperLeftCorner.x();
		while (currentX <= lowerRightCorner.x())
		{
			nvgMoveTo(ctx, Math.round(currentX), Math.round(upperLeftCorner.y()));
			nvgLineTo(ctx, Math.round(currentX), Math.round(lowerRightCorner.y()));
			currentX += stride;
		}
    
		// Draw the horizontal grid lines
		float currentY = upperLeftCorner.y();
		while (currentY <= lowerRightCorner.y())
		{
			nvgMoveTo(ctx, Math.round(upperLeftCorner.x()), Math.round(currentY));
			nvgLineTo(ctx, Math.round(lowerRightCorner.x()), Math.round(currentY));
			currentY += stride;
		}
    
		nvgStrokeWidth(ctx, 1.0f);
		nvgStrokeColor(ctx, Color(1.0f, 1.0f, 1.0f, 0.2f));
		nvgStroke(ctx);
	}

//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public void drawPixelInfo(NVGcontext ctx, float stride)
	{
		// Extract the image coordinates at the two corners of the widget.
		Vector2i topLeft = clampedImageCoordinateAt(Vector2f.Zero()).unaryExpr((drawPixelInfo_float x) =>
		{
			return Math.floor(x);
		}).cast<int>();
    
		Vector2i bottomRight = clampedImageCoordinateAt(sizeF()).unaryExpr((drawPixelInfo_float x) =>
		{
			return Math.ceil(x);
		}).cast<int>();
    
		// Extract the positions for where to draw the text.
		Vector2f currentCellPosition = (positionF() + positionForCoordinate(topLeft.cast<drawPixelInfo_float>()));
    
		drawPixelInfo_float xInitialPosition = currentCellPosition.x();
		int xInitialIndex = topLeft.x();
    
		// Properly scale the pixel information for the given stride.
	//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		auto fontSize = stride * mFontScaleFactor;
	//C++ TO JAVA CONVERTER NOTE: This static local variable declaration (not allowed in Java) has been moved just prior to the method:
	//	static constexpr float maxFontSize = 30.0f;
	//C++ TO JAVA CONVERTER WARNING: The following line was determined to be a copy assignment (rather than a reference assignment) - this should be verified and a 'copyFrom' method should be created if it does not yet exist:
	//ORIGINAL LINE: fontSize = fontSize > maxFontSize ? maxFontSize : fontSize;
		fontSize.copyFrom(fontSize > maxFontSize != 0 ? maxFontSize : fontSize);
		nvgBeginPath(ctx);
		nvgFontSize(ctx, fontSize);
		nvgTextAlign(ctx, NVG_ALIGN_CENTER | NVG_ALIGN_TOP);
		nvgFontFace(ctx, "sans");
		while (topLeft.y() != bottomRight.y())
		{
			while (topLeft.x() != bottomRight.x())
			{
				writePixelInfo(ctx, currentCellPosition, topLeft, stride, fontSize);
				currentCellPosition.x() += stride;
				++topLeft.x();
			}
			currentCellPosition.x() = xInitialPosition;
			currentCellPosition.y() += stride;
			++topLeft.y();
			topLeft.x() = xInitialIndex;
		}
	}

//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public void writePixelInfo(NVGcontext ctx, Vector2f cellPosition, Vector2i pixel, float stride, float fontSize)
	{
	//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		auto pixelData = mPixelInfoCallback(pixel);
	//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		auto pixelDataRows = tokenize(pixelData.first);
    
		// If no data is provided for this pixel then simply return.
		if (pixelDataRows.empty())
		{
			return;
		}
    
		nvgFillColor(ctx, pixelData.second);
		float yOffset = (stride - fontSize * pixelDataRows.size()) / 2;
		for (size_t i = 0; i != pixelDataRows.size(); ++i)
		{
			nvgText(ctx, cellPosition.x() + stride / 2, cellPosition.y() + yOffset, pixelDataRows[i].data(), null);
			yOffset += fontSize;
		}
	}
}
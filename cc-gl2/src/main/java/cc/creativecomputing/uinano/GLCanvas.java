package cc.creativecomputing.uinano;
public class GLCanvas
{
//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public void drawWidgetBorder(NVGcontext ctx)
	{
		nvgBeginPath(ctx);
		nvgStrokeWidth(ctx, 1.0f);
		nvgRoundedRect(ctx, mPos.x() - 0.5f, mPos.y() - 0.5f, mSize.x() + 1, mSize.y() + 1, mTheme.mWindowCornerRadius);
		nvgStrokeColor(ctx, mTheme.mBorderLight);
		nvgRoundedRect(ctx, mPos.x() - 1.0f, mPos.y() - 1.0f, mSize.x() + 2, mSize.y() + 2, mTheme.mWindowCornerRadius);
		nvgStrokeColor(ctx, mTheme.mBorderDark);
		nvgStroke(ctx);
	}

//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public void draw(NVGcontext ctx)
	{
		Widget.draw(ctx);
		nvgEndFrame(ctx);
    
		if (mDrawBorder)
		{
			drawWidgetBorder(ctx);
		}
    
		Screen screen = this.screen();
		assert screen;
    
		float pixelRatio = screen.pixelRatio();
		Vector2f screenSize = screen.size().cast<float>();
		Vector2i positionInScreen = absolutePosition();
    
		Vector2i size = (mSize.cast<float>() * pixelRatio).cast<int>();
		Vector2i imagePosition = (Vector2f(positionInScreen[0], screenSize[1] - positionInScreen[1] - (float) mSize[1]) * pixelRatio).cast<int>();
    
		GLint[] storedViewport = tangible.Arrays.initializeWithDefaultGLintInstances(4);
		glGetIntegerv(GL_VIEWPORT, storedViewport);
    
		glViewport(imagePosition[0], imagePosition[1], size[0], size[1]);
    
		glEnable(GL_SCISSOR_TEST);
		glScissor(imagePosition[0], imagePosition[1], size[0], size[1]);
		glClearColor(mBackgroundColor[0], mBackgroundColor[1], mBackgroundColor[2], mBackgroundColor[3]);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    
		this.drawGL();
    
		glDisable(GL_SCISSOR_TEST);
		glViewport(storedViewport[0], storedViewport[1], storedViewport[2], storedViewport[3]);
	}

//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public void save(Serializer s)
	{
		Widget.save(s);
		s.set("backgroundColor", mBackgroundColor);
		s.set("drawBorder", mDrawBorder);
	}

//C++ TO JAVA CONVERTER WARNING: The original C++ declaration of the following method implementation was not found:
	public boolean load(Serializer s)
	{
		if (!Widget.load(s))
		{
			return false;
		}
		if (!s.get("backgroundColor", mBackgroundColor))
		{
			return false;
		}
		if (!s.get("drawBorder", mDrawBorder))
		{
			return false;
		}
		return true;
	}
}
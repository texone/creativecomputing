package cc.creativecomputing.uinano;

import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2i;
import cc.creativecomputing.ui.Button.Flags;
import cc.creativecomputing.ui.layout.Alignment;
import cc.creativecomputing.ui.layout.BoxLayout;
import cc.creativecomputing.ui.layout.GroupLayout;

public class ExampleApplication extends Screen implements java.io.Closeable
{
	public ExampleApplication()
	{
		super(new CCVector2i(1024, 768), "NanoGUI Test");

		Window window = new Window(this, "Button demo");
		window.setPosition(new CCVector2i(15, 15));
		window.setLayout(new GroupLayout());

		/* No need to store a pointer, the data structure will be automatically
		   freed when the parent window is deleted */
		new Label(window, "Push buttons", "sans-bold");

		Button b = new Button(window, "Plain button");
		b.mCallback.add(() ->
		{
			System.out.print("pushed!");
			System.out.print("\n");
		});
		b.setTooltip("short tooltip");

		/* Alternative construction notation using variadic template */
		b = new Button(window,"Styled", TypoIcon.ICON_ROCKET.id);
		b.setBackgroundColor(new CCColor(0, 0, 255, 25));
		b.mCallback.add(() ->
		{
			System.out.print("pushed!");
			System.out.print("\n");
		});
		b.setTooltip("This button has a fairly long tooltip. It is so long, in " + "fact, that the shown text will span several lines.");

		new Label(window, "Toggle buttons", "sans-bold");
		b = new Button(window, "Toggle me");
		b.mToggle = true;
		b.mChangeCallback.add((boolean state) ->
		{
			System.out.print("Toggle button state: ");
			System.out.print(state);
			System.out.print("\n");
		});

		new Label(window, "Radio buttons", "sans-bold");
		b = new Button(window, "Radio button 1");
		b.setFlags(Flags.RadioButton);
		b = new Button(window, "Radio button 2");
		b.setFlags(Flags.RadioButton);

		new Label(window, "A tool palette", "sans-bold");
		Widget tools = new Widget(window);
		tools.setLayout(new BoxLayout(Orientation.Horizontal, Alignment.Middle, 0, 6));

		b = new ToolButton(tools, TypoIcon.ICON_CLOUD.id);
		b = new ToolButton(tools, TypoIcon.ICON_CONTROLLER_FAST_FORWARD.id);
		b = new ToolButton(tools, TypoIcon.ICON_COMPASS.id);
		b = new ToolButton(tools, TypoIcon.ICON_INSTALL.id);

		new Label(window, "Popup buttons", "sans-bold");
		PopupButton popupBtn = new PopupButton(window, "Popup", TypoIcon.ICON_EXPORT.id);
		Popup popup = popupBtn.popup();
		popup.setLayout(new GroupLayout());
		new Label(popup, "Arbitrary widgets can be placed here");
		new CheckBox(popup, "A check box");
		// popup right
		popupBtn = new PopupButton(popup, "Recursive popup", TypoIcon.ICON_FLASH.id);
		Popup popupRight = popupBtn.popup();
		popupRight.setLayout(new GroupLayout());
		new CheckBox(popupRight, "Another check box");
		// popup left
		popupBtn = new PopupButton(popup, "Recursive popup", TypoIcon.ICON_FLASH.id);
		popupBtn.setSide(Popup.Side.Left);
		Popup popupLeft = popupBtn.popup();
		popupLeft.setLayout(new GroupLayout());
		new CheckBox(popupLeft, "Another check box");

		window = new Window(this, "Basic widgets");
		window.setPosition(new CCVector2i(200, 15));
		window.setLayout(new GroupLayout());

		new Label(window, "Message dialog", "sans-bold");
		tools = new Widget(window);
		tools.setLayout(new BoxLayout(Orientation.Horizontal, Alignment.Middle, 0, 6));
		b = new Button(tools, "Info");
		b.mCallback.add(() ->
		{
			MessageDialog dlg = new MessageDialog(this, MessageDialog.Type.Information, "Title", "This is an information message");
			dlg.mCallback.add((int result) ->
			{
				System.out.print("Dialog result: ");
				System.out.print(result);
				System.out.print("\n");
			});
		});
		b = new Button(tools, "Warn");
		b.mCallback.add(() ->
		{
			MessageDialog dlg = new MessageDialog(this, MessageDialog.Type.Warning, "Title", "This is a warning message");
			dlg.mCallback.add((int result) ->
			{
				System.out.print("Dialog result: ");
				System.out.print(result);
				System.out.print("\n");
			});
		});
		b = new Button(tools, "Ask");
		b.mCallback.add(() ->
		{
			MessageDialog dlg = new MessageDialog(this, MessageDialog.Type.Warning, "Title", "This is a question message", "Yes", "No", true);
			dlg.mCallback.add((int result) ->
			{
				System.out.print("Dialog result: ");
				System.out.print(result);
				System.out.print("\n");
			});
		});

//		vector<pair<Integer, string>> icons = loadImageDirectory(mNVGContext, "icons");
////C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//		///#if _WIN32
//			string resourcesFolderPath = "../resources/";
//		///#else
//			string resourcesFolderPath = "./";
//		///#endif
//
//		new Label(window, "Image panel & scroll panel", "sans-bold");
//		PopupButton imagePanelBtn = new PopupButton(window, "Image Panel");
//		imagePanelBtn.setIcon(ENTYPO_ICON_FOLDER);
//		popup = imagePanelBtn.popup();
//		VScrollPanel vscroll = new VScrollPanel(popup);
//		ImagePanel imgPanel = new ImagePanel(vscroll);
//		imgPanel.setImages(icons);
//		popup.setFixedSize(CCVector2i(245, 150));
//
//		Window imageWindow = new Window(this, "Selected image");
//		imageWindow.setPosition(CCVector2i(710, 15));
//		imageWindow.setLayout(new GroupLayout());
//
//		// Load all of the images by creating a GLTexture object and saving the pixel data.
////C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//		for (auto icon : icons)
//		{
//			GLTexture texture = new GLTexture(icon.second);
//			GLTexture.handleType data = texture.load(resourcesFolderPath + icon.second + ".png");
//			mImagesData.emplace_back(std::move(texture), std::move(data));
//		}
//
//		// Set the first texture
//		ImageView imageView = new ImageView(imageWindow, mImagesData[0].first.texture());
//		mCurrentImage = 0;
//		// Change the active textures.
////C++ TO JAVA CONVERTER TODO TASK: Only lambda expressions having all locals passed by reference can be converted to Java:
////ORIGINAL LINE: imgPanel->mCallback.add([this, imageView](int i)
//		imgPanel.mCallback.add((int i) ->
//		{
//			imageView.bindImage(mImagesData[i].first.texture());
//			mCurrentImage = i;
//			System.out.print("Selected item ");
//			System.out.print(i);
//			System.out.print('\n');
//		});
//		imageView.setGridThreshold(20);
//		imageView.setPixelInfoThreshold(20);
////C++ TO JAVA CONVERTER TODO TASK: Only lambda expressions having all locals passed by reference can be converted to Java:
////ORIGINAL LINE: imageView->setPixelInfoCallback([this, imageView](const CCVector2i& index)->pair<string, Color>
//		imageView.setPixelInfoCallback((CCVector2i index) ->
//		{
//			auto imageData = mImagesData[mCurrentImage].second;
//			auto textureSize = imageView.imageSize();
//			string stringData;
//			uint16_t channelSum = 0;
//			for (int i = 0; i != 4; ++i)
//			{
//				auto channelData = imageData[4 * index.y() * textureSize.x() + 4 * index.x() + i];
//				channelSum += channelData;
//				stringData += (to_string((int)channelData) + "\n");
//			}
//			float intensity = (float)(255 - (channelSum / 4)) / 255.0f;
//			float colorScale = intensity > 0.5f ? (intensity + 1) / 2 : intensity / 2;
//			Color textColor = new Color(colorScale, 1.0f);
//			return {stringData, textColor};
//		});

		new Label(window, "File dialog", "sans-bold");
		tools = new Widget(window);
		tools.setLayout(new BoxLayout(Orientation.Horizontal, Alignment.Middle, 0, 6));
		b = new Button(tools, "Open");
		b.mCallback.add(() ->
		{
			System.out.print("File dialog result: ");
			System.out.print(file_dialog(
			{
				{"png", "Portable Network Graphics"},
				{"txt", "Text file"}
			},
			false));
			System.out.print("\n");
		});
		b = new Button(tools, "Save");
		b.mCallback.add(() ->
		{
			System.out.print("File dialog result: ");
			System.out.print(file_dialog(
			{
				{"png", "Portable Network Graphics"},
				{"txt", "Text file"}
			},
			true));
			System.out.print("\n");
		});

		new Label(window, "Combo box", "sans-bold");
		new ComboBox(window, {"Combo box item 1", "Combo box item 2", "Combo box item 3"});
		new Label(window, "Check box", "sans-bold");
		CheckBox cb = new CheckBox(window, "Flag 1", (boolean state) ->
		{
			System.out.print("Check box 1 state: ");
			System.out.print(state);
			System.out.print("\n");
		});
		cb.setChecked(true);
		cb = new CheckBox(window, "Flag 2", (boolean state) ->
		{
			System.out.print("Check box 2 state: ");
			System.out.print(state);
			System.out.print("\n");
		});
		new Label(window, "Progress bar", "sans-bold");
		mProgress = new ProgressBar(window);

		new Label(window, "Slider and text box", "sans-bold");

		Widget panel = new Widget(window);
		panel.setLayout(new BoxLayout(Orientation.Horizontal, Alignment.Middle, 0, 20));

		Slider slider = new Slider(panel);
		slider.setValue(0.5f);
		slider.setFixedWidth(80);

		TextBox textBox = new TextBox(panel);
		textBox.setFixedSize(CCVector2i(60, 25));
		textBox.setValue("50");
		textBox.setUnits("%");
//C++ TO JAVA CONVERTER TODO TASK: Only lambda expressions having all locals passed by reference can be converted to Java:
//ORIGINAL LINE: slider->mCallback.add([textBox](float value)
		slider.mCallback.add((float value) ->
		{
			textBox.setValue(String.valueOf((int)(value * 100)));
		});
		slider.setFinalCallback((float value) ->
		{
			System.out.print("Final slider value: ");
			System.out.print((int)(value * 100));
			System.out.print("\n");
		});
		textBox.setFixedSize(CCVector2i(60,25));
		textBox.setFontSize(20);
		textBox.setAlignment(TextBox.Alignment.Right);

		window = new Window(this, "Misc. widgets");
		window.setPosition(CCVector2i(425,15));
		window.setLayout(new GroupLayout());

		TabWidget tabWidget = window.<TabWidget>add();

		Widget layer = tabWidget.createTab("Color Wheel");
		layer.setLayout(new GroupLayout());

		// Use overloaded variadic add to fill the tab widget with Different tabs.
		layer.<Label>add("Color wheel widget", "sans-bold");
		layer.<ColorWheel>add();

		layer = tabWidget.createTab("Function Graph");
		layer.setLayout(new GroupLayout());

		layer.<Label>add("Function graph widget", "sans-bold");

		Graph graph = layer.<Graph>add("Some Function");

		graph.setHeader("E = 2.35e-3");
		graph.setFooter("Iteration 89");
		VectorXf func = graph.values();
		func.resize(100);
		for (int i = 0; i < 100; ++i)
		{
			func[i] = 0.5f * (0.5f * Math.sin(i / 10.0f) + 0.5f * Math.cos(i / 23.0f) + 1);
		}

		// Dummy tab used to represent the last tab button.
		tabWidget.createTab("+");

		// A simple counter.
		int counter = 1;
//C++ TO JAVA CONVERTER TODO TASK: Only lambda expressions having all locals passed by reference can be converted to Java:
//ORIGINAL LINE: tabWidget->mCallback.add([tabWidget, this, counter] (int index) mutable
		tabWidget.mCallback.add( (int index) -> 
		{
			if (index == (tabWidget.tabCount() - 1))
			{
				// When the "+" tab has been clicked, simply add a new tab.
				string tabName = "Dynamic " + to_string(counter);
				Widget layerDyn = tabWidget.createTab(index, tabName);
				layerDyn.setLayout(new GroupLayout());
				layerDyn.<Label>add("Function graph widget", "sans-bold");
				Graph graphDyn = layerDyn.<Graph>add("Dynamic function");

				graphDyn.setHeader("E = 2.35e-3");
				graphDyn.setFooter("Iteration " + to_string(index * counter));
				VectorXf funcDyn = graphDyn.values();
				funcDyn.resize(100);
				for (int i = 0; i < 100; ++i)
				{
					funcDyn[i] = 0.5f * Math.abs((0.5f * Math.sin(i / 10.0f + counter) + 0.5f * Math.cos(i / 23.0f + 1 + counter)));
				}
				++counter;
				// We must invoke perform layout from the screen instance to keep everything in order.
				// This is essential when creating tabs dynamically.
				performLayout();
				// Ensure that the newly added header is visible on screen
				tabWidget.ensureTabVisible(index);

			}
		}
	   );
		tabWidget.setActiveTab(0);

		// A button to go back to the first tab and scroll the window.
		panel = window.<Widget>add();
		panel.<Label>add("Jump to tab: ");
		panel.setLayout(new BoxLayout(Orientation.Horizontal, Alignment.Middle, 0, 6));

		auto ib = panel.<IntBox<Integer>>add();
		ib.setEditable(true);

		b = panel.<Button>add("", ENTYPO_ICON_FORWARD);
		b.setFixedSize(CCVector2i(22, 22));
		ib.setFixedHeight(22);
		b.mCallback.add(() ->
		{
			int value = ib.value();
			if (value >= 0 && value < tabWidget.tabCount())
			{
				tabWidget.setActiveTab(value);
				tabWidget.ensureTabVisible(value);
			}
		});

		window = new Window(this, "Grid of small widgets");
		window.setPosition(CCVector2i(425, 300));
		GridLayout layout = new GridLayout(Orientation.Horizontal, 2, Alignment.Middle, 15, 5);
		layout.setColAlignment({Alignment.Maximum, Alignment.Fill});
		layout.setSpacing(0, 10);
		window.setLayout(layout);

		/* FP widget */
		{			new Label(window, "Floating point :", "sans-bold");
			textBox = new TextBox(window);
			textBox.setEditable(true);
			textBox.setFixedSize(CCVector2i(100, 20));
			textBox.setValue("50");
			textBox.setUnits("GiB");
			textBox.setDefaultValue("0.0");
			textBox.setFontSize(16);
			textBox.setFormat("[-]?[0-9]*\\.?[0-9]+");
		}

		/* Positive integer widget */
		{			new Label(window, "Positive integer :", "sans-bold");
			IntBox<Integer> intBox = new IntBox<Integer>(window);
			intBox.setEditable(true);
			intBox.setFixedSize(CCVector2i(100, 20));
			intBox.setValue(50);
			intBox.setUnits("Mhz");
			intBox.setDefaultValue("0");
			intBox.setFontSize(16);
			intBox.setFormat("[1-9][0-9]*");
			intBox.setSpinnable(true);
			intBox.setMinValue(1);
			intBox.setValueIncrement(2);
		}
		/* Checkbox widget */
		{			new Label(window, "Checkbox :", "sans-bold");

			cb = new CheckBox(window, "Check me");
			cb.setFontSize(16);
			cb.setChecked(true);
		}

		new Label(window, "Combo box :", "sans-bold");
		ComboBox cobo = new ComboBox(window, {"Item 1", "Item 2", "Item 3"});
		cobo.setFontSize(16);
		cobo.setFixedSize(CCVector2i(100,20));

		new Label(window, "Color picker :", "sans-bold");
		ColorPicker cp = new ColorPicker(window, new CCColor(255, 120, 0, 255));
		cp.setFixedSize(new CCVector2i(100, 20));
		cp.finalCallback.add((Color c) ->
		{
			System.out.print("ColorPicker Final Callback: [");
			System.out.print(c.r());
			System.out.print(", ");
			System.out.print(c.g());
			System.out.print(", ");
			System.out.print(c.b());
			System.out.print(", ");
			System.out.print(c.w());
			System.out.print("]");
			System.out.print("\n");
		});
		// setup a fast callback for the color picker widget on a new window
		// for demonstrative purposes
		window = new Window(this, "Color Picker Fast Callback");
		layout = new GridLayout(Orientation.Horizontal, 2, Alignment.Middle, 15, 5);
		layout.setColAlignment({Alignment.Maximum, Alignment.Fill});
		layout.setSpacing(0, 10);
		window.setLayout(layout);
		window.setPosition(CCVector2i(425, 500));
		new Label(window, "Combined: ");
		b = new Button(window, "ColorWheel", ENTYPO_ICON_500PX);
		new Label(window, "Red: ");
		IntBox<Integer> redIntBox = new IntBox<Integer>(window);
		redIntBox.setEditable(false);
		new Label(window, "Green: ");
		IntBox<Integer> greenIntBox = new IntBox<Integer>(window);
		greenIntBox.setEditable(false);
		new Label(window, "Blue: ");
		IntBox<Integer> blueIntBox = new IntBox<Integer>(window);
		blueIntBox.setEditable(false);
		new Label(window, "Alpha: ");
		IntBox<Integer> alphaIntBox = new IntBox<Integer>(window);
		
		cp.mCallback.add((Color c) ->
		{
			b.setBackgroundColor(c);
			b.setTextColor(c.contrastingColor());
			int red = (int)(c.r() * 255.0f);
			redIntBox.setValue(red);
			int green = (int)(c.g() * 255.0f);
			greenIntBox.setValue(green);
			int blue = (int)(c.b() * 255.0f);
			blueIntBox.setValue(blue);
			int alpha = (int)(c.w() * 255.0f);
			alphaIntBox.setValue(alpha);

		});

		performLayout();

//		/* All NanoGUI widgets are initialized at this point. Now
//		   create an OpenGL shader to draw the main window contents.
//
//		   NanoGUI comes with a simple Eigen-based wrapper around OpenGL 3,
//		   which eliminates most of the tedious and error-prone shader and
//		   buffer object management.
//		*/
//
//		mShader.init("a_simple_shader", "#version 330\n" + "uniform mat4 modelViewProj;\n" + "in vec3 position;\n" + "void main() {\n" + "    gl_Position = modelViewProj * vec4(position, 1.0);\n" + "}", "#version 330\n" + "out vec4 color;\n" + "uniform float intensity;\n" + "void main() {\n" + "    color = vec4(vec3(intensity), 1.0);\n" + "}");
//			/* An identifying name */
//			/* Vertex shader */
//			/* Fragment shader */
//
//		MatrixXu indices = new MatrixXu(3, 2); // Draw 2 triangles
//		indices.col(0) << 0, 1, 2;
//		indices.col(1) << 2, 3, 0;
//
//		MatrixXf positions = new MatrixXf(3, 4);
//		positions.col(0) << -1, -1, 0;
//		positions.col(1) << 1, -1, 0;
//		positions.col(2) << 1, 1, 0;
//		positions.col(3) << -1, 1, 0;
//
//		mShader.bind();
//		mShader.uploadIndices(indices);
//		mShader.uploadAttrib("position", positions);
//		mShader.setUniform("intensity", 0.5f);
	}

	public final void close()
	{
		mShader.free();
	}

	public boolean keyboardEvent(int key, int scancode, int action, int modifiers)
	{
		if (Screen.keyboardEvent(key, scancode, action, modifiers))
		{
			return true;
		}
		if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS)
		{
			setVisible(false);
			return true;
		}
		return false;
	}

	public void draw(NVGcontext ctx)
	{
		/* Animate the scrollbar */
		mProgress.setValue(std::fmod((float) glfwGetTime() / 10, 1.0f));

		/* Draw the user interface */
		Screen.draw(ctx);
	}

	public void drawContents()
	{

		/* Draw the window contents using OpenGL */
		mShader.bind();

		Matrix4f mvp = new Matrix4f();
		mvp.setIdentity();
		mvp.topLeftCorner < 3,3>() = Matrix3f(Eigen.AngleAxisf((float) glfwGetTime(), Vector3f.UnitZ())) * 0.25f;

		mvp.row(0) *= (float) mSize.y() / (float) mSize.x();

		mShader.setUniform("modelViewProj", mvp);

		/* Draw 2 triangles starting at index 0 */
		mShader.drawIndexed(GL_TRIANGLES, 0, 2);
	}
	private nanogui.ProgressBar mProgress;
	private nanogui.GLShader mShader = new nanogui.GLShader();

	private vector<pair<GLTexture, GLTexture.handleType>> mImagesData = new vector<pair<GLTexture, GLTexture.handleType>>();
	private int mCurrentImage;
}
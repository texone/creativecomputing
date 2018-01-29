package cc.creativecomputing.uinano;

import cc.creativecomputing.math.CCVector2i;

/**
 * ToolButton toolbutton.h nanogui/toolbutton.h
 *
 * Simple radio+toggle button with an icon.
 */
public class ToolButton extends Button {
	public ToolButton(CCWidget parent, int icon) {
		this(parent, icon, "");
	}

	public ToolButton(CCWidget parent, int icon, String caption) {
		super(parent, caption, icon);
		setFlags(Flags.RadioButton);
		mToggle = true;
		setFixedSize(new CCVector2i(25, 25));
	}
}

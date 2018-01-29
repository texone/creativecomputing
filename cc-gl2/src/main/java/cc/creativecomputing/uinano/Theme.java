package cc.creativecomputing.uinano;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;

/**
 * Storage class for basic theme-related properties.
 */
public class Theme extends Object {
	/* Fonts */
	/**
	 * The standard font face (default: ``"sans"`` from
	 * ``resources/roboto_regular.ttf``).
	 */
	public CCTextureMapFont mFontNormal;
	/**
	 * The bold font face (default: ``"sans-bold"`` from
	 * ``resources/roboto_regular.ttf``).
	 */
	public CCTextureMapFont mFontBold;
	/// The icon font face (default: ``"icons"`` from ``resources/entypo.ttf``).
	public CCTextureMapFont mFontIcons;
	/**
	 * The amount of scaling that is applied to each icon to fit the size of
	 * NanoGUI widgets. The default value is ``0.77f``, setting to e.g. higher
	 * than ``1.0f`` is generally discouraged.
	 */
	public float mIconScale;

	/* Spacing-related parameters */
	/// The font size for all widgets other than buttons and textboxes (default:
	/// `` 16``).
	public int mStandardFontSize;
	/// The font size for buttons (default: ``20``).
	public int mButtonFontSize;
	/// The font size for text boxes (default: ``20``).
	public int mTextBoxFontSize;
	/// Rounding radius for Window widget corners (default: ``2``).
	public int mWindowCornerRadius;
	/// Default size of Window widget titles (default: ``30``).
	public int mWindowHeaderHeight;
	/// Size of drop shadow rendered behind the Window widgets (default:
	/// ``10``).
	public int mWindowDropShadowSize;
	/// Rounding radius for Button (and derived types) widgets (default: ``2``).
	public int mButtonCornerRadius;
	/// The border width for TabHeader widgets (default: ``0.75f``).
	public float mTabBorderWidth;
	/// The inner margin on a TabHeader widget (default: ``5``).
	public int mTabInnerMargin;
	/// The minimum size for buttons on a TabHeader widget (default: ``20``).
	public int mTabMinButtonWidth;
	/// The maximum size for buttons on a TabHeader widget (default: ``160``).
	public int mTabMaxButtonWidth;
	/// Used to help specify what lies "in bound" for a TabHeader widget
	/// (default: ``20``).
	public int mTabControlWidth;
	/// The amount of horizontal padding for a TabHeader widget (default:
	/// ``10``).
	public int mTabButtonHorizontalPadding;
	/// The amount of vertical padding for a TabHeader widget (default: ``2``).
	public int mTabButtonVerticalPadding;

	/* Generic colors */
	/**
	 * The color of the drop shadow drawn behind widgets (default:
	 * intensity=``0``, alpha=``128``; see \ref
	 * nanogui::CCColor::CCColor(int,int)).
	 */
	public CCColor mDropShadow = new CCColor();
	/**
	 * The transparency color (default: intensity=``0``, alpha=``0``; see \ref
	 * nanogui::CCColor::CCColor(int,int)).
	 */
	public CCColor mTransparent = new CCColor();
	/**
	 * The dark border color (default: intensity=``29``, alpha=``255``; see \ref
	 * nanogui::CCColor::CCColor(int,int)).
	 */
	public CCColor mBorderDark = new CCColor();
	/**
	 * The light border color (default: intensity=``92``, alpha=``255``; see
	 * \ref nanogui::CCColor::CCColor(int,int)).
	 */
	public CCColor mBorderLight = new CCColor();
	/**
	 * The medium border color (default: intensity=``35``, alpha=``255``; see
	 * \ref nanogui::CCColor::CCColor(int,int)).
	 */
	public CCColor mBorderMedium = new CCColor();
	/**
	 * The text color (default: intensity=``255``, alpha=``160``; see \ref
	 * nanogui::CCColor::CCColor(int,int)).
	 */
	public CCColor mTextColor = new CCColor();
	/**
	 * The disable dtext color (default: intensity=``255``, alpha=``80``; see
	 * \ref nanogui::CCColor::CCColor(int,int)).
	 */
	public CCColor mDisabledTextColor = new CCColor();
	/**
	 * The text shadow color (default: intensity=``0``, alpha=``160``; see \ref
	 * nanogui::CCColor::CCColor(int,int)).
	 */
	public CCColor mTextColorShadow = new CCColor();
	/// The icon color (default: \ref nanogui::Theme::mTextCCColor).
	public CCColor mIconColor = new CCColor();

	/* Button colors */
	/**
	 * The top gradient color for buttons in focus (default: intensity=``64``,
	 * alpha=``255``; see \ref nanogui::CCColor::CCColor(int,int)).
	 */
	public CCColor mButtonGradientTopFocused = new CCColor();
	/**
	 * The bottom gradient color for buttons in focus (default:
	 * intensity=``48``, alpha=``255``; see \ref
	 * nanogui::CCColor::CCColor(int,int)).
	 */
	public CCColor mButtonGradientBotFocused = new CCColor();
	/**
	 * The top gradient color for buttons not in focus (default:
	 * intensity=``74``, alpha=``255``; see \ref
	 * nanogui::CCColor::CCColor(int,int)).
	 */
	public CCColor mButtonGradientTopUnfocused = new CCColor();
	/**
	 * The bottom gradient color for buttons not in focus (default:
	 * intensity=``58``, alpha=``255``; see \ref
	 * nanogui::CCColor::CCColor(int,int)).
	 */
	public CCColor mButtonGradientBotUnfocused = new CCColor();
	/**
	 * The top gradient color for buttons currently pushed (default:
	 * intensity=``41``, alpha=``255``; see \ref
	 * nanogui::CCColor::CCColor(int,int)).
	 */
	public CCColor mButtonGradientTopPushed = new CCColor();
	/**
	 * The bottom gradient color for buttons currently pushed (default:
	 * intensity=``29``, alpha=``255``; see \ref
	 * nanogui::CCColor::CCColor(int,int)).
	 */
	public CCColor mButtonGradientBotPushed = new CCColor();

	/* Window colors */
	/**
	 * The fill color for a Window that is not in focus (default:
	 * intensity=``43``, alpha=``230``; see \ref
	 * nanogui::CCColor::CCColor(int,int)).
	 */
	public CCColor mWindowFillUnfocused = new CCColor();
	/**
	 * The fill color for a Window that is in focus (default: intensity=``45``,
	 * alpha=``230``; see \ref nanogui::CCColor::CCColor(int,int)).
	 */
	public CCColor mWindowFillFocused = new CCColor();
	/**
	 * The title color for a Window that is not in focus (default:
	 * intensity=``220``, alpha=``160``; see \ref
	 * nanogui::CCColor::CCColor(int,int)).
	 */
	public CCColor mWindowTitleUnfocused = new CCColor();
	/**
	 * The title color for a Window that is in focus (default:
	 * intensity=``255``, alpha=``190``; see \ref
	 * nanogui::CCColor::CCColor(int,int)).
	 */
	public CCColor mWindowTitleFocused = new CCColor();

	/**
	 * The top gradient color for Window headings (default: \ref
	 * nanogui::Theme::mButtonGradientTopUnfocused).
	 */
	public CCColor mWindowHeaderGradientTop = new CCColor();
	/**
	 * The bottom gradient color for Window headings (default: \ref
	 * nanogui::Theme::mButtonGradientBotUnfocused).
	 */
	public CCColor mWindowHeaderGradientBot = new CCColor();
	/// The Window header top separation color (default: \ref
	/// nanogui::Theme::mBorderLight).
	public CCColor mWindowHeaderSepTop = new CCColor();
	/// The Window header bottom separation color (default: \ref
	/// nanogui::Theme::mBorderDark).
	public CCColor mWindowHeaderSepBot = new CCColor();

	/**
	 * The popup window color (default: intensity=``50``, alpha=``255``; see
	 * \ref nanogui::CCColor::CCColor(int,int))).
	 */
	public CCColor mWindowPopup = new CCColor();
	/**
	 * The transparent popup window color (default: intensity=``50``,
	 * alpha=``0``; see \ref nanogui::CCColor::CCColor(int,int))).
	 */
	public CCColor mWindowPopupTransparent = new CCColor();

	/// Icon to use for CheckBox widgets (default: ``ENTYPO_ICON_CHECK``).
	public TypoIcon mCheckBoxIcon;
	/// Icon to use for informational MessageDialog widgets (default:
	/// ``ENTYPO_ICON_INFO_WITH_CIRCLE``).
	public TypoIcon mMessageInformationIcon;
	/// Icon to use for interrogative MessageDialog widgets (default:
	/// ``ENTYPO_ICON_HELP_WITH_CIRCLE``).
	public TypoIcon mMessageQuestionIcon;
	/// Icon to use for warning MessageDialog widgets (default:
	/// ``ENTYPO_ICON_WARNING``).
	public TypoIcon mMessageWarningIcon;
	/// Icon to use on MessageDialog alt button (default:
	/// ``ENTYPO_ICON_CIRCLE_WITH_CROSS``).
	public TypoIcon mMessageAltButtonIcon;
	/// Icon to use on MessageDialog primary button (default:
	/// ``ENTYPO_ICON_CHECK``).
	public TypoIcon mMessagePrimaryButtonIcon;
	/// Icon to use for PopupButton widgets opening to the right (default:
	/// ``ENTYPO_ICON_CHEVRON_RIGHT``).
	public TypoIcon mPopupChevronRightIcon;
	/// Icon to use for PopupButton widgets opening to the left (default:
	/// ``ENTYPO_ICON_CHEVRON_LEFT``).
	public TypoIcon mPopupChevronLeftIcon;
	/// Icon to indicate hidden tabs to the left on a TabHeader (default:
	/// ``ENTYPO_ICON_ARROW_BOLD_LEFT``).
	public TypoIcon mTabHeaderLeftIcon;
	/// Icon to indicate hidden tabs to the right on a TabHeader (default:
	/// ``ENTYPO_ICON_ARROW_BOLD_RIGHT``).
	public TypoIcon mTabHeaderRightIcon;
	/// Icon to use when a TextBox has an up toggle (e.g. IntBox) (default:
	/// ``ENTYPO_ICON_CHEVRON_UP``).
	public TypoIcon mTextBoxUpIcon;
	/// Icon to use when a TextBox has a down toggle (e.g. IntBox) (default:
	/// ``ENTYPO_ICON_CHEVRON_DOWN``).
	public TypoIcon mTextBoxDownIcon;

	public Theme() {
		mStandardFontSize = 16;
		mButtonFontSize = 20;
		mTextBoxFontSize = 20;
		mIconScale = 0.77f;

		mWindowCornerRadius = 2;
		mWindowHeaderHeight = 30;
		mWindowDropShadowSize = 10;
		mButtonCornerRadius = 2;
		mTabBorderWidth = 0.75f;
		mTabInnerMargin = 5;
		mTabMinButtonWidth = 20;
		mTabMaxButtonWidth = 160;
		mTabControlWidth = 20;
		mTabButtonHorizontalPadding = 10;
		mTabButtonVerticalPadding = 2;

		mDropShadow = new CCColor(0, 128);
		mTransparent = new CCColor(0, 0);
		mBorderDark = new CCColor(29, 255);
		mBorderLight = new CCColor(92, 255);
		mBorderMedium = new CCColor(35, 255);
		mTextColor = new CCColor(255, 160);
		mDisabledTextColor = new CCColor(255, 80);
		mTextColorShadow = new CCColor(0, 160);
		mIconColor = mTextColor;

		mButtonGradientTopFocused = new CCColor(64, 255);
		mButtonGradientBotFocused = new CCColor(48, 255);
		mButtonGradientTopUnfocused = new CCColor(74, 255);
		mButtonGradientBotUnfocused = new CCColor(58, 255);
		mButtonGradientTopPushed = new CCColor(41, 255);
		mButtonGradientBotPushed = new CCColor(29, 255);

		/* Window-related */
		mWindowFillUnfocused = new CCColor(43, 230);
		mWindowFillFocused = new CCColor(45, 230);
		mWindowTitleUnfocused = new CCColor(220, 160);
		mWindowTitleFocused = new CCColor(255, 190);

		mWindowHeaderGradientTop = mButtonGradientTopUnfocused;
		mWindowHeaderGradientBot = mButtonGradientBotUnfocused;
		mWindowHeaderSepTop = mBorderLight;
		mWindowHeaderSepBot = mBorderDark;

		mWindowPopup = new CCColor(50, 255);
		mWindowPopupTransparent = new CCColor(50, 0);

		mCheckBoxIcon = TypoIcon.ICON_CHECK;
		mMessageInformationIcon = TypoIcon.ICON_INFO_WITH_CIRCLE;
		mMessageQuestionIcon = TypoIcon.ICON_HELP_WITH_CIRCLE;
		mMessageWarningIcon = TypoIcon.ICON_WARNING;
		mMessageAltButtonIcon = TypoIcon.ICON_CIRCLE_WITH_CROSS;
		mMessagePrimaryButtonIcon = TypoIcon.ICON_CHECK;
		mPopupChevronRightIcon = TypoIcon.ICON_CHEVRON_RIGHT;
		mPopupChevronLeftIcon = TypoIcon.ICON_CHEVRON_LEFT;
		mTabHeaderLeftIcon = TypoIcon.ICON_ARROW_BOLD_LEFT;
		mTabHeaderRightIcon = TypoIcon.ICON_ARROW_BOLD_RIGHT;
		mTextBoxUpIcon = TypoIcon.ICON_CHEVRON_UP;
		mTextBoxDownIcon = TypoIcon.ICON_CHEVRON_DOWN;

		mFontNormal = new CCTextureMapFont(CCCharSet.REDUCED,CCNIOUtil.dataPath("fonts/Roboto/Roboto-Regular.ttf"), 24, 2, 2);
		mFontBold = new CCTextureMapFont(CCCharSet.REDUCED, CCNIOUtil.dataPath("fonts/Roboto/Roboto-Bold.ttf"),
				24, 2, 2);
		mFontIcons = new CCTextureMapFont(CCCharSet.REDUCED, CCNIOUtil.dataPath("fonts/entypo.ttf"), 24, 2, 2);

	}

	/// Default destructor does nothing; allows for inheritance.
	public void close() {
	}

}
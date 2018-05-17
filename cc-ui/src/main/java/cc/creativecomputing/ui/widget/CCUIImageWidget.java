package cc.creativecomputing.ui.widget;

import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.ui.draw.CCUIStrokeDrawable;
import cc.creativecomputing.ui.draw.CCUITextureDrawable;

public class CCUIImageWidget extends CCUIWidget{

	public static CCUIWidgetStyle createDefaultStyle(CCTexture2D theTexture){
		CCUIWidgetStyle myStyle = new CCUIWidgetStyle();
		myStyle.background(new CCUITextureDrawable(theTexture));
		myStyle.foreground(new CCUIStrokeDrawable(CCColor.WHITE, 2, 0));
		return myStyle;
	}
	
	
	public CCUIImageWidget(CCTexture2D theTexture) {
		super(createDefaultStyle(theTexture), theTexture.width(), theTexture.height());
		_myMinWidth = theTexture.width();
		_myMinHeight = theTexture.height();
	}

}

package cc.creativecomputing.gl.demo.font;

import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_SRGB;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCChar;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCFontImage;
import cc.creativecomputing.graphics.font.CCTextField;
import cc.creativecomputing.graphics.font.CCTextureMapChar;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.io.CCNIOUtil;

public class CCFFontOversampleDemo extends CCGLApp{

    private static final int[] scale = {
    	24,
    	14
    };

    private CCFontImage _myFontBitmap;
    private CCTextureMapFont[] _myFonts = new CCTextureMapFont[6];
    
    private int font = 3;

    private boolean black_on_white;
    private boolean integer_align;
    private boolean translating;
    private boolean rotating;

    private boolean supportsSRGB;
    private boolean srgb;

    private float rotate_t, translate_t;

    private boolean show_tex;
    
    private List<CCTextField> _myTextFields = new ArrayList<>();
	
	@Override
	public void setup(CCGraphics g, CCGLTimer theTimer) {
		CCFont<CCChar> myFont = new CCFont<CCChar>(null,CCNIOUtil.dataPath("fonts/Roboto/Roboto-Bold.ttf"));
        _myFontBitmap = new CCFontImage(1024, 1024);
    	
        _myFontBitmap.packBegin();
        for (int i = 0; i < 2; i++) {
        	_myFonts[i * 3] = new CCTextureMapFont(myFont, _myFontBitmap, scale[i], 1, 1);
        	_myFonts[i * 3 + 1] = new CCTextureMapFont(myFont, _myFontBitmap, scale[i], 2, 2);
        	_myFonts[i * 3 + 2] = new CCTextureMapFont(myFont, _myFontBitmap, scale[i], 3, 1);
        }
       
        _myFontBitmap.packEnd();
		
		_myMainWindow.keyReleaseEvents.add((c) -> {
			switch(c.key){
			case KEY_ESCAPE:
				_myMainWindow.shouldClose(true);
                break;
            case KEY_O:
                font = (font + 1) % 3 + (font / 3) * 3;
                break;
            case KEY_S:
                font = (font + 3) % 6;
                break;
            case KEY_T:
                translating = !translating;
                translate_t = 0.0f;
                break;
            case KEY_R:
                rotating = !rotating;
                rotate_t = 0.0f;
                break;
            case KEY_P:
                integer_align = !integer_align;
                break;
            case KEY_G:
                if (!supportsSRGB) {
                    break;
                }

                srgb = !srgb;
                if (srgb) {
                    glEnable(GL_FRAMEBUFFER_SRGB);
                } else {
                    glDisable(GL_FRAMEBUFFER_SRGB);
                }
                break;
            case KEY_V:
                show_tex = !show_tex;
                break;
            case KEY_B:
                black_on_white = !black_on_white;
                break;
             default:
			}
		});
		
		// Detect sRGB support
        GLCapabilities caps = GL.getCapabilities();
        supportsSRGB = caps.OpenGL30 || caps.GL_ARB_framebuffer_sRGB || caps.GL_EXT_framebuffer_sRGB;
	}
	
	@Override
	public void update(CCGLTimer theTimer) {
	}
    
	private void text(CCGraphics g, float x, float y, CCTextureMapFont font, String text) {
		
		font.beginText(g);
        for (char myChar:text.toCharArray()) {
        	CCTextureMapChar myCharObject = font.fontChar(myChar);
        	x += myCharObject.drawVertices(g, x, y, 0, 1);
        }
        font.endText(g);
    }
	
	@Override
	public void display(CCGraphics g) {
		
        if (black_on_white) {
            g.clearColor(1.0, 1.0, 1.0, 0.0);
        } else {
        	g.clearColor(0.0, 0.0, 0.0, 0.0);
        }
        g.clear();
		
		CCTextureMapFont myFont = _myFonts[font % 3];

        float x = 200;

        g.blend();

        if (black_on_white) {
            g.color(0.0, 0.0, 0.0);
        } else {
        	g.color(1.0, 1.0, 1.0);
        }

        
        text(g,80, 500, myFont, "Controls:");
        text(g,100, 470, myFont, "S: toggle font size");
        text(g,100, 445, myFont, "O: toggle oversampling");
        text(g,100, 420, myFont, "T: toggle translation");
        text(g,100, 395, myFont, "R: toggle rotation");
        text(g,100, 370, myFont, "P: toggle pixel-snap (only non-oversampled)");
        if (supportsSRGB) {
            text(g,100, 345, myFont, "G: toggle srgb gamma-correction");
        }
        if (black_on_white) {
            text(g,100, 320, myFont, "B: toggle to white-on-black");
        } else {
            text(g,100, 320, myFont, "B: toggle to black-on-white");
        }
        text(g,100, 295, myFont, "V: view font texture");

        text(g,80, 270, myFont, "Current font:");

        if (!show_tex) {
            if (font < 3) {
                text(g,100, 245, myFont, "Font height: 24 pixels");
            } else {
                text(g,100, 245, myFont, "Font height: 14 pixels");
            }
        }

        if (font % 3 == 1) {
            text(g,100, 220, myFont, "2x2 oversampled text at 1:1");
        } else if (font % 3 == 2) {
            text(g,100, 220, myFont, "3x1 oversampled text at 1:1");
        } else if (integer_align) {
            text(g,100, 220, myFont, "1:1 text, one texel = one pixel, snapped to integer coordinates");
        } else {
            text(g,100, 220, myFont, "1:1 text, one texel = one pixel");
        }

        if (show_tex) {
        	g.image(_myFontBitmap.texture(), 200,195);
        } else {

            if (translating) {
                x += translate_t * 8 % 30;
            }

            if (rotating) {
            	g.translate(100, 150, 0);
            	g.rotate(rotate_t * 2);
                g.translate(-100, -150, 0);
            }
    		myFont = _myFonts[font];
            text(g,x, 170, myFont, "This is a test");
            text(g,x, 145, myFont, "Now is the time for all good men to come to the aid of their country.");
            text(g,x, 120, myFont, "The quick brown fox jumps over the lazy dog.");
            text(g,x, 95, myFont, "0123456789");
        }
//		g.image(_myFontTexture, 0,0);
	}
	
	public static void main(String[] args) {
		CCFFontOversampleDemo myDemo = new CCFFontOversampleDemo();
		myDemo.width = 1024;
		myDemo.height = 768;
		myDemo.run();
	}
}

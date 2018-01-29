/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package cc.creativecomputing.gl.demo.font.lwjgl;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_B;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_G;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_O;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_T;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_V;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_ALPHA;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_SRGB;
import static org.lwjgl.stb.STBTruetype.stbtt_GetPackedQuad;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memFree;

import java.nio.FloatBuffer;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.Callback;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCFontImage;
import cc.creativecomputing.io.CCNIOUtil;

/**
 * STB Truetype oversampling demo.
 *
 * <p>This is a Java port of <a href="https://github.com/nothings/stb/blob/master/tests/oversample/main.c">https://github
 * .com/nothings/stb/blob/master/tests/oversample/main.c</a>.</p>
 */
public final class TruetypeOversample {

    private static final int BITMAP_W = 1024;
    private static final int BITMAP_H = 1024;

    private static final float[] scale = {
        24.0f,
        14.0f
    };

    private static final int[] sf = {
        0, 1, 2,
        0, 1, 2
    };

    // ----

    private final STBTTAlignedQuad q  = STBTTAlignedQuad.malloc();
    private final FloatBuffer      xb = memAllocFloat(1);
    private final FloatBuffer      yb = memAllocFloat(1);

    private long window;

    private Callback debugProc;

    // ----

    private int ww = 1024;
    private int wh = 768;

    private int fbw = ww;
    private int fbh = wh;

    private int font_tex;

    private STBTTPackedchar.Buffer[] chardata;

    private int font = 3;

    private boolean black_on_white;
    private boolean integer_align;
    private boolean translating;
    private boolean rotating;

    private boolean supportsSRGB;
    private boolean srgb;

    private float rotate_t, translate_t;

    private boolean show_tex;

    private TruetypeOversample() {
    }

    public static void main(String[] args) {
        new TruetypeOversample().run("STB Truetype Oversample Demo");
    }

    private void load_fonts() {
        font_tex = glGenTextures();
        chardata = new STBTTPackedchar.Buffer[6];
        	
        CCFont myFont = new CCFont(null,CCNIOUtil.dataPath("Roboto-Bold.ttf"));
        CCFontImage myFontBitmap = new CCFontImage(BITMAP_W, BITMAP_H);
            
        myFontBitmap.packBegin();
        for (int i = 0; i < 2; i++) {
        	myFontBitmap.oversampling(1, 1);
        	chardata[i * 3] = myFontBitmap.packFont(myFont, scale[i]);

        	myFontBitmap.oversampling(2, 2);
        	chardata[i * 3 + 1] = myFontBitmap.packFont(myFont, scale[i]);

        	myFontBitmap.oversampling(3, 1);
        	chardata[i * 3 + 2] = myFontBitmap.packFont(myFont, scale[i]);
        }
        for(int i = 0; i < 6;i++){
        	CCLog.info(i);
//        	chardata[i].clear();
        }
//        chardata.clear();|
        myFontBitmap.packEnd();

        glBindTexture(GL_TEXTURE_2D, font_tex);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, BITMAP_W, BITMAP_H, 0, GL_ALPHA, GL_UNSIGNED_BYTE, myFontBitmap.data());
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            
       
    }

    private void draw_init() {
        glDisable(GL_CULL_FACE);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_LIGHTING);
        glDisable(GL_DEPTH_TEST);

        glViewport(0, 0, fbw, fbh);
        if (black_on_white) {
            glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        } else {
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        }
        glClear(GL_COLOR_BUFFER_BIT);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0.0, ww, wh, 0.0, -1.0, 1.0);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
    }

    private static void drawBoxTC(float x0, float y0, float x1, float y1, float s0, float t0, float s1, float t1) {
        glTexCoord2f(s0, t0);
        glVertex2f(x0, y0);
        glTexCoord2f(s1, t0);
        glVertex2f(x1, y0);
        glTexCoord2f(s1, t1);
        glVertex2f(x1, y1);
        glTexCoord2f(s0, t1);
        glVertex2f(x0, y1);
    }

    private void print(float x, float y, int font, String text) {
        xb.put(0, 0);
        yb.put(0, 0);

        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, font_tex);

        glBegin(GL_QUADS);
        for (int i = 0; i < text.length(); i++) {
            stbtt_GetPackedQuad(chardata[font], BITMAP_W, BITMAP_H, text.charAt(i), xb, yb, q, font == 0 && integer_align);
            drawBoxTC(
                q.x0() + x, q.y0() + y, q.x1() + x, q.y1() + y,
                q.s0(), q.t0(), q.s1(), q.t1()
            );
            CCLog.info(i, text.charAt(i));
            CCLog.info(q.x0(), q.y0(), q.x1(), q.y1());
            CCLog.info(q.s0(), q.t0(), q.s1(), q.t1());
        }
        glEnd();
    }

    private void draw_world() {
        int sfont = sf[font];

        float x = 20;

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        if (black_on_white) {
            glColor3f(0.0f, 0.0f, 0.0f);
        } else {
            glColor3f(1.0f, 1.0f, 1.0f);
        }

        print(80, 30, sfont, "Controls:");
        print(100, 60, sfont, "S: toggle font size");
        print(100, 85, sfont, "O: toggle oversampling");
        print(100, 110, sfont, "T: toggle translation");
        print(100, 135, sfont, "R: toggle rotation");
        print(100, 160, sfont, "P: toggle pixel-snap (only non-oversampled)");
        if (supportsSRGB) {
            print(100, 185, sfont, "G: toggle srgb gamma-correction");
        }
        if (black_on_white) {
            print(100, 210, sfont, "B: toggle to white-on-black");
        } else {
            print(100, 210, sfont, "B: toggle to black-on-white");
        }
        print(100, 235, sfont, "V: view font texture");

        print(80, 300, sfont, "Current font:");

        if (!show_tex) {
            if (font < 3) {
                print(100, 350, sfont, "Font height: 24 pixels");
            } else {
                print(100, 350, sfont, "Font height: 14 pixels");
            }
        }

        if (font % 3 == 1) {
            print(100, 325, sfont, "2x2 oversampled text at 1:1");
        } else if (font % 3 == 2) {
            print(100, 325, sfont, "3x1 oversampled text at 1:1");
        } else if (integer_align) {
            print(100, 325, sfont, "1:1 text, one texel = one pixel, snapped to integer coordinates");
        } else {
            print(100, 325, sfont, "1:1 text, one texel = one pixel");
        }

        if (show_tex) {
            glBegin(GL_QUADS);
            drawBoxTC(200, 400, 200 + BITMAP_W, 300 + BITMAP_H, 0, 0, 1, 1);
            glEnd();
        } else {
            glMatrixMode(GL_MODELVIEW);
            glTranslatef(200, 350, 0);

            if (translating) {
                x += translate_t * 8 % 30;
            }

            if (rotating) {
                glTranslatef(100, 150, 0);
                glRotatef(rotate_t * 2, 0, 0, 1);
                glTranslatef(-100, -150, 0);
            }
            print(x, 100, font, "This is a test");
            print(x, 130, font, "Now is the time for all good men to come to the aid of their country.");
            print(x, 160, font, "The quick brown fox jumps over the lazy dog.");
            print(x, 190, font, "0123456789");
        }
    }

    private void draw() {
        draw_init();
        draw_world();
        glfwSwapBuffers(window);
    }

    private void loopmode(float dt) {
        if (dt > 0.25f) {
            dt = 0.25f;
        }
        if (dt < 0.01f) {
            dt = 0.01f;
        }

        rotate_t += dt;
        translate_t += dt;

        draw();
    }

    private void windowSizeChanged(long window, int width, int height) {
        this.ww = width;
        this.wh = height;
    }

    private void framebufferSizeChanged(long window, int width, int height) {
        this.fbw = width;
        this.fbh = height;
    }

    private void createWindow(String title) {
        GLFWErrorCallback.createPrint().set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        this.window = glfwCreateWindow(ww, wh, title, NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwSetWindowSizeCallback(window, this::windowSizeChanged);
        glfwSetFramebufferSizeCallback(window, this::framebufferSizeChanged);

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (action == GLFW_RELEASE) {
                return;
            }

            switch (key) {
                case GLFW_KEY_ESCAPE:
                    glfwSetWindowShouldClose(window, true);
                    break;
                case GLFW_KEY_O:
                    font = (font + 1) % 3 + (font / 3) * 3;
                    break;
                case GLFW_KEY_S:
                    font = (font + 3) % 6;
                    break;
                case GLFW_KEY_T:
                    translating = !translating;
                    translate_t = 0.0f;
                    break;
                case GLFW_KEY_R:
                    rotating = !rotating;
                    rotate_t = 0.0f;
                    break;
                case GLFW_KEY_P:
                    integer_align = !integer_align;
                    break;
                case GLFW_KEY_G:
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
                case GLFW_KEY_V:
                    show_tex = !show_tex;
                    break;
                case GLFW_KEY_B:
                    black_on_white = !black_on_white;
                    break;
            }
        });

        // Center window
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        glfwSetWindowPos(
            window,
            (vidmode.width() - ww) / 2,
            (vidmode.height() - wh) / 2
        );

        // Create context
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        debugProc = GLUtil.setupDebugMessageCallback();

        glfwSwapInterval(1);
        glfwShowWindow(window);

        GLFWUtil.glfwInvoke(window, this::windowSizeChanged, this::framebufferSizeChanged);

        // Detect sRGB support
        GLCapabilities caps = GL.getCapabilities();
        supportsSRGB = caps.OpenGL30 || caps.GL_ARB_framebuffer_sRGB || caps.GL_EXT_framebuffer_sRGB;
    }

    private void run(String title) {
        try {
            createWindow(title);
            load_fonts();

            long time = System.nanoTime();
            while (!glfwWindowShouldClose(window)) {
                glfwPollEvents();

                long  t  = System.nanoTime();
                float dt = (float)((t - time) / 1000000000.0);
                time = t;

                loopmode(dt);
            }
        } finally {
            try {
                destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void destroy() {
    	for(int i = 0; i < chardata.length;i++){
            chardata[i].free();
    	}

        if (debugProc != null) {
            debugProc.free();
        }

        glfwFreeCallbacks(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();

        memFree(yb);
        memFree(xb);

        q.free();
    }
}
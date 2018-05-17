/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package cc.creativecomputing.yoga;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import org.lwjgl.util.yoga.*;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.yoga.CCYogaNode.CCYogaDirection;
import cc.creativecomputing.yoga.CCYogaNode.CCYogaFlexDirection;

import java.nio.*;
import java.util.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBEasyFont.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.util.yoga.Yoga.*;

/** Yoga implementation of the Holy Grail layout. Ported from: <a href="https://codepen.io/Praseetha-KR/pen/rJqEL">Holy Grail Layout with Flexbox</a>. */
public final class HolyGrail {

    private final Callback debugProc;

    private final long window;

    private final ByteBuffer charBuffer;

    private final CCYogaNode root;

    private final CCYogaNode header;
    private final CCYogaNode footer;

    private final CCYogaNode container;

    private final CCYogaNode navbar;
    private final CCYogaNode article;
    private final CCYogaNode sidebar;

    private int width  = 1280;
    private int height = 720;

    private HolyGrail() {
        // ----------------------
        //          GLFW
        // ----------------------
        GLFWErrorCallback.createPrint().set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize glfw");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
        if (Platform.get() == Platform.MACOSX) {
            glfwWindowHint(GLFW_COCOA_RETINA_FRAMEBUFFER, GLFW_FALSE);
        }

        long window = glfwCreateWindow(width, height, "Holy Grail layout with Yoga", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Center window
        GLFWVidMode vidmode = Objects.requireNonNull(glfwGetVideoMode(glfwGetPrimaryMonitor()));
        glfwSetWindowPos(
            window,
            (vidmode.width() - width) / 2,
            (vidmode.height() - height) / 2
        );

        glfwSetKeyCallback(window, this::keyTriggered);

        glfwSetWindowSizeCallback(window, this::windowSizeChanged);

        glfwSetWindowRefreshCallback(window, windowHnd -> {
            renderLoop();
            glfwSwapBuffers(windowHnd);
        });

        // ----------------------
        //          OpenGL
        // ----------------------

        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        debugProc = GLUtil.setupDebugMessageCallback();

        glfwSwapInterval(1);

        charBuffer = BufferUtils.createByteBuffer(256 * 270);

        glEnableClientState(GL_VERTEX_ARRAY);
        glVertexPointer(2, GL_FLOAT, 16, charBuffer);

        // ----------------------
        //          Yoga
        // ----------------------

        root = new CCYogaNode();
        root.flexDirection(CCYogaFlexDirection.COLUMN);

        header = new CCYogaNode();
        container = new CCYogaNode();
        footer = new CCYogaNode();

        header.height(100.0);
        container.flex( 1.0f);
        footer.height(40.0f);

        root.insertChild(header, 0);
        root.insertChild(container, 1);
        root.insertChild(footer, 2);

        navbar = new CCYogaNode();
        article = new CCYogaNode();
        sidebar = new CCYogaNode();

        navbar.flex(1.0f);
        article.flex(3.0f);
        sidebar.flex(1.0f);

        container.insertChild(navbar, 0);
        container.insertChild(article, 1);
        container.insertChild(sidebar, 2);

        // Show window
        windowSizeChanged(window, width, height);
        glfwShowWindow(window);
        this.window = window;
    }

    private void keyTriggered(long window, int key, int scancode, int action, int mods) {
        if (action != GLFW_RELEASE) {
            return;
        }

        switch (key) {
            case GLFW_KEY_ESCAPE:
                glfwSetWindowShouldClose(window, true);
                break;
            case GLFW_KEY_D:
            	root.direction(root.direction() == CCYogaDirection.RTL ? CCYogaDirection.LTR : CCYogaDirection.RTL);
                root.calculateLayout(width, height, CCYogaFlexDirection.COLUMN);
                break;
        }
    }

    private void windowSizeChanged(long window, int width, int height) {
        this.width = width;
        this.height = height;

        glViewport(0, 0, width, height);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0.0, width, height, 0.0, -1.0, 1.0);
        glMatrixMode(GL_MODELVIEW);

        // Toggle mobile/desktop layout when the threshold is passed
        if (width <= 480) {
            if (container.flexDirection() == CCYogaFlexDirection.ROW) {
                toggleLayout(article, navbar, CCYogaFlexDirection.COLUMN);
            }
        } else if (container.flexDirection() == CCYogaFlexDirection.COLUMN) {
            toggleLayout(navbar, article, CCYogaFlexDirection.ROW);
        }

        root.calculateLayout(width, height, CCYogaFlexDirection.COLUMN);
    }

    private void toggleLayout(CCYogaNode first, CCYogaNode second, CCYogaFlexDirection direction) {
    	container.removeChild(first);
    	container.removeChild(second);
    	container.insertChild(first, 0);
    	container.insertChild(second, 1);
    	container.flexDirection(direction);
    }

    private void run() {
        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();

            renderLoop();

            glfwSwapBuffers(window);
        }

        destroy();
    }

    private void renderLoop() {
        renderNode(header, 0xFFFFFFFF, "Header");
        {
            glPushMatrix();
            glTranslatef((float)container.left(), (float)container.top(), 0.0f);
            renderNode(navbar, 0xBCD39BFF, "Navbar contents\n( Box 2 )");
            renderNode(article, 0xCE9B64FF, "Article contents\n( Box 1 )");
            renderNode(sidebar, 0x62626DFF, "Sidebar contents\n( Box 3 )");
            glPopMatrix();
        }
        renderNode(footer, 0xFFFFFFFF, "Footer");
    }

    private void renderNode(CCYogaNode node, int color, String title) {
        glColor3f(
            ((color >> 24) & 255) / 255.0f,
            ((color >> 16) & 255) / 255.0f,
            ((color >> 8) & 255) / 255.0f
        );

		// Public API with 4x JNI call overhead
        float l = (float)node.left();
		float t = (float)node.top();
		float w = (float)node.width();
		float h = (float)node.height();
		

        // Internal API without overhead (plain memory accesses, assuming allocations are eliminated via EA)


        glBegin(GL_QUADS);
        glVertex2f(l, t);
        glVertex2f(l, t + h);
        glVertex2f(l + w, t + h);
        glVertex2f(l + w, t);
        glEnd();

        glColor3f(0.0f, 0.0f, 0.0f);

        glPushMatrix();
        glTranslatef(l + 8, t + 8, 0.0f);
        glScalef(2.0f, 2.0f, 1.0f);
        glDrawArrays(GL_QUADS, 0, stb_easy_font_print(0, 0, title, null, charBuffer) * 4);
        glPopMatrix();
    }

    private void destroy() {

        if (debugProc != null) {
            debugProc.free();
        }

        if (window != NULL) {
            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);
        }

        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    public static void main(String[] args) {
        new HolyGrail().run();
    }

}
package cc.creativecomputing.uinano;
import String;
import nanogui.*;
import javax.swing.*;
import java.util.*;

/*
    src/example4.cpp -- C++ version of an example application that shows
    how to use the OpenGL widget. For a Python implementation, see
    '../python/example4.py'.

    NanoGUI was developed by Wenzel Jakob <wenzel.jakob@epfl.ch>.
    The widget drawing code is based on the NanoVG demo application
    by Mikko Mononen.

    All rights reserved. Use of this source code is governed by a
    BSD-style license that can be found in the LICENSE.txt file.
*/


// Includes for the GLTexture class.

//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#if __GNUC__
//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#pragma GCC diagnostic ignored "-Wmissing-field-initializers"
///#endif
//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#if _WIN32
//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#pragma warning(push)
//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#pragma warning(disable: 4457 4456 4005 4312)
///#endif

///#define STB_IMAGE_IMPLEMENTATION

//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#if _WIN32
//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#pragma warning(pop)
///#endif
//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#if _WIN32
//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#if APIENTRY
//C++ TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#undef APIENTRY
///#endif
///#endif


public class MyGLCanvas extends nanogui.GLCanvas implements java.io.Closeable
{
	public MyGLCanvas(Widget parent)
	{
		super(parent);
		this.mRotation = nanogui.Vector3f(0.25f, 0.5f, 0.33f);

		mShader.init("a_simple_shader", "#version 330\n" + "uniform mat4 modelViewProj;\n" + "in vec3 position;\n" + "in vec3 color;\n" + "out vec4 frag_color;\n" + "void main() {\n" + "    frag_color = 3.0 * modelViewProj * vec4(color, 1.0);\n" + "    gl_Position = modelViewProj * vec4(position, 1.0);\n" + "}", "#version 330\n" + "out vec4 color;\n" + "in vec4 frag_color;\n" + "void main() {\n" + "    color = frag_color;\n" + "}");
			/* An identifying name */
			/* Vertex shader */
			/* Fragment shader */

		MatrixXu indices = new MatrixXu(3, 12); // Draw a cube
		indices.col(0) << 0, 1, 3;
		indices.col(1) << 3, 2, 1;
		indices.col(2) << 3, 2, 6;
		indices.col(3) << 6, 7, 3;
		indices.col(4) << 7, 6, 5;
		indices.col(5) << 5, 4, 7;
		indices.col(6) << 4, 5, 1;
		indices.col(7) << 1, 0, 4;
		indices.col(8) << 4, 0, 3;
		indices.col(9) << 3, 7, 4;
		indices.col(10) << 5, 6, 2;
		indices.col(11) << 2, 1, 5;

		MatrixXf positions = new MatrixXf(3, 8);
		positions.col(0) << -1, 1, 1;
		positions.col(1) << -1, 1, -1;
		positions.col(2) << 1, 1, -1;
		positions.col(3) << 1, 1, 1;
		positions.col(4) << -1, -1, 1;
		positions.col(5) << -1, -1, -1;
		positions.col(6) << 1, -1, -1;
		positions.col(7) << 1, -1, 1;

		MatrixXf colors = new MatrixXf(3, 12);
		colors.col(0) << 1, 0, 0;
		colors.col(1) << 0, 1, 0;
		colors.col(2) << 1, 1, 0;
		colors.col(3) << 0, 0, 1;
		colors.col(4) << 1, 0, 1;
		colors.col(5) << 0, 1, 1;
		colors.col(6) << 1, 1, 1;
		colors.col(7) << 0.5, 0.5, 0.5;
		colors.col(8) << 1, 0, 0.5;
		colors.col(9) << 1, 0.5, 0;
		colors.col(10) << 0.5, 1, 0;
		colors.col(11) << 0.5, 1, 0.5;

		mShader.bind();
		mShader.uploadIndices(indices);

		mShader.uploadAttrib("position", positions);
		mShader.uploadAttrib("color", colors);
	}

	public final void close()
	{
		mShader.free();
	}

	public final void setRotation(nanogui.Vector3f vRotation)
	{
		mRotation = vRotation;
	}

	@Override
	public void drawGL()
	{

		mShader.bind();

		Matrix4f mvp = new Matrix4f();
		mvp.setIdentity();
		float fTime = (float)glfwGetTime();
		mvp.topLeftCorner < 3,3>() = Eigen.Matrix3f(Eigen.AngleAxisf(mRotation[0] * fTime, Vector3f.UnitX()) * Eigen.AngleAxisf(mRotation[1] * fTime, Vector3f.UnitY()) * Eigen.AngleAxisf(mRotation[2] * fTime, Vector3f.UnitZ())) * 0.25f;

		mShader.setUniform("modelViewProj", mvp);

		glEnable(GL_DEPTH_TEST);
		/* Draw 12 triangles starting at index 0 */
		mShader.drawIndexed(GL_TRIANGLES, 0, 12);
		glDisable(GL_DEPTH_TEST);
	}

	private nanogui.GLShader mShader = new nanogui.GLShader();
	private Eigen.Vector3f mRotation = new Eigen.Vector3f();
}
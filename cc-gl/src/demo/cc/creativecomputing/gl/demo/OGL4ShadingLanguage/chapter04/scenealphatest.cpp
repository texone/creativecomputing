#include "scenealphatest.h"

#include <cstdio>
#include <cstdlib>

#include <QImage>
#include <QGLWidget>

#include "glutils.h"
#include "defines.h"

using glm::vec3;

#include <glm/gtc/matrix_transform.hpp>
#include <glm/gtx/transform2.hpp>
#include <glm/gtc/matrix_projection.hpp>

SceneAlphaTest::SceneAlphaTest()
{
}

void SceneAlphaTest::initScene()
{
    compileAndLinkShader();

    glEnable(GL_DEPTH_TEST);

    plane = new VBOPlane(50.0f, 50.0f, 1, 1);
    teapot = new VBOTeapot(14, mat4(1.0f));

    projection = mat4(1.0f);

    angle = 0.0;

    prog.setUniform("Light.Intensity", vec3(1.0f,1.0f,1.0f) );

    GLuint texIDs[2];
    glGenTextures(2, texIDs);

    // Load brick texture file
    const char * texName = "texture/cement.jpg";
    QImage brickImg = QGLWidget::convertToGLFormat(QImage(texName,"JPG"));

    // Copy brick texture to OpenGL
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, texIDs[0]);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, brickImg.width(), brickImg.height(), 0,
                 GL_RGBA, GL_UNSIGNED_BYTE, brickImg.bits());
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    prog.setUniform("BaseTex", 0);

    // Load moss texture file
    texName = "texture/moss.png";
    QImage mossImg = QGLWidget::convertToGLFormat(QImage(texName,"PNG"));

    // Copy moss texture to OpenGL
    glActiveTexture(GL_TEXTURE1);
    glBindTexture(GL_TEXTURE_2D, texIDs[1]);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, mossImg.width(), mossImg.height(), 0,
                 GL_RGBA, GL_UNSIGNED_BYTE, mossImg.bits());
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

    prog.setUniform("AlphaTex", 1);
}

void SceneAlphaTest::update( float t )
{
    angle += 0.001f;
    if( angle > TWOPI) angle -= TWOPI;
}

void SceneAlphaTest::render()
{
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    vec3 cameraPos = vec3(6.0f * cos(angle), 0.25f, 6.0f * sin(angle));
    view = glm::lookAt(cameraPos, vec3(0.0f,0.0f,0.0f), vec3(0.0f,1.0f,0.0f));

    prog.setUniform("Light.Position", vec4(0.0f,0.0f,0.0f,1.0f) );
    prog.setUniform("Material.Kd", 0.9f, 0.9f, 0.9f);
    prog.setUniform("Material.Ks", 0.0f, 0.0f, 0.0f);
    prog.setUniform("Material.Ka", 0.1f, 0.1f, 0.1f);
    prog.setUniform("Material.Shininess", 100.0f);

    model = mat4(1.0f);
    model = glm::translate(model, vec3(0.0f,-1.5f,0.0f));
    model = glm::rotate(model, -90.0f, vec3(1.0f,0.0f,0.0f));
    setMatrices();
    teapot->render();
}

void SceneAlphaTest::setMatrices()
{
    mat4 mv = view * model;
    prog.setUniform("ModelViewMatrix", mv);
    prog.setUniform("NormalMatrix",
                    mat3( vec3(mv[0]), vec3(mv[1]), vec3(mv[2]) ));
    prog.setUniform("MVP", projection * mv);
}

void SceneAlphaTest::resize(int w, int h)
{
    glViewport(0,0,w,h);
    width = w;
    height = h;
    projection = glm::perspective(60.0f, (float)w/h, 0.3f, 100.0f);
}

void SceneAlphaTest::compileAndLinkShader()
{
    if( ! prog.compileShaderFromFile("shader/alphatest.vs",GLSLShader::VERTEX) )
    {
        printf("Vertex shader failed to compile!\n%s",
               prog.log().c_str());
        exit(1);
    }
    if( ! prog.compileShaderFromFile("shader/alphatest.fs",GLSLShader::FRAGMENT))
    {
        printf("Fragment shader failed to compile!\n%s",
               prog.log().c_str());
        exit(1);
    }
    if( ! prog.link() )
    {
        printf("Shader program failed to link!\n%s",
               prog.log().c_str());
        exit(1);
    }

    prog.use();
}

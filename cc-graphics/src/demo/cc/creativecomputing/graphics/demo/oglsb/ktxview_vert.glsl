#version 410 core

uniform mat4 ModelViewMatrix;
uniform mat3 NormalMatrix;
uniform mat4 ProjectionMatrix;
uniform mat4 MVP;

layout (location = 0) in vec3 vertex;
layout (location = 1) in vec2 textureCoord_0;

out vec2 vs_fs_texcoord;

void main(void){
	vs_fs_texcoord = textureCoord_0;
    gl_Position = MVP * vec4(vertex,1.0);
}
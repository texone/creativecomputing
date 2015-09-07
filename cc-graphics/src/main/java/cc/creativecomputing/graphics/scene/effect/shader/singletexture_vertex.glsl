#version 400

layout (location = 0) in vec3 vertex;
layout (location = 1) in vec2 textureCoord_0;

uniform mat4 MVP;
out vec2 vTexcoord;

void main(){
	vTexcoord = textureCoord_0;
    gl_Position = MVP * vec4(vertex,1.0);
}
#version 410 core

out VS_OUT {
	vec2 tc;
} vs_out;

uniform mat4 mvp;
uniform float offset;

layout (location = 0) in vec4 position;
layout (location = 1) in vec2 texcoord;

void main(void) {
	vs_out.tc = texcoord;
	gl_Position = mvp * position; //
}
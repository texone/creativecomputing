#version 410 core

layout (location = 0) in vec4 position;
layout (location = 1) in vec2 texcoord;

out vec2 vs_fs_texcoord;

void main(void)
{
	vs_fs_texcoord = texcoord;
    gl_Position = position;
}
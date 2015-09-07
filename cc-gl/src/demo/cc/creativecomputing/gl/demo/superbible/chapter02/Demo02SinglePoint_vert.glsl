#version 410 core

layout (location = 0) in vec4 position;

void main(void){
	gl_Position = position;
}
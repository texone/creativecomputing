#version 410 core

layout (location = 0) out vec4 color;

in VS_OUT{
	vec2 tc;
} fs_in;

uniform sampler2D tex;

void main(void){
	color = vec4(fs_in.tc,0.0,1.0);//texture(tex, fs_in.tc);
}
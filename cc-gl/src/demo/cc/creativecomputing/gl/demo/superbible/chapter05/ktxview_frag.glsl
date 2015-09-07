#version 410 core
 
uniform sampler2D s; 
 
uniform float exposure;

in vec2 vs_fs_texcoord;

out vec4 color;
 
void main(void){
	color = texture(s, vs_fs_texcoord) * exposure; 
}
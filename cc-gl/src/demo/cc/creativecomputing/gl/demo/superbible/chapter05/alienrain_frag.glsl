 #version 420 core
 
layout (location = 0) out vec4 color;
 
in VS_OUT{
	flat int alien;
	vec2 tc; 
} fs_in; 
 
layout (binding = 0) uniform sampler2DArray tex_aliens;
 
void main(void){
	color = texture(tex_aliens, vec3(fs_in.tc, float(fs_in.alien))); 
}
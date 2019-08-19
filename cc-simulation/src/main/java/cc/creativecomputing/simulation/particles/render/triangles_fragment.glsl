#version 120 
#extension GL_ARB_texture_rectangle : enable

uniform sampler2D texture;

void main(){
	//if(gl_Color.a == 0)discard;
	vec4 col = texture2D(texture, vec2(gl_TexCoord[0].x,1 - gl_TexCoord[0].y));
	gl_FragColor = col;
}
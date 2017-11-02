#version 120 
#extension GL_ARB_texture_rectangle : enable

uniform sampler2DRect data;
uniform float scale;

void main(){
	gl_FragColor = texture2DRect(data, gl_TexCoord[0].xy);
	gl_FragColor.rgb /= scale;
	gl_FragColor.a = 1.0;
}
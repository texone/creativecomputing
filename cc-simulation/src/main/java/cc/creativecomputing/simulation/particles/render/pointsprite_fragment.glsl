#version 120 
#extension GL_ARB_texture_rectangle : enable

uniform sampler2D pointSprite;

void main(){
	gl_FragColor = texture2D(pointSprite,gl_TexCoord[0].xy);
	gl_FragColor.a = gl_Color.a;
}
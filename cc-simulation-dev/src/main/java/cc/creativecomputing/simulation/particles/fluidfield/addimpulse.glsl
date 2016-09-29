#version 120 

uniform vec2 windowDimension;
uniform vec2 position;

uniform vec4 color;

uniform float radius;

uniform sampler2DRect baseTexture;

void main(){
	vec2 pos = position - gl_TexCoord[0]/windowDimension;
	radius /= windowDimension.x;
	float gaussian = exp(-dot(pos,pos) / radius);
	
	vec4 direction = color * 2 - 1;
	direction.w = 1;
	
	gl_FragColor = texture2DRect(baseTexture, gl_TexCoord[0]) + direction *  gaussian;
}
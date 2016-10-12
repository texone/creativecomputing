#version 120 

uniform vec2 windowDimension;
uniform vec2 position;

uniform vec4 color;

uniform float radius;

uniform sampler2DRect baseTexture;

void main(){
	vec2 pos = position - gl_TexCoord[0].xy / windowDimension;
	float myRadius = radius / windowDimension.x;
	float gaussian = exp(-dot(pos,pos) / myRadius);
	
	gl_FragColor = texture2DRect(baseTexture, gl_TexCoord[0].xy) + color *  gaussian;
	gl_FragColor = clamp(gl_FragColor, 0.0, 1.0);
}
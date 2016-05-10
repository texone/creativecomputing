#version 120
#extension GL_ARB_texture_rectangle : enable

uniform sampler2DRect Field;

void main() {

	vec2 output = vec2 (0.0, 0.0);
	vec2 coords = gl_TexCoord[0].xy;
	vec2 tmp;	
	output  = texture2DRect (Field, gl_TexCoord[0].xy).rg;
	
	tmp = texture2DRect (Field, gl_TexCoord[0].xy + vec2( 0,-1)).rg;
	if (tmp.y >= 0) {
		output += tmp / 6;	
	}
	
	tmp = texture2DRect (Field, gl_TexCoord[0].xy + vec2(-1,-1)).rg;
	if (tmp.y >= 0) {
		output += tmp / 12;	
	}
	
	tmp = texture2DRect (Field, gl_TexCoord[0].xy + vec2( 0, 1)).rg;
	if (tmp.y <= 0) {
		output += tmp / 6;		
	}
	
	tmp = texture2DRect (Field, gl_TexCoord[0].xy + vec2( 1, 1)).rg;
	if (tmp.y <= 0) {
		output += tmp / 12;
	}
	
	tmp = texture2DRect (Field, gl_TexCoord[0].xy + vec2(-1, 0)).rg;
	if (tmp.x >= 0) {
		output += tmp / 6;	
	}
	
	tmp = texture2DRect (Field, gl_TexCoord[0].xy + vec2(-1, 1)).rg;
	if (tmp.x >= 0) {
		output += tmp/12;
	}
	
	tmp = texture2DRect (Field, gl_TexCoord[0].xy + vec2( 1, 0)).rg;
	if (tmp.x <= 0) {
		output += tmp / 6;
	}
	
	tmp = texture2DRect (Field, gl_TexCoord[0].xy + vec2( 1,-1)).rg;
	if (tmp.x <= 0) {
		output += tmp / 12;
	}

	//output /= 1.3;
	gl_FragColor = vec4 (output, 0.0, 1.0);	
}
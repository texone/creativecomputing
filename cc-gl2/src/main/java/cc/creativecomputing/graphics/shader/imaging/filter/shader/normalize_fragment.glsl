#version 120
#extension GL_ARB_texture_rectangle : enable

uniform sampler2DRect IN;
uniform float factor;


void main() {

	vec4 val = texture2DRect(IN, gl_TexCoord[0].xy);
	gl_FragData[0] = val / factor;
}

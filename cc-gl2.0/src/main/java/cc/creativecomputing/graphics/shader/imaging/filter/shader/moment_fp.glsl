#version 120
#extension GL_ARB_texture_rectangle : enable

uniform sampler2DRect IN0;


uniform float threshold;
uniform float exp;
uniform float amplify;

void main(){
	
	vec4 inp = texture2DRect (IN0, gl_TexCoord[0].xy);
	
	if (inp.r < threshold) {
		inp = inp*0;
	}
	inp = pow(inp, vec4(exp,exp,exp,1.0));
	
	gl_FragData[0] = inp;
}

#version 120
#extension GL_ARB_texture_rectangle : enable

uniform sampler2DRect IN0;
uniform sampler2DRect IN1;
uniform sampler2DRect OUT1;
uniform sampler2DRect OUT2;

uniform float a0 = 1.0;
uniform float a1 = 0.0;
uniform float b1 = 0.0;
uniform float b2 = 0.0;

uniform float gain;
uniform float thresh;

void main(){

	/*
	float in0 = texture2DRect (IN0, gl_TexCoord[0].xy).r;
	float in1 = texture2DRect (IN1, gl_TexCoord[0].xy).r;
	float out1 = texture2DRect (OUT1, gl_TexCoord[0].xy).r;
	float out2 = texture2DRect (OUT2, gl_TexCoord[0].xy).r;
	float val = a0 * in0 + a1 * in1 + b1*out1 + b2*out2;
	*/
	
	vec3 in0  = texture2DRect (IN0, gl_TexCoord[0].xy).xyz;
	vec3 in1  = texture2DRect (IN1, gl_TexCoord[0].xy).xyz;
	vec3 out1 = texture2DRect (OUT1, gl_TexCoord[0].xy).xyz;
	vec3 out2 = texture2DRect (OUT2, gl_TexCoord[0].xy).xyz;

	// 3d 
	vec3 val = gain * (a0*in0 + a1*in1 + b1*out1 + b2*out2).xyz;
	if (val.x < thresh || val.y < thresh) {
		//val = vec3(0,0,0);
	}
	gl_FragColor = vec4 (val, 1.0);
 	
	
	// 1d
	/*
	float val = gain * (a0*in0 + a1*in1 + b1*out1 + b2*out2).x;
	
	if (val > 1.0) {
		val = 1.0;
	}
	if (val < 0) {
		//val = -val;
		val = 0;
	}
	if (val < thresh) {
		val = 0.0;
	}

 	gl_FragColor = vec4 (val, val, val, 1.0);
 	*/
 	
}

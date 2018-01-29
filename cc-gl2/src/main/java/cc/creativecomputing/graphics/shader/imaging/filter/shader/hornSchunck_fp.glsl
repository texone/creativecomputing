#version 120
#extension GL_ARB_texture_rectangle : enable

uniform sampler2DRect UV;
uniform sampler2DRect E_xyt;


float weight (int i, int j) {
	return 1.0 / (6.0 * (i+i + j*j));
}

void main() {

	vec2 coords = gl_TexCoord[0].xy;
	float a = 5;
	
	// last iteration result
	vec2 uv = texture2DRect (UV, gl_TexCoord[0].xy).rg;
	
	// the current frame derivatives
	vec3 E  = texture2DRect (E_xyt, gl_TexCoord[0].xy).rgb;
	
	
	// calc the local mean of last iterations uv field
	float u_mean = 0.0;
	float v_mean = 0.0;
	
	//sum over 1pixel nb, weighting: 
	//	1/12 1/6 1/12
	//	1/6	 -1	 1/6
	//	1/12 1/6 1/12
	vec2 uv_mean = vec2 (0,0);
	
	uv_mean += 0.083333 * texture2DRect (UV, gl_TexCoord[0].xy + vec2(0,0)).rg ;
	uv_mean += 0.166667 * texture2DRect (UV, gl_TexCoord[0].xy + vec2(1,0)).rg ;
	uv_mean += 0.083333 * texture2DRect (UV, gl_TexCoord[0].xy + vec2(2,0)).rg ;
	uv_mean += 0.166667 * texture2DRect (UV, gl_TexCoord[0].xy + vec2(0,1)).rg ;
	uv_mean += 0 		* texture2DRect (UV, gl_TexCoord[0].xy + vec2(1,1)).rg ;
	uv_mean += 0.166667 * texture2DRect (UV, gl_TexCoord[0].xy + vec2(2,1)).rg ;
	uv_mean += 0.083333 * texture2DRect (UV, gl_TexCoord[0].xy + vec2(0,2)).rg ;
	uv_mean += 0.166667 * texture2DRect (UV, gl_TexCoord[0].xy + vec2(1,2)).rg ;
	uv_mean += 0.083333 * texture2DRect (UV, gl_TexCoord[0].xy + vec2(2,2)).rg ;
	

	vec2 uv_new = vec2(0,0);
	uv_new.x = uv.x - E.x*(E.x * uv_mean.x + E.y * uv_mean.y + E.z)/ (E.x*E.x + E.y*E.y + a*a);
	uv_new.y = uv.y - E.y*(E.x * uv_mean.x + E.y * uv_mean.y + E.z)/ (E.x*E.x + E.y*E.y + a*a);
	
	gl_FragColor = vec4 (uv_new*1.1, 0.0, 1.0);
}
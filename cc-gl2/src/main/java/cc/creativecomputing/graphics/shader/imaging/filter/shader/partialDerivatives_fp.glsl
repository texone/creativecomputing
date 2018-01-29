#version 120
#extension GL_ARB_texture_rectangle : enable

uniform sampler2DRect IN0;
uniform sampler2DRect IN1;
uniform float gain = 1.0;

void main() {
	vec2 coords = gl_TexCoord[0].xy;
	
	float outx = 0;
	float outy = 0; 
	float outz = 0;
	
	float tmp00 = texture2DRect (IN0, gl_TexCoord[0].xy+vec2 (-1, -1)).r;
	float tmp01 = texture2DRect (IN0, gl_TexCoord[0].xy+vec2 (-1,  0)).r;
	float tmp02 = texture2DRect (IN0, gl_TexCoord[0].xy+vec2 (-1,  1)).r;
	
	float tmp10 = texture2DRect (IN0, gl_TexCoord[0].xy+vec2 (0, -1)).r;
	float tmp11 = texture2DRect (IN0, gl_TexCoord[0].xy+vec2 (0,  0)).r;
	float tmp12 = texture2DRect (IN0, gl_TexCoord[0].xy+vec2 (0,  1)).r;
	
	float tmp20 = texture2DRect (IN0, gl_TexCoord[0].xy+vec2 (1, -1)).r;
	float tmp21 = texture2DRect (IN0, gl_TexCoord[0].xy+vec2 (1,  0)).r;
	float tmp22 = texture2DRect (IN0, gl_TexCoord[0].xy+vec2 (1,  1)).r;
	
	float tmp00_1 = texture2DRect (IN1, gl_TexCoord[0].xy+vec2 (-1, -1)).r;
	float tmp01_1 = texture2DRect (IN1, gl_TexCoord[0].xy+vec2 (-1,  0)).r;
	float tmp02_1 = texture2DRect (IN1, gl_TexCoord[0].xy+vec2 (-1,  1)).r;
	
	float tmp10_1 = texture2DRect (IN1, gl_TexCoord[0].xy+vec2 (0, -1)).r;
	float tmp11_1 = texture2DRect (IN1, gl_TexCoord[0].xy+vec2 (0,  0)).r;
	float tmp12_1 = texture2DRect (IN1, gl_TexCoord[0].xy+vec2 (0,  1)).r;
	
	float tmp20_1 = texture2DRect (IN1, gl_TexCoord[0].xy+vec2 (1, -1)).r;
	float tmp21_1 = texture2DRect (IN1, gl_TexCoord[0].xy+vec2 (1,  0)).r;
	float tmp22_1 = texture2DRect (IN1, gl_TexCoord[0].xy+vec2 (1,  1)).r;
	
	outx  = 2*(tmp01 - tmp21) + (tmp00 - tmp20) + (tmp02 - tmp22);
	outy  = 2*(tmp10 - tmp12) + (tmp20 - tmp22) + (tmp00 - tmp02);
	outz  = (tmp00 + tmp01 + tmp02 + tmp10 + tmp11 + tmp12 + tmp20 + tmp21 + tmp22 - tmp00_1 - tmp01_1 - tmp02_1 - tmp10_1 - tmp11_1 - tmp12_1 - tmp20_1 - tmp21_1 - tmp22_1) / 9; 
	
	gl_FragColor = vec4 (gain*outx/8.0, gain*outy/8.0, gain*outz, 1.0);
}


/*
void main() {

	vec2 coords = gl_TexCoord[0].xy;

	float outx = 0;
	float outy = 0;
	float outz = 0;
	
	float tmp0, tmp1;
	
	// [0,0], t=0
	tmp0 = texture2DRect (IN0, gl_TexCoord[0].xy+vec2 (0, 0)).r;
	outx -= tmp0;
	outy -= tmp0;
	outz += tmp0;
	
	// [0,1], t=0
	tmp0 = texture2DRect (IN0, gl_TexCoord[0].xy+vec2 (0, 1)).r;
	outx -= tmp0;
	outy += tmp0;
	outz += tmp0;
	
	// [1,0], t=0
	tmp0 = texture2DRect (IN0, gl_TexCoord[0].xy+vec2 (1, 0)).r;
	outx += tmp0;
	outy -= tmp0;
	outz += tmp0;
	
	// [1,1], t=0
	tmp0 = texture2DRect (IN0, gl_TexCoord[0].xy+vec2 (1, 1)).r;
	outx += tmp0;
	outy += tmp0;
	outz += tmp0;
	
	// [0,0], t=-1
	tmp1 = texture2DRect (IN1, gl_TexCoord[0].xy+vec2 (0, 0)).r;
	outx -= tmp1;
	outy -= tmp1;
	outz -= tmp1;
	
	// [0,1], t=-1
	tmp1 = texture2DRect (IN1, gl_TexCoord[0].xy+vec2 (0, 1)).r;
	outx -= tmp1;
	outy += tmp1;
	outz -= tmp1;
	
	// [1,0], t=-1
	tmp1 = texture2DRect (IN1, gl_TexCoord[0].xy+vec2 (1, 0)).r;
	outx += tmp1;
	outy -= tmp1;
	outz -= tmp1;
	
	// [1,1], t=-1
	tmp1 = texture2DRect (IN1, gl_TexCoord[0].xy+vec2 (1, 1)).r;
	outx += tmp1;
	outy += tmp1;
	outz -= tmp1;
	
	outx /= 4.0;
	outy /= 4.0;
	outz /= 4.0;
	gl_FragColor = vec4 (outx, outy, outz, 1.0);
}*/
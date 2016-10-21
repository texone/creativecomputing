#version 120
#define KERNEL_SIZE 9

float kernel[KERNEL_SIZE];
vec2 offset[KERNEL_SIZE];

uniform sampler2DRect tex;

uniform float ru;          // rate of diffusion of U
uniform float rv;          // rate of diffusion of V

uniform float f;           // some coupling parameter
uniform float k;           // another coupling parameter

void main(void){
	
	kernel[0] = 0.707106781;
	kernel[1] = 1.0;
	kernel[2] = 0.707106781;
	kernel[3] = 1.0;
	kernel[4] =-6.82842712;
	kernel[5] = 1.0;
	kernel[6] = 0.707106781;
	kernel[7] = 1.0;
	kernel[8] = 0.707106781;
	
	offset[0] = vec2( -1, -1);
	offset[1] = vec2(0.0, -1);
	offset[2] = vec2(  1, -1);
	
	offset[3] = vec2( -1, 0.0);
	offset[4] = vec2(0.0, 0.0);
	offset[5] = vec2(  1, 0.0);

	offset[6] = vec2( -1, 1);
	offset[7] = vec2(0.0, 1);
	offset[8] = vec2(  1, 1);
	

	vec2 texColor		= texture2DRect( tex, gl_TexCoord[0].xy ).rb;
	
	vec2 sum			= vec2( 0.0, 0.0 );
	
	for( int i=0; i<KERNEL_SIZE; i++ ){
		vec2 tmp	= texture2DRect( tex, gl_TexCoord[0].xy + offset[i] ).rb;
		sum			+= tmp * kernel[i];
	}
	
	
	float F		= f +  0.025 - 0.0005;
	float K		= k +  0.025 - 0.0005;
	
	float u		= texColor.r;
	float v		= texColor.g;
	float uvv	= u * v * v;
//============================================================================
	float du	= ru * sum.r - uvv + F * (1.0 - u);		// Gray-Scott equation
	float dv	= rv * sum.g + uvv - (F + K) * v;		// diffusion+-reaction
//============================================================================
	u += du * 0.6;
	v += dv * 0.6;
	gl_FragColor = vec4( clamp( u, 0.0, 1.0 ), 1.0 - u/v, clamp( v, 0.0, 1.0 ), 1.0 );
	
	//gl_FragColor = vec4(1.0);
}

	/*
	kernel[0]  = 1.0/331.0;
	kernel[1]  = 4.0/331.0;
	kernel[2]  = 7.0/331.0;
	kernel[3]  = 4.0/331.0;
	kernel[4]  = 1.0/331.0;
	kernel[5]  = 4.0/331.0;
	kernel[6]  = 20.0/331.0;
	kernel[7]  = 33.0/331.0;
	kernel[8]  = 20.0/331.0;
	kernel[9]  = 4.0/331.0;
	kernel[10] = 7.0/331.0;
	kernel[11] = 33.0/331.0;
	kernel[12] = -55.0/331.0;
	kernel[13] = 33.0/331.0;
	kernel[14] = 7.0/331.0;
	kernel[15] = 4.0/331.0;
	kernel[16] = 20.0/331.0;
	kernel[17] = 33.0/331.0;
	kernel[18] = 20.0/331.0;
	kernel[19] = 4.0/331.0;
	kernel[20] = 1.0/331.0;
	kernel[21] = 4.0/331.0;
	kernel[22] = 7.0/331.0;
	kernel[23] = 4.0/331.0;
	kernel[24] = 1.0/331.0;
	
	offset[0]  = vec2(-2.0, -2.0);
	offset[1]  = vec2( -1.0, -2.0);
	offset[2]  = vec2(0.0, -2.0);
	offset[3]  = vec2(  1.0, -2.0);
	offset[4]  = vec2( 2.0, -2.0);
	
	offset[5]  = vec2(-2.0, -1.0);
	offset[6]  = vec2( -1.0, -1.0);
	offset[7]  = vec2(0.0, -1.0);
	offset[8]  = vec2(  1.0, -1.0);
	offset[9]  = vec2( 2.0, -1.0);
	
	offset[10] = vec2(-2.0, 0.0);
	offset[11] = vec2( -1.0, 0.0);
	offset[12] = vec2(0.0, 0.0);
	offset[13] = vec2(  1.0, 0.0);
	offset[14] = vec2( 2.0, 0.0);
	offset[15] = vec2(-2.0, 1.0);
	offset[16] = vec2( -1.0, 1.0);
	offset[17] = vec2(0.0, 1.0);
	offset[18] = vec2(  1.0, 1.0);
	offset[19] = vec2( 2.0, 1.0);
	
	offset[20] = vec2(-2.0, 2.0);
	offset[21] = vec2( -1.0, 2.0);
	offset[22] = vec2(0.0, 2.0);
	offset[23] = vec2(  1.0, 2.0);
	offset[24] = vec2( 2.0, 2.0);
	*/
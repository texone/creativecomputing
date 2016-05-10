#include "shader/util/simplex.fp"

uniform float noiseScale = 1;
uniform float3 noiseOffset = float3(0,0,0);
	
void main(
	in 		float2 		coords	: WPOS,
	out 	float4 		output0 : COLOR0
) { 
	output0 = (snoise(float3(coords.xy,0) * 0.01 * noiseScale + noiseOffset) + 1)/2;
}     
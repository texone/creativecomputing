#include "shader/util/simplex.fp"

uniform float noiseScale = 1;
uniform float3 noiseOffset = float3(0,0,0);
uniform sampler3D texture : TEXUNIT0;
	
void main(
	in 		float2 		coords	: WPOS,
	in 		float2 		texCoords : TEXCOORD0,
	out 	float4 		output0 : COLOR0
) { 
	float myNoise = (noise(float3(coords.xy,0) * 0.01 * noiseScale + noiseOffset) + 1)/2;
	vec4 myRefColor = tex3D(texture, float3(texCoords,0));
	vec4 myColor = tex3D(texture, float3(texCoords,myNoise));
	//output0 = (
	//output0 = myColor *  abs(myColor - myRefColor);
	//output0.a = 1.0;
	output0 = myColor;
}     
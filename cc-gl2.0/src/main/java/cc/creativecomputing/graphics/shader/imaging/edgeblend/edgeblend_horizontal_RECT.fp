uniform samplerRECT blendTexture : TEXUNIT0;
uniform float blendStart;
uniform float blendEnd;
uniform float blendPower;
uniform float blendBreak;

uniform float rGamma;
uniform float gGamma;
uniform float bGamma;

void main(
	in float2 iTexCoord : TEXCOORD0,
	out float4 oColor : COLOR0
){
	float blend = (iTexCoord.x - blendStart) / (blendEnd - blendStart);
	float blendStep = step(0.5, blend);
	blend = 
	blendBreak * pow(2 * blend,blendPower) * (1 - blendStep)  + 
	(1 - (1 - blendBreak) * pow(2 * (1 - blend),blendPower)) * blendStep;
	
	float4 blendColor = float4(
		pow(blend, 1 / rGamma),
		pow(blend, 1 / gGamma),
		pow(blend, 1 / bGamma),
		1
	);
	float4 color = texRECT(blendTexture, iTexCoord);
	oColor = color * blendColor;
}
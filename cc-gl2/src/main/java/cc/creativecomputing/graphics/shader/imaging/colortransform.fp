uniform sampler2D texture : TEX0;
uniform float4x4 colorMatrix;

void main(
	in float2 texCoord : TEXCOORD0,
	out float3 color : COLOR0
){
    color = mul(colorMatrix,tex2D(texture, texCoord));
}
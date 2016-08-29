
uniform samplerRECT sortTexture : TEXUNIT0;
uniform samplerRECT positionTexture : TEXUNIT1;
uniform float3 viewerPosition;

void main(
	in float2 iTexCoord : TEXCOORD0,
	out float4 oColor : COLOR
){

	float2 particleIndex = texRECT(sortTexture, iTexCoord.xy).xy;

	float3 particlePos = (float3)texRECT(positionTexture, particleIndex);

	float3 delta = viewerPosition - particlePos;
	float distanceSqr = dot(delta, delta);

	// Prevent unused, far-away particles from destroying comparisons in sorting.
	if (distanceSqr > 1e6 || isnan(distanceSqr))
		distanceSqr = 1e6;

	oColor = float4(particleIndex,particlePos.z,1.0);
}
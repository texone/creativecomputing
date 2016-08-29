uniform samplerRECT sortData : TEXUNIT0;
	
uniform int2 size;
uniform int sortStep;
uniform int sortCount;
	
void main(
	in float2 current : TEXCOORD0,
	out float3 oColor : COLOR0
) {
	float3 currentSample = texRECT(sortData, current);
	oColor = currentSample;

	float i = (current.y - 0.5) * size.x + current.x;

	float iMod = fmod((float)(i / sortStep), (float)sortCount);

	if (iMod >= 1.0 && iMod < sortCount - 1.0){
		float b = (fmod((float)iMod, 2.0) > 1.0 ? 1.0 : -1.0);

		float otherI = i + (b * sortStep);
		float2 otherPos = float2(fmod(otherI, size.x), floor(otherI / size.x) + 0.5);

		float3 otherSample = texRECT(sortData, otherPos);

		if (b >= 0){
			oColor = currentSample.z < otherSample.z ? currentSample : otherSample;
		} else {
			oColor = currentSample.z > otherSample.z ? currentSample : otherSample;
		}
	} else
		oColor = currentSample;
}
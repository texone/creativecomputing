uniform samplerRECT infoTexture : TEXUNIT0;
uniform samplerRECT springIDs : TEXUNIT1;

void main (
	in 	float2 texID : WPOS,
	out float4 newSpringIDs : COLOR0
){
	newSpringIDs = texRECT(springIDs, texID);
	
	float4 particleInfo1 = texRECT(infoTexture, newSpringIDs.xy);
	float4 particleInfo2 = texRECT(infoTexture, newSpringIDs.zw);
	
	if(particleInfo1.x >= particleInfo1.y && particleInfo1.z == 0.0){
		newSpringIDs.xy = float2(-1,-1);
	}
	if(particleInfo2.x >= particleInfo2.y && particleInfo2.z == 0.0){
		newSpringIDs.zw = float2(-1,-1);
	}
	
	//newSpringIDs = float4(1.0,0.0,0.0,1.0);
}
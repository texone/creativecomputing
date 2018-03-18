uniform samplerRECT positionTexture;
uniform samplerRECT infoTexture;
uniform samplerRECT velocityTexture;
uniform samplerRECT colorTexture;


void main (
	in 	float2 texID : WPOS,
	out float4 newPosition : COLOR0,
	out float4 newInfo : COLOR1,
	out float4 newVelocity : COLOR2,
	out float4 newColor : COLOR3
){
	float4 position = texRECT (positionTexture, texID);
	float4 info     = texRECT (infoTexture, texID);
	float4 velocity = texRECT (velocityTexture, texID);
	float4 color    = texRECT (colorTexture, texID);
		
	newPosition = position;
	newInfo     = info; 
	newVelocity = velocity;
	newColor    = color;
}
	           
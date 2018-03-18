uniform samplerRECT positionTexture;
uniform samplerRECT velocityTexture;
uniform samplerRECT infoTexture;
uniform samplerRECT colorTexture;
uniform float deltaTime;

uniform float3 randomSeed;
uniform float velocityFactor;
uniform float lifetimeFactor;
uniform float brightnessFactor;
uniform bool invert;

uniform samplerRECT destroyTexture;


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
	
	float3 d = texRECT (destroyTexture, position+float3(1000,100,0));
	
	if (d.r>0.4 && d.z<0.4) {
	//if (d.r>0.4) {
		newColor    *= brightnessFactor;
		newVelocity *= velocityFactor;
		newInfo.y   *= lifetimeFactor;
	}
}
	           
	           
uniform samplerRECT positions;
uniform samplerRECT velocities;

uniform float3 impulsePosition;
uniform float impulseRadius;
uniform float impulseStrength;

void main (
	in 		float2 texID : WPOS,
	out 	float3 newVelocity : COLOR0
){
	float3 position =(float3) texRECT (positionTexture, texID);
	float3 velocity = texRECT (velocityTexture, texID);
	
	float distance = distance(position, impulsePosition);
	if(distance > radius){
		newVelocity = velocity;
	}
	
	float myScale = 1 - distance / radius;
	
	float3 myDirection = impulsePosition - position;
	normalize(myDirection);
	myDirection *= myScale;
	myDirection *= impulseStrength;
	
	newVelocity = myDirection;
	
}
	           
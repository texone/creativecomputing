
interface Impulse{
	float3 impulse(float3 thePosition, float3 theVelocity, float2 theTexID, float theDeltaTime);
};

struct SphereImpulse : Impulse{
	float3 center;
	float radius;
	float strength;

	float3 impulse(float3 thePosition, float3 theVelocity, float2 theTexID, float theDeltaTime){
	
		float dist = distance(center, thePosition);
		
		if(dist > radius){
			return float3(0);
		}
	
		float myScale = 1 - dist / radius;
	
		float3 myDirection = thePosition - center;
		normalize(myDirection);
		myDirection *= myScale;
		myDirection *= strength;
		return myDirection;
	}
};

uniform Impulse impulses[];
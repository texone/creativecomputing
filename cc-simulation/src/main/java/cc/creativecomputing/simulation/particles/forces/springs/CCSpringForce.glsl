

uniform float springConstant;
uniform float forceRestLength;

vec3 springForce2(vec3 thePosition1, vec3 thePosition2, float theRestLength){
	vec3 delta = thePosition2 - thePosition1;
	float deltalength = length(delta);
	delta /= max(1,deltalength);
	float springForce = (deltalength - theRestLength) * springConstant * 0.1;// * float(deltalength > theRestLength || theForceRestLength > 0);
	return delta * springForce;
}

uniform int numberOfBuffers;
uniform int xBuffers;
uniform float index;
uniform float strength;
uniform float damping;

uniform sampler2DRect data;
uniform vec2 textureSize;

vec3 springForce(vec3 thePosition1, vec3 thePosition2, vec3 theVelocity1, vec3 theVelocity2, float theRestLength){
	
	vec3 deltaPosition = thePosition1 - thePosition2;
	vec3 deltaVelocity = theVelocity1 - theVelocity2;
	
	float myDistance = length(deltaPosition);
	
	deltaPosition /= max(1,myDistance);
	//deltaPosition *= myDistance > 0;

    float springForce = - (myDistance - theRestLength) * springConstant * float(myDistance > theRestLength);
        
   	float dampForce = -damping * dot(deltaPosition, deltaVelocity);
    return deltaPosition * (springForce + dampForce);
}

vec3 function(vec3 thePosition, vec3 theVelocity, vec4 theInfos, vec4 theGroupInfos, vec2 theTexID, float theDeltaTime){
	vec3 force = vec3(0);
	
	for(int i = 0; i < numberOfBuffers;i++){
		vec2 myTexId = theTexID + textureSize * vec2(mod(i,xBuffers), i / xBuffers);
		vec4 springInfos = texture2DRect(data, myTexId);
		if(springInfos.x < 0)continue;
		
		// get positions of neighbouring particles
		vec3 position = texture2DRect(positionTexture, springInfos.xy).xyz;
		vec3 velocity = texture2DRect(velocityTexture, springInfos.xy).xyz;
		float restLength = springInfos.z;
		
		force += springForce(thePosition, position, theVelocity, velocity, restLength) * float(springInfos.x >= 0);
		//position = float(springInfos.x == -1) * vec3(i * 50,0,0);//texture2DRect(positionTexture, springInfos.xy).xyz;
		//force += position - thePosition;
			//continue;
	}

	return force * strength;
}
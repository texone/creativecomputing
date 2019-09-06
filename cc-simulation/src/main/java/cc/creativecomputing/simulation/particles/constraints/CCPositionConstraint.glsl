uniform float stiffness;
	
uniform float resilience;
uniform float friction;
uniform float minimalVelocity;

uniform int numberOfBuffers;
uniform int xBuffers;
uniform sampler2DRect data;
uniform vec2 textureSize;

// constrain a particle to be a fixed distance from another particle
	
vec3 function(vec3 theVelocity, vec3 thePosition, vec2 theTexID,float theDeltaTime){
	
	vec3 velocity = vec3(0);
	
	for(int i = 0; i < numberOfBuffers;i++){
		vec2 myTexId = theTexID + textureSize * vec2(mod(i,xBuffers), i / xBuffers);
		vec4 springInfos = texture2DRect(data, myTexId);
		if(springInfos.x < 0)continue;
		
		// get positions of neighbouring particles
		vec3 position = texture2DRect(positionTexture, springInfos.xy).xyz;
		float restLength = springInfos.z;
			
		vec3 delta = position- thePosition;
		float deltalength = length(delta);
		float diff = (deltalength - restLength) / deltalength;
		velocity += delta * stiffness * diff;
	}

	return velocity;
}
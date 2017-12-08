uniform sampler2DRect idTextures;
uniform sampler2DRect infoTextures;

uniform float strength;
uniform float index;

uniform float springConstant;

vec3 function(vec3 thePosition, vec3 theVelocity, vec2 theTexID, float theDeltaTime){
	
	vec3 force = 0;
	
	int4 ids = texture2DRect(idTextures[i], theTexID);
		
	// get positions of neighbouring particles
	vec3 position1 = texture2DRect(positionTexture, ids.xy);
	vec3 position2 = texture2DRect(positionTexture, ids.zw);
			
	vec4 infos = texture2DRect(infoTextures[i], theTexID);
	float restLength1 = infos.x;
	float restLength2 = infos.y;
	float forceRestLength1 = infos.z;
	float forceRestLength2 = infos.w;
		
	force += springForce(thePosition, position1, restLength1, forceRestLength1) * (ids.x >= 0);
	force += springForce(thePosition, position2, restLength2, forceRestLength2) * (ids.z >= 0);
	
	return force * lifeTimeBlend(theTexID, index) * strength;// * targetStrength * strength;// / (theDeltaTime * 60);
}
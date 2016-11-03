/*
 * terrain constraint that takes a heightmap texture to let particles bounce of it.
 * texture can be scaled and moved in all x,y and z direction to place it in the scene
 * correctly
 */
uniform sampler2DRect lookUpTexture;
uniform sampler2DRect shapeTexture;
	
uniform vec3 scale;
uniform vec3 offset;
	
uniform float resilience;
uniform float friction;
uniform float minimalVelocity;

vec3 function(vec3 theVelocity, vec3 thePosition, vec2 theTexID, float theDeltaTime){

	vec3 fPosition = thePosition + theVelocity * theDeltaTime;
	fPosition = fPosition / scale + offset;
	
	vec3 position = thePosition / scale + offset;
	
	float currentID = texture2DRect(lookUpTexture, position.xy).x;
	float futureID = texture2DRect(lookUpTexture, fPosition.xy).x;

	if (currentID == futureID)return theVelocity;
	
	return theVelocity * -1;
	/*
	float height = shapeTexture();
		
	// Calculate normal vector.
	vec3 vertex0 = vec3(0, height * scale.y, 0);
	vec3 vertex1 = vec3(scale.x, terrainHeight(terrainPos + vec2(1, 0)) * scale.y, 0);
	vec3 vertex2 = vec3(0, terrainHeight(terrainPos + vec2(0, 1)) * scale.y, scale.z);
			
	vec3 normal = normalize(cross(vertex1 - vertex0, vertex2 - vertex0));
			
	if (normal.y < 0)
		normal *= -1;
	
	// Check whether previous time step has collision already.
	vec2 terrainPosOld = thePosition.xz / scale.xz + offset.xz;
	float heightOld = terrainHeight(terrainPosOld) * scale.y + offset.y;

	return bounceReflection(
		theVelocity, 
		normal, 
		thePosition.y - heightOld,
		resilience, 
		friction, 
		minimalVelocity
	);	
	*/	
	return theVelocity;
}
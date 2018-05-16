uniform sampler2DRect groupActivationTexture;
uniform sampler2DRect groupPositionTexture;

uniform float propability;

uniform vec3 startVelocity;
uniform vec3 offset;

uniform float randomPosition;
uniform float randomVelocity;

bool function(
	in vec4 position,
	in vec4 info,
	in vec4 velocity,
	in vec4 color,
	vec2 texID, 
	out vec4 newPosition, 
	out vec4 newInfo,
	out vec4 newVelocity,
	out vec4 newColor
) {
		//if(rand(texID) > propability)return false;

	vec4 groupInfos = texture2DRect (groupActivationTexture, texID);
		
	if(groupInfos.x <= 0.5) {
		newPosition.xyz = vec3(100000,0,0);
		newInfo.x = 0;
		newInfo.y = 0;
		return true;
	}

	if(info.y > 0) {	
		newPosition = position;
		newInfo = info;
		newInfo.x = groupInfos.y;
		newVelocity = velocity;
		return true;
	}
	vec4 groupPosition = texture2DRect (groupPositionTexture, texID);
			
	newPosition.xyz = groupPosition.xyz;
	newPosition.xyz += offset;
	newPosition.xy += randDirection(texID) * randomPosition * rand(texID);
	newPosition.w = 1;
					
	newInfo.x = 0;
	newInfo.y = 1;
	newInfo.z = 0;
	
	newVelocity.xyz = startVelocity;
	newVelocity.xy += randDirection(texID) * randomVelocity;
					
			//newVelocity.xy = newPosition.xy / 10;
			
	newColor = vec4(1.0);
					
	return true;
}
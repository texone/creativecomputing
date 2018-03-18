
uniform float propability;
uniform vec2 center;
uniform float radius;

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
	if(rand(texID) > propability)return false;
	
	newPosition.x = cos(rand(texID + vec2(3000,0)) * 6.3) * radius + center.x;
	newPosition.y = sin(rand(texID + vec2(3000,0)) * 6.3) * radius + center.y; 
			
	newInfo.x = 0;
	newInfo.y = 10;
	newInfo.z = -1;
			
	newVelocity.xy = newPosition.xy / 10;
	
	newColor = vec4(1.0);
			
	return true;
}
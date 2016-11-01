uniform sampler2DRect forceTexture;

uniform vec2 textureSize;
uniform vec2 textureScale;
uniform vec2 textureOffset;

uniform float strength;

vec3 function(vec3 thePosition, vec3 theVelocity, vec2 theTexID, float theDeltaTime){
	vec2 texturePos = thePosition.xy / textureScale.xy + textureOffset.xy;
	vec3 force = texture2DRect(forceTexture, texturePos.xy).xyz;
	force *= 2;
	force -= 1;
	force.z = 0;
		
	return force * strength;
}
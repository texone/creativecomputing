uniform sampler2D forceFieldTexture;

uniform vec2 textureSize;
uniform vec2 textureScale;
uniform vec2 textureOffset;

uniform float strength;
uniform float index;
uniform float blend;
uniform vec3 forceScale;

vec3 function(vec3 thePosition, vec3 theVelocity, vec4 theInfos, vec4 theGroupInfos, vec2 theTexID, float theDeltaTime){
	vec2 texturePos = thePosition.xy / textureScale.xy + textureOffset.xy;
	vec3 force = texture2D(forceFieldTexture, texturePos.xy).xyz;
	force *= 2;
	force -= 1;
	force *= forceScale;
	//force.z = 0;
		
	return force * strength;
}
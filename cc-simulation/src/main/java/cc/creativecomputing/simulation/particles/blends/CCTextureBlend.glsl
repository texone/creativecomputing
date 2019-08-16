uniform sampler2DRect blendTexture;

uniform float amount;
uniform vec3 channelAmounts;
uniform float globalAmount;
uniform float channelBlend;
uniform float channelRange;

vec2 function(vec3 thePosition, vec3 theVelocity, vec4 theInfos, vec4 theGroupInfos, vec2 theTexID, float theDeltaTime){

	vec4 channelInfos = texture2DRect (blendTexture, theTexID);
	
	float myOffsetSum = channelAmounts.x + channelAmounts.y + channelAmounts.z;
	float myModulation =
		channelInfos.x * channelAmounts.x + 
		channelInfos.y * channelAmounts.y + 
		channelInfos.z * channelAmounts.z;
		
	myModulation /= myOffsetSum;
	float minMod = mix(myModulation - channelRange, 0, globalAmount);
	float maxMod = mix(myModulation, 1, globalAmount);
	float blendMod = mix(channelBlend * (1 + channelRange) - channelRange, channelBlend, globalAmount);
	myModulation = smoothstep(minMod, maxMod, blendMod);
	myModulation = clamp(myModulation,0,1);
	return vec2(myModulation, amount);
}
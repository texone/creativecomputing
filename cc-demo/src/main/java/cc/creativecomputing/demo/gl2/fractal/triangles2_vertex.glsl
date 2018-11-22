#version 120 
#extension GL_ARB_texture_rectangle : enable

uniform sampler2DRect textureCoordsTexture;
uniform sampler2DRect centerTextureCoordsTexture;
uniform sampler2DRect randomTexture;
uniform sampler2DRect blendTexture;

uniform vec2 textureSize;
uniform vec2 blendTextureSize;

uniform float textureRandom;
uniform float textureRandomAdd;
uniform float useTextureRandom = 1;

uniform float positionRandom;

uniform float saturationRandom;
uniform float textureCenter;

uniform float level;
uniform float maxLevel;

uniform float alpha;

uniform float levelRandomness;
uniform float levelRandomBlend;
uniform float alphaRandomness;


void main(){
	vec2[7] levels;
	levels[0] = gl_MultiTexCoord0.xy;
	levels[1] = gl_MultiTexCoord0.zw;
	levels[2] = gl_MultiTexCoord1.xy;
	levels[3] = gl_MultiTexCoord1.zw;
	levels[4] = gl_MultiTexCoord2.xy;
	levels[5] = gl_MultiTexCoord2.zw;
	levels[6] = gl_MultiTexCoord3.xy;
	
	vec2 random = gl_MultiTexCoord3.zw;
	
	vec2 myTexCoords = texture2DRect(textureCoordsTexture,gl_MultiTexCoord0.xy).st / textureSize;
	vec4 blendColor = texture2DRect(blendTexture,myTexCoords * blendTextureSize);
	
	//float random = 0;
	
	float myLevel = blendColor.r + level;
	float myAdd = 1 - myLevel;
	myLevel += myAdd * levelRandomness;
	myLevel *= maxLevel;
	
	
	float c = 0;
	float leveler = 0;
	vec3 myRandomer;
	float myLevelFloor = floor(levelRandomness * maxLevel);
	while (c < myLevelFloor){
		myRandomer = texture2DRect(randomTexture,levels[int(maxLevel - c - 1)]).xyz;
		c++;
		leveler += mix(myRandomer.x, myRandomer.y, levelRandomBlend);
	}
	myRandomer = texture2DRect(randomTexture,levels[int(min(maxLevel - 1,maxLevel - c - 1))]).xyz;
	float leveler2 = leveler + myRandomer.x;
	float myLevelRandomBlend = levelRandomness * maxLevel - myLevelFloor;
	leveler2 = mix(leveler,leveler2,myLevelRandomBlend);
	
	//clamp(blendColor.r * (1 + levelRandomness) + random.x * levelRandomness - levelRandomness,0,1) * maxLevel;
	myLevel = clamp(leveler2 + myLevel ,0,maxLevel);
	int level1 = int(floor(myLevel));
	int level2 = int(ceil(myLevel));
	
	float blend = myLevel - level1;
	
	vec3 myRandom1 = texture2DRect(randomTexture,levels[level1]).xyz;
	vec3 myRandom2 = texture2DRect(randomTexture,levels[level2]).xyz;
	vec3 myRandom = mix(myRandom1, myRandom2, blend);
	
	vec2 myTexCoordsCenter1 = texture2DRect(centerTextureCoordsTexture,levels[level1]).st / textureSize;
	vec2 myTexCoordsCenter2 = texture2DRect(centerTextureCoordsTexture,levels[level2]).st / textureSize;
	vec2 myTexCoordsCenter = mix(myTexCoordsCenter1, myTexCoordsCenter2, blend);
	
	vec4 blendColor2 = texture2DRect(blendTexture,myTexCoordsCenter * blendTextureSize);
	float myAlpha = clamp((blendColor2.g + alpha) * (1 + alphaRandomness) + myRandom.z * alphaRandomness - alphaRandomness,0,1);
	float myTextureRandom = (blendColor2.b + textureRandomAdd) * textureRandom * useTextureRandom;
	
	gl_TexCoord[0] = vec4(mix(myTexCoords,myTexCoordsCenter,textureCenter) + myRandom.xy * myTextureRandom, 0, 1);
	gl_TexCoord[1] = vec4(mix(myTexCoords,myTexCoordsCenter,textureCenter) + myRandom.xy * saturationRandom, 0, 1);
	gl_FrontColor = vec4(myLevel, 1, myRandom.x, myAlpha);
	//gl_TexCoord[0] = vec4(myTexCoordsCenter, 0, 1);;
	
	float myPositionRandom = blendColor2.b * positionRandom * (1 - useTextureRandom);
	gl_Position = gl_ProjectionMatrix * gl_ModelViewMatrix * gl_Vertex + vec4(myRandom.xy * myPositionRandom,0,0);
}
uniform sampler2DRect infoTexture;
uniform sampler2DRect springIDs;

void main (){
	vec4 newSpringIDs = texture2DRect(springIDs, gl_FragCoord.xy);
	
	vec4 particleInfo1 = texture2DRect(infoTexture, newSpringIDs.xy);
	vec4 particleInfo2 = texture2DRect(infoTexture, newSpringIDs.zw);
	
	if(particleInfo1.x >= particleInfo1.y && particleInfo1.z == 0.0){
		newSpringIDs.xy = vec2(-1,-1);
	}
	if(particleInfo2.x >= particleInfo2.y && particleInfo2.z == 0.0){
		newSpringIDs.zw = vec2(-1,-1);
	}
	
	gl_FragData[0] = newSpringIDs;
	//newSpringIDs = vec4(1.0,0.0,0.0,1.0);
}
#version 120

uniform sampler2DRect infoTexture;
uniform sampler2DRect springIDs;
uniform vec2 dimension;

void main (){
	vec4 newSpringIDs = texture2DRect(springIDs, gl_FragCoord.xy);

	vec4 particleInfo1 = texture2DRect(infoTexture, newSpringIDs.xy);
	vec4 particleInfo2 = texture2DRect(infoTexture, mod(gl_FragCoord.xy,dimension));
	
	if(particleInfo1.y >= 0 && particleInfo1.x >= particleInfo1.y || particleInfo2.y >= 0 && particleInfo2.x >= particleInfo2.y){
		newSpringIDs.xy = vec2(-1,-1);
	}
	
	gl_FragColor = newSpringIDs;
	
	//newSpringIDs = vec4(1.0,0.0,0.0,1.0);
}
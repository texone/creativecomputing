#version 120 
#extension GL_ARB_texture_rectangle : enable

uniform float tanHalfFOV;

uniform sampler2DRect springs;
uniform sampler2DRect positions;
uniform sampler2DRect infos;
uniform sampler2DRect colors;

void main (){
	vec4 myIndex = texture2DRect(springs, gl_Vertex.xy);
	vec4 myValues = texture2DRect(infos, gl_Vertex.xy);
	float myAlpha = clamp(1 - myValues.x / myValues.y,0,1);
	
	vec4 myPosition;
	if(gl_MultiTexCoord0.x == 0){
		if(gl_MultiTexCoord0.y == 1 && myIndex.x < 0 || gl_MultiTexCoord0.y == 2 && myIndex.z < 0){
			myAlpha = 0;
		}
		myAlpha = 0;
		gl_FrontColor = vec4(1.0,0.0,0.0,1.0);
		myPosition = texture2DRect(positions, gl_Vertex.xy);
	} else if (gl_MultiTexCoord0.x == 1){
		if(myIndex.x < 0){
			myAlpha = 0;
		}
		gl_FrontColor = vec4(0.0,1.0,0.0,1.0);
		myPosition = texture2DRect(positions, myIndex.xy);
	} else {
	
		if(myIndex.z < 0){
			myAlpha = 0;
		}
		gl_FrontColor = vec4(0.0,0.0,1.0,1.0);
		myPosition = texture2DRect(positions, myIndex.zw);
		
	}
	
	//myPosition = texture2DRect(positions, gl_Vertex.xy);
	gl_Position = gl_ModelViewProjectionMatrix * myPosition;
	gl_TexCoord[0] = vec4(gl_Vertex.xy,0,0);
	
	// Compute point size.
	
	 
	//gl_FrontColor = texture2DRect(colors, gl_Vertex.xy) * gl_Color;
	gl_FrontColor.a *= myAlpha;// * myAlpha;
}
	           
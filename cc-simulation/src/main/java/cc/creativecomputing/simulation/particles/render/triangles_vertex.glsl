#version 120 

uniform float tanHalfFOV;

uniform sampler2DRect triangleIDs;
uniform sampler2DRect texCoords;
uniform sampler2DRect positions;
uniform sampler2DRect infos;
uniform sampler2DRect colors;

void main (){
	
	vec4 myIndex = texture2DRect(triangleIDs, gl_Vertex.xy);
	vec4 tex = texture2DRect(texCoords, gl_Vertex.xy);
	vec4 myValues = texture2DRect(infos, myIndex.xy);
	float myAlpha = clamp(1 - myValues.x / myValues.y,0,1);
	myAlpha *= 0.5;
	vec4 myPosition = texture2DRect(positions, myIndex.xy);
	/*
	//myPosition = texture2DRect(positions, gl_Vertex.xy);
	gl_Position = gl_ModelViewProjectionMatrix * myPosition;
	gl_TexCoord[0] = tex;
	float d = dist / myIndex.z;
	//gl_FrontColor = vec4(d,d,d,1);
	// Compute point size.
	*/
	 
	//gl_FrontColor = texture2DRect(colors, gl_Vertex.xy) * gl_Color;
	gl_TexCoord[0] = tex;

	gl_Position = gl_ModelViewProjectionMatrix * myPosition; 
	gl_FrontColor = vec4(1);// * myAlpha; 
}
	           
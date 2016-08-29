#version 120 
#extension GL_ARB_texture_rectangle : enable

uniform float tanHalfFOV;
uniform float pointSize;

uniform sampler2DRect springs;
uniform sampler2DRect positions;
uniform sampler2DRect infos;
uniform sampler2DRect colors;

void main (){
	vec4 myPosition = texture2DRect(positions, gl_Vertex.xy);
	gl_Position = gl_ModelViewProjectionMatrix * myPosition;
	gl_TexCoord[0] = vec4(gl_Vertex.xy,0,0);
	
	// Compute point size.
	
	vec4 myValues = texture2DRect(infos, gl_Vertex.xy);
	float myAlpha = clamp(1 - myValues.x / myValues.y * (1 - myValues.z),0,1);
	
	vec4 posViewSpace = gl_ModelViewMatrix * myPosition;
	gl_PointSize = max(tanHalfFOV / -posViewSpace.z * pointSize,1);
	 
	gl_FrontColor = texture2DRect(colors, gl_Vertex.xy) * gl_Color;
	gl_FrontColor.a *= myAlpha;//pow(myAlpha, 0.1);// * myAlpha;
	
	
}
	           
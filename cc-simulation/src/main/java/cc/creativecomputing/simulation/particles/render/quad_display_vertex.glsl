#version 120 
#extension GL_ARB_texture_rectangle : enable

uniform float tanHalfFOV;
uniform float pointSize;

uniform sampler2DRect velocities;
uniform sampler2DRect positions;
uniform sampler2DRect infos;
uniform sampler2DRect colors;

void main (){

	vec3 myVelocity = normalize(texture2DRect(velocities, gl_Vertex.xy).xyz);
	vec3 sideX = cross(myVelocity, vec3(0.0,1.0,0.0));
	vec3 sideY = cross(myVelocity, sideX);
	vec4 myPosition = texture2DRect(positions, gl_Vertex.xy) + vec4(sideX * gl_MultiTexCoord0.x,0.0) * pointSize + vec4(sideY * gl_MultiTexCoord0.y,0.0) * pointSize;
	gl_Position = gl_ModelViewProjectionMatrix * myPosition;
	gl_TexCoord[0] = vec4(gl_Vertex.xy,0,0);
	
	// Compute point size.
	
	vec4 myValues = texture2DRect(infos, gl_Vertex.xy);
	float myAlpha = clamp(1 - myValues.x / myValues.y * (1 - myValues.z),0,1);
	
	vec4 posViewSpace = gl_ModelViewMatrix * myPosition;
	gl_PointSize = max(tanHalfFOV / -posViewSpace.z * pointSize,1);
	 
	gl_FrontColor = texture2DRect(colors, gl_Vertex.xy) * gl_Color;
	gl_FrontColor.a *= pow(myAlpha, 0.1);// * myAlpha;
	
	
}
	           
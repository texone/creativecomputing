#version 120 
#extension GL_ARB_texture_rectangle : enable

uniform float _uPointSize;
uniform float tanHalfFov;

void main (){
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
	gl_TexCoord[0] = gl_MultiTexCoord0;
	
	vec4 posViewSpace = gl_ModelViewProjectionMatrix * gl_Vertex;
	//oPointSize = clamp(pointSize * tanHalfFov / -oPosition.z, minPointSize, maxPointSize);
	// Compute point size.
	
	
	float myPointSize = tanHalfFov / -posViewSpace.z * _uPointSize;
	
	gl_FrontColor = gl_Color;//min(gl_Color * myPointSize * myPointSize, gl_Color);
	
	gl_PointSize = max(myPointSize, 3);
}
	           
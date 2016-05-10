#version 120

uniform float radius;


void main(){
	
	vec4 myPosition = vec4(gl_Vertex.xyz,1.0);
	gl_Position = gl_ModelViewProjectionMatrix * myPosition;
	gl_FrontColor = gl_Color;//vec4(weights.x, weights.y, weights.z, 1.0);
	
	gl_TexCoord[0] = vec4(gl_Vertex.w,1.0,1.0,1.0);
}
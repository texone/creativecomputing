#version 120

uniform sampler2DRect field;


void main(){
	
	float height = texture2DRect (field, gl_Vertex.xy).x;
	
	vec4 position = vec4 (gl_Vertex.xy , height, 1.0);
	
	gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_Position = gl_ModelViewProjectionMatrix * position;
}
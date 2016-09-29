#version 120 
#extension GL_ARB_texture_rectangle : enable

uniform sampler2DRect data;
uniform sampler2DRect connectionData;

void main(){
	vec4 connection = texture2DRect(connectionData,gl_Vertex.xy);
	vec4 pos = texture2DRect(data,connection.xy);
	gl_Position = gl_ProjectionMatrix * gl_ModelViewMatrix * pos;
}
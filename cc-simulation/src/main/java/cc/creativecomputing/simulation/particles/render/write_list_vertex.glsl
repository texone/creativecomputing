#version 120 
#extension GL_ARB_texture_rectangle : enable

uniform float listSize;
uniform float listIndex;

uniform sampler2DRect positions;
uniform sampler2DRect infos;

void main (){

	vec4 myPosition = texture2DRect(positions, gl_Vertex.xy);
	vec4 myInfos = texture2DRect(infos, gl_Vertex.xy);
	myPosition.w = myInfos.x;
	gl_Position = gl_ModelViewProjectionMatrix * vec4(gl_Vertex.x * listSize + listIndex,gl_Vertex.yzw);
	gl_TexCoord[0] = myPosition;
}
	           
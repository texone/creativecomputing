#version 120 
#extension GL_ARB_texture_rectangle : enable

uniform float tanHalfFOV;

uniform sampler2DRect positions;
uniform sampler2DRect infos;

uniform vec2 pointSize;

uniform float alpha;

void main (){
	vec4 myPosition = texture2DRect(positions, gl_Vertex.xy);
	
	gl_Position = gl_ModelViewProjectionMatrix * myPosition;
	
	gl_Position.xy += vec2( pointSize.x * gl_Vertex.z,  pointSize.y * gl_Vertex.w);
	
	gl_TexCoord[0] = gl_MultiTexCoord0;
	
	
	vec4 myValues = texture2DRect(infos, gl_Vertex.xy);
	float myAlpha = clamp(1 - myValues.x / myValues.y,0,1);
	gl_FrontColor = gl_Color;
	gl_FrontColor.a *=  min(myAlpha + alpha,1);
}
	           
#version 120 
#extension GL_ARB_texture_rectangle : enable

uniform sampler2DRect positionTexture;

uniform float focalDistance;
uniform float focalRange;

varying vec3 normal;

void main(){
	vec4 position1 = texture2DRect(positionTexture, gl_MultiTexCoord0.zw);
	vec4 position2 = gl_Vertex;
	vec4 position3 = texture2DRect(positionTexture, gl_MultiTexCoord0.xy);
	normal = normalize(cross(position1.xyz - position2.xyz, position3.xyz - position2.xyz));
	normal = normalize(position2.xyz);
	
	gl_Position = ftransform();
}
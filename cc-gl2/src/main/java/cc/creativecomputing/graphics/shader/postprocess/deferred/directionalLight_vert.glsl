#version 120

varying vec4 cameraPosition;

uniform vec3 position;
uniform vec3 inCamPosition;

void main() {
	cameraPosition	= gl_ModelViewMatrix * vec4(inCamPosition, 1.0);
	gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_Position = ftransform();
	gl_FrontColor = gl_Color;
}
#version 120

varying vec4 lightPosition;
varying vec3 lightDirection;

uniform vec3 position;
uniform vec3 direction;

void main() {
	lightPosition	= gl_ModelViewMatrix * vec4(position, 1.0);
	lightDirection = normalize(gl_NormalMatrix * direction);
	
	gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_Position = ftransform();
	gl_FrontColor = gl_Color;
}
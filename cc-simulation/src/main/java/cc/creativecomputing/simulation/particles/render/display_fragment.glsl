#version 120 

uniform sampler2DRect infos;

void main(){
	//oColor = vec4(iColor.xyz, glColor.w * pow(myAlpha,4));
	gl_FragColor = gl_Color;
	gl_FragColor = vec4(1,1,1, 1);
}
#version 120 
#extension GL_ARB_texture_rectangle : enable

uniform float time;

void main(){
	if(gl_Color.a <= 0)discard;
	gl_FragColor = vec4(1,0,0,1);
	gl_FragColor.a = gl_Color.a * 0.3;//
	float progress = gl_Color.a * 20 + time * 0.45;
	float random = gl_TexCoord[1].x;
	progress += random * .2;
	gl_FragColor.a = 1;
	//gl_FragColor.a = smoothstep(0.75,1,fract( progress)) * 0.3 + 0.05;
}
#version 120 
#extension GL_ARB_texture_rectangle : enable



void main(){
	//if(gl_Color.a == 0)discard;
	gl_FragColor = vec4(1,1,1,0.2);
}
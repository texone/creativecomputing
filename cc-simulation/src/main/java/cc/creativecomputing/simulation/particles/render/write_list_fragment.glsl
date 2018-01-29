#version 120 


void main(){
	//oColor = vec4(iColor.xyz, glColor.w * pow(myAlpha,4));
	gl_FragColor = gl_TexCoord[0];
	//oColor = vec4(1,1,1, 1);
}
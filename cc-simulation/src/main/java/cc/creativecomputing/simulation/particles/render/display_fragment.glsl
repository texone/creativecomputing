#version 120 

uniform sampler2DRect infos;

void main(){
	vec4 myValues = texture2DRect(infos, gl_TexCoord[0].xy);
	float myAlpha = clamp(1 - myValues.x / myValues.y, 0, 1);
	//oColor = vec4(iColor.xyz, glColor.w * pow(myAlpha,4));
	gl_FragColor = gl_Color;
	//oColor = vec4(1,1,1, 1);
}
#1.5

uniform sampler3D texture;

uniform float xStart;
uniform float xEnd;

uniform float yStart;
uniform float yEnd;
//
void main(){

	float a0 = acos(gl_TexCoord[0].z);
	float a1 = asin(gl_TexCoord[0].y / sin(a0));
	a0 /= 3.1415926;
	a1 /= 2.0 * 3.1415926;
	gl_FragColor = texture3D(texture, vec3(a1, a0, gl_TexCoord[0].w));//vec4(a0, a1 , 0.0, 1.0); // vec4((gl_TexCoord[0].xyz + 1.0) / 2.0,1.0);//
	
	float u = atan(gl_TexCoord[0].x, gl_TexCoord[0].y) / (2.0 * 3.1415926) + 0.5;
	float v = asin(gl_TexCoord[0].z) / 3.1415926 + .5;
	
	gl_FragColor = vec4(gl_TexCoord[0]);
}
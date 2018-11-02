uniform float time;

float hash1(float n) {
	return fract(sin(n)*43758.5453);
}

void main(){
	gl_FragColor = vec4(1);
	float r = hash1(floor(gl_TexCoord[0].x * 10000 + time* 2));
	float r2 = hash1(floor(gl_TexCoord[0].x * 60000 + time* 12 + 10000));
	r *= step(0.3, r2) ;
	gl_FragColor.a = step(0.9,fract(gl_TexCoord[0].x * 300 - time * 0.1 ));
	gl_FragColor.rgb = vec3(1);
	gl_FragColor.a = r;// * 0.6;
}
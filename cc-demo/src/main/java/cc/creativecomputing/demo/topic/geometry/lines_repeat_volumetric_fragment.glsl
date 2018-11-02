#version 120

uniform sampler2D lineTexture;
@CCProperty(name = "centerZ", min = 0, max = 5000)
uniform float centerZ;

@CCProperty(name = "z range", min = 0, max = 5000)
uniform float zRange;

uniform float time;
uniform float radius;

float hash1(float n) {
	return fract(sin(n)*43758.5453);
}

float signal(float d){
	float rb = hash1(floor(d )- 1);
	float r = hash1(floor(d));
	float ra = hash1(floor(d )+ 1);

	
	return  (fract(d) * float(mod(d, 4) > 1));
	//return float(r > 0.9) * 0.5 * float(rb > 0.5);//mix(fract(d),0.,);
}

void main(){
	//just sample the texture
	float z = min(1, abs(gl_TexCoord[0].z - centerZ) / zRange);

	float thickness = (1-z) * 0.5 ;
	float d = 1 - distance(gl_TexCoord[0].xy + vec2(0.,0),vec2(0.5)) * 2;
	d = smoothstep(thickness,thickness + 0.1 + z,d);

	float xd = signal(gl_TexCoord[1].x * 0.01 - time*0.7);
	float range = 0.01 * radius;
	float a = smoothstep(1 - range,1.0,xd) + smoothstep(0.9 - range,0.9,1 - xd) ;
	
	vec3 rgb = mix(vec3(1), vec3(0.05,0.05,0.6),a * d);
	gl_FragColor = vec4(rgb, (a * 0.5 + 0.1) * d);//texture2D(lineTexture,gl_TexCoord[0].xy);	
	a = xd;
	//gl_FragColor = vec4(a,a,a,1);
}
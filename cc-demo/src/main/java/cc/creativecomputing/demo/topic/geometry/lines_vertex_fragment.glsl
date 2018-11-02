#version 120

@CCProperty(name = "centerZ", min = 0, max = 5000)
uniform float centerZ;

@CCProperty(name = "z range", min = 0, max = 5000)
uniform float zRange;

void main(){
	
	float z = min(1, abs(gl_TexCoord[0].z - centerZ) / zRange);

	float thickness = (1-z) * 0.75;
	float d = 1 - distance(gl_TexCoord[0].xy,vec2(0.5)) * 2;
	d = smoothstep(thickness,thickness + 0.1 + z,d);

	
	gl_FragColor = vec4(d,d,d,0.25);//texture2D(lineTexture,gl_TexCoord[0].xy);	
}
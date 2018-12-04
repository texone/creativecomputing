#version 120 
uniform float time;

@CCProperty(name = "centerZ", min = -500, max = 500)
uniform float centerZ;

@CCProperty(name = "z range", min = 0, max = 1500)
uniform float zRange;

@CCProperty(name = "z fade", min = -500, max = 500)
uniform float zFade;

@CCProperty(name = "z fade range", min = 0, max = 500)
uniform float zFadeRange;

@CCProperty(name = "col", min = 0, max = 1)
uniform vec3 col;

@CCProperty(name = "alpha", min = 0, max = 1)
uniform float alpha;

void main(){
	if(gl_Color.a <= 0)discard;

	float z = min(1, abs(gl_TexCoord[0].z - centerZ) / zRange);

	float thickness = (1-z) * 0.75;
	float d = 1 - distance(gl_TexCoord[0].xy,vec2(0.5)) * 2;
	d = smoothstep(thickness,thickness + 0.1 + z,d);

	
	gl_FragColor = vec4(col, d * alpha * gl_Color.a);//texture2D(lineTexture,gl_TexCoord[0].xy);	


	float depthFade = smoothstep(zFade - zFadeRange , zFade, gl_TexCoord[0].z);
	
	gl_FragColor.a *= depthFade;
	//gl_FragColor.a = 0.1;

	//gl_FragColor = vec4(gl_TexCoord[0].xy, 0, 1);
	
}
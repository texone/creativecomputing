#version 120 

@CCProperty(name = "centerZ", min = -500, max = 500)
uniform float centerZ;

@CCProperty(name = "z range", min = 0, max = 1500)
uniform float zRange;

@CCProperty(name = "z fade", min = -500, max = 500)
uniform float zFade;

@CCProperty(name = "z fade range", min = 0, max = 500)
uniform float zFadeRange;

@CCProperty(name = "alpha2", min = 0, max = 1)
uniform float alpha2;

uniform vec3 mouse;

void main(){
	float z = min(1, abs(gl_TexCoord[0].z - centerZ) / zRange);
	float thickness = (1-z) * 0.175;
	
	float d = 1 - (distance(gl_TexCoord[0].xy,vec2(0.5)) * 2);
	d = smoothstep(thickness,thickness + 0.1 + z*0.7,d);
	gl_FragColor = vec4(gl_Color.rgb, d * alpha2 * gl_Color.a);
	float depthFade = smoothstep(zFade - zFadeRange , zFade, gl_TexCoord[0].z);
	float mouseFade = distance(gl_FragCoord.xy, mouse.xy) / (1500);
	gl_FragColor.a *= depthFade * mix(1,smoothstep(0.0,1,mouseFade),min(1,mouse.z ));
}
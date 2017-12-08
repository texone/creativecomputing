uniform vec4 diffuse;
uniform vec4 ambient;
uniform vec4 specular;
uniform float shininess;

uniform vec3 lightDir;

varying vec3 normal;

void main(){
	float NdotL = max(0.0,dot(normalize(normal), lightDir));
	gl_FragColor = NdotL * diffuse + ambient;
	//gl_FragColor.xyz = normalize(normal);
	//gl_FragData[0].w = 1.0;
	//gl_FragColor = vec4((normal.xyz + 1.0)/2.0,1.0);
	//gl_FragColor = vec4(1.0,0.0,0.0,1.0);
	//gl_FragData[1] = vec4(blur);
	//gl_FragData[1].w = 1.0;
}
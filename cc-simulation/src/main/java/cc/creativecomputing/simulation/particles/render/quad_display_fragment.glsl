#version 120 
#extension GL_ARB_texture_rectangle : enable


varying vec3 vPosition;
varying vec3 vNormal;
@CCProperty(name = "lightpos", min = -100, max = 100)
uniform vec3 vLightPosition;
uniform vec3 cameraPosition;



void main(){
	
	vec3 L = normalize( vLightPosition - vPosition );
	vec3 E = normalize( cameraPosition - vPosition );

	float diffuse = max( 0., abs(dot( L, vNormal )) );

	vec4 vColor = gl_Color;
	
	float shininess = 200.;
	vec3 halfVector = normalize(E + L );
	float specular = dot(vNormal, halfVector);
	specular = max(0.0, specular);
	specular = pow(specular, shininess);
	float ambient = .2;
	float o = diffuse;
	vec3 color = mix( vColor.rgb, vec3( 1. ), .8 * clamp( -vNormal.y, 0., 1. ) );
	vec3 diffuseColor = color * mix( vec3( o ), vec3( 1. ), ambient );
	vec3 specularColor = vec3( 1. );
	vec3 base = mix( diffuseColor, specularColor, specular * o );
	
	gl_FragColor = gl_Color;
	gl_FragColor.rgb = vec3(1.);//base.rgb;
}
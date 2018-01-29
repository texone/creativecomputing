#version 120
#extension GL_ARB_texture_rectangle : enable

uniform sampler2DRect TEXTURE;
uniform sampler2DRect REFRACT;

//uniform sampler2D TEXTURE;
//uniform sampler2D REFRACT;


uniform float depth;
uniform float scaleX;
uniform float scaleY;


void main() {

	vec2 coords = gl_TexCoord[0].xy;
	
	/*
	vec4 tex0 = vec4(0.0, 0.0, 0.0, 0.0);
	vec4 tex0 = texture2DRect (TEXTURE, coords*vec2(scaleX, scaleY));
	vec4 tex1 = texture2DRect (REFRACT, coords);
	*/
	
	
	vec2 above = texture2DRect( REFRACT, coords + vec2 (0.0, -1.0) ).rg;
	float x = above.g - texture2DRect( REFRACT, coords + vec2 (1.0, 0.0)).g;
	float y = above.r - texture2DRect( REFRACT, coords + vec2 (0.0, 1.0)).r;	
	
	
	
	//gl_FragColor = vec4 (tex0.rgb+tex1.rgb, 1.0);
	gl_FragColor = texture2DRect (TEXTURE, (coords + vec2(x,y)*100)*vec2(scaleX, scaleY));
}


/*
void main() {

	vec2 coordsRect = gl_TexCoord[0].xy + vec2(0, 64.0);
	vec2 coords = gl_TexCoord[0].xy / 64.0 + vec2(0.0, 1.0);
	vec2 pixel = vec2(1.0, 1.0);
	
	//Calculate refraction
	vec2 above = texture2DRect( REFRACT, coordsRect + vec2( 0.0, -pixel.y ) ).rg;
	float x = above.g - texture2DRect( REFRACT, coords + vec2( pixel.x, 0.0 ) ).g / 64.0;
	float y = above.r - texture2DRect( REFRACT, coords + vec2( 0.0, pixel.y ) ).r / 64.0;
	
	
	// Sample the texture from the target position
	gl_FragColor = texture2D( TEXTURE, coords + vec2( x, y ) );
	//gl_FragColor = vec4(x, y, 0.0, 1.0);
}
*/
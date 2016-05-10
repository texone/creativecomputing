#version 120
#extension GL_ARB_texture_rectangle : enable

uniform sampler2DRect IN0;
uniform sampler2DRect OUT1;
uniform sampler2DRect OUT2;

uniform float damping = 1.0;
uniform float speed   = 1.0;


const float power = 1.5;


/*
void main() {
	vec2 coords = gl_TexCoord[0].xy;
	
	vec3 sum = vec3 (0.0, 0.0, 0.0);
	
	sum = sum + texture2DRect(OUT2, coords+vec2(-1,0)).rgb
			  + texture2DRect(OUT2, coords+vec2(+1,0)).rgb
			  + texture2DRect(OUT2, coords+vec2(0,-1)).rgb
			  + texture2DRect(OUT2, coords+vec2(0,+1)).rgb;
	
	sum = (sum/2.0 + texture2DRect(OUT1, coords).rgb) * damping;	
	sum += texture2DRect(IN0, coords).rgb;
	
	gl_FragData[0] = vec4 (sum, 1.0);
}
*/

//Samples velocity from neighbor
float getSpring( float height, vec2 position, float factor ) {
	return ( texture2DRect( OUT1, position ).r - height ) * factor;
}

void main() {

	// coords
	vec2 uv = gl_TexCoord[0].xy;
	
	// Kernel size
	vec2 kernel = vec2(1.0, 1.0) * speed; //pixel * speed;
  
	// Sample the color to get the height and velocity of this pixel
	vec4 color = texture2DRect( OUT1, uv );
	float height = color.r;
	float vel = color.g;
	
	// Sample neighbors to update this pixel's velocity. Sampling inside of for() loops
	// is very slow, so we write it all out.
	vel += getSpring( height, uv + kernel * vec2( 2.0, 3.0 ), 0.0022411859348636983 * power );
	vel += getSpring( height, uv + kernel * vec2( 0.0, 3.0 ), 0.0056818181818181820 * power );
	vel += getSpring( height, uv + kernel * vec2( -2.0, 3.0 ), 0.0022411859348636983 * power );
	vel += getSpring( height, uv + kernel * vec2( 2.0, 2.0 ), 0.0066566640639421000 * power );
	vel += getSpring( height, uv + kernel * vec2( 0.0, 2.0 ), 0.0113636363636363640 * power );
	vel += getSpring( height, uv + kernel * vec2( -2.0, 2.0 ), 0.0066566640639421000 * power );
	vel += getSpring( height, uv + kernel * vec2( 3.0, 1.0 ), 0.0047597860217705710 * power );
	vel += getSpring( height, uv + kernel * vec2( 1.0, 1.0 ), 0.0146919683956074150 * power );
	vel += getSpring( height, uv + kernel * vec2( -1.0, 1.0 ), 0.0146919683956074150 * power );
	vel += getSpring( height, uv + kernel * vec2( -3.0, 1.0 ), 0.0047597860217705710 * power );
	vel += getSpring( height, uv + kernel * vec2( 2.0, 0.0 ), 0.0113636363636363640 * power );
	vel += getSpring( height, uv + kernel * vec2( -2.0, 0.0 ), 0.0113636363636363640 * power );
	vel += getSpring( height, uv + kernel * vec2( 3.0, -1.0 ), 0.0047597860217705710 * power );
	vel += getSpring( height, uv + kernel * vec2( 1.0, -1.0 ), 0.0146919683956074150 * power );
	vel += getSpring( height, uv + kernel * vec2( -1.0, -1.0 ), 0.0146919683956074150 * power );
	vel += getSpring( height, uv + kernel * vec2( -3.0, -1.0 ), 0.0047597860217705710 * power );
	vel += getSpring( height, uv + kernel * vec2( 2.0, -2.0 ), 0.0066566640639421000 * power );
	vel += getSpring( height, uv + kernel * vec2( 0.0, -2.0 ), 0.0113636363636363640 * power );
	vel += getSpring( height, uv + kernel * vec2( -2.0, -2.0 ), 0.0066566640639421000 * power );
	vel += getSpring( height, uv + kernel * vec2( 2.0, -3.0 ), 0.0022411859348636983 * power );
	vel += getSpring( height, uv + kernel * vec2( 0.0, -3.0 ), 0.0056818181818181820 * power );
	vel += getSpring( height, uv + kernel * vec2( -2.0, -3.0 ), 0.0022411859348636983 * power );
	
	// Update this pixel's height (red channel)
	height += vel;
	
	// Reduce the velocity
	vel *= damping;
	
	vec2 inp = texture2DRect(IN0, uv).rg;
	// Store the height and velocity in the red and green channels
	
	float amplitude = height + inp.x;
	if (amplitude > 1.0) {
		amplitude = 1.0;
	}
	
	gl_FragColor = vec4( height+inp.x, vel, 0.0, 1.0 );
}
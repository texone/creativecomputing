const vec2 Diffusion = vec2(0.08,0.03);
//const float F = 0.04;
//const float k = 0.06;
uniform float dt;

uniform sampler2DRect tex;

uniform vec2 iResolution;

uniform int reset;

float rand(vec2 co){
	// implementation found at: lumina.sourceforge.net/Tutorials/Noise.html
	return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

// nine point stencil
vec2 laplacian(vec2 position) {
    vec4 P = vec4(1.0, 1.0, 0.0, -1.0);
	return  
	0.5 * texture2DRect( tex,  position - P.xy ).xy + // first row
	1.0 * texture2DRect( tex,  position - P.zy ).xy +
	0.5 * texture2DRect( tex,  position - P.wy ).xy +
	1.0 * texture2DRect( tex,  position - P.xz ).xy - // second row
	6.0 * texture2DRect( tex,  position        ).xy +
	1.0 * texture2DRect( tex,  position + P.xz ).xy +
	0.5 * texture2DRect( tex,  position + P.wy ).xy +  // third row
	1.0 * texture2DRect( tex,  position + P.zy ).xy +
	0.5 * texture2DRect( tex,  position + P.xy ).xy;	
}

uniform float uFScale; 
uniform float uUScale;

uniform float uUOffset;
uniform float uFOffset;

void main(){
    vec2 uv = gl_FragCoord.xy / iResolution.xy;
    
    gl_FragColor = vec4(0.0,0.0,0.0,1.0);
    if(reset == 1) {
        float rnd = rand(uv) + (sin(50.*uv.x)+sin(50.*uv.y))*0.;
        if(rnd>0.5) gl_FragColor.x=.5;
        else gl_FragColor.y=.5;
        return;
    }
    
    float F = uv.y * uFScale + uFOffset;
 
    float k = uv.x * uUScale + uUOffset;
    
    
    vec4 data = texture2DRect(tex, gl_FragCoord.xy);
    
    float u = data.x;
    float v = data.y;
    
    vec2 Duv = laplacian(gl_FragCoord.xy) * Diffusion; 
    float du = Duv.x - u * v * v + F * (1.-u);
    float dv = Duv.y + u * v * v - (F + k) * v;
    gl_FragColor.xy = clamp(vec2(u + du * dt,v + dv * dt), 0., 1.);
}



uniform float dt;

uniform sampler2DRect dataBuffer;

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
	0.5 * texture2DRect( dataBuffer,  position - P.xy ).xy + // first row
	1.0 * texture2DRect( dataBuffer,  position - P.zy ).xy +
	0.5 * texture2DRect( dataBuffer,  position - P.wy ).xy +
	1.0 * texture2DRect( dataBuffer,  position - P.xz ).xy - // second row
	6.0 * texture2DRect( dataBuffer,  position        ).xy +
	1.0 * texture2DRect( dataBuffer,  position + P.xz ).xy +
	0.5 * texture2DRect( dataBuffer,  position + P.wy ).xy +  // third row
	1.0 * texture2DRect( dataBuffer,  position + P.zy ).xy +
	0.5 * texture2DRect( dataBuffer,  position + P.xy ).xy;	
}

uniform float drawParameterSpace;

uniform sampler2D interpolationMap;
uniform float kBlend;
uniform float fBlend;

uniform vec2 Diffusion;
uniform float vScale;

void main(){
    vec2 uv = gl_FragCoord.xy / iResolution.xy;
    
    gl_FragColor = vec4(0.0,0.0,0.0,1.0);
    
    if(reset == 1) {
        float rnd = rand(uv) + (sin(50.*uv.x)+sin(50.*uv.y))*0.;
        if(rnd>0.5) gl_FragColor.x=.5;
        else gl_FragColor.y=.5;
        return;
    }
    
    float k = uv.x * 0.1;// + 0.0;
    float F = uv.y * 0.1;// + 0.0;
    
    vec2 kf = texture2D(interpolationMap, vec2(kBlend, fBlend)).xy * 0.1;
    
    k = kf.x;
    F = kf.y;
    
    if(drawParameterSpace == 1.0){
    	k = uv.x * 0.1;
    	F = uv.y * 0.1;
    }
    
    
    vec4 data = texture2DRect(dataBuffer, gl_FragCoord.xy);
    
	float u = data.x;
	float v = data.y;
	float uvv	= u * v * v;
    
	vec2 Duv = laplacian(gl_FragCoord.xy) * Diffusion; 
	float du = Duv.x - uvv + F * (1.0 - u);
	float dv = Duv.y + uvv - (F + k) * v;

	u += du * dt;
	v += dv * dt * vScale;
    gl_FragColor.xy = clamp(vec2(u,v * (1.0 - data.z)), 0., 1.); 
    gl_FragColor.z = data.z;
}




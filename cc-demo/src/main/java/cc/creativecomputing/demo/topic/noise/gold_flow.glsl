//  Blackbody Lava, based on fizzer's Flowing Lava - https://www.shadertoy.com/view/4djSzR
//  
//  ~bj.2014

#define NEW_LAVA 0
#define TEMPERATURE 2200.0

float fbm(vec2 p);

float hash(float n){
    n=mod(n,1024.0);
    return fract(sin(n)*43758.5453);
}

float noise(vec2 p){
    return hash(p.x + p.y*57.0);
}

float smoothNoise2(vec2 p){
	vec2 p0 = floor(p + vec2(0.0, 0.0));
	vec2 p1 = floor(p + vec2(1.0, 0.0));
	vec2 p2 = floor(p + vec2(0.0, 1.0));
	vec2 p3 = floor(p + vec2(1.0, 1.0));
	vec2 pf = fract(p);
	
	return mix(
		mix(noise(p0), noise(p1), pf.x),
		mix(noise(p2), noise(p3), pf.x), 
		pf.y
	);
}



uniform sampler2D iChannel0;

uniform float iTime;
uniform vec2 iResolution;



float moveSpeed=0.75;
float time;

float cubic(float x){
    return (3.0 * x - 2.0 * x * x) * x;
}

vec3 rotateX(float angle, vec3 v){
    return vec3(v.x, cos(angle) * v.y + sin(angle) * v.z, cos(angle) * v.z - sin(angle) * v.y);
}

vec3 rotateY(float angle, vec3 v){
    return vec3(cos(angle) * v.x + sin(angle) * v.z, v.y, cos(angle) * v.z - sin(angle) * v.x);
}

@CCProperty(name = "edge Noise", min = 0, max = 0.5)
uniform float edgeNoise;
@CCProperty(name = "edge Range", min = 0, max = 1.5)
uniform float edgeRange;

float heightField(vec2 p){
	float range = abs(p.x);
	float result = (1. - cos(range * edgeRange));
	result += smoothstep(0.0,0.7,1.0-smoothstep(0.0,.9,smoothNoise2(p))) * edgeNoise;
	return result* step(0.,range);
}


float fbm(vec2 p){
    float f=0.0;
    for(int i=0;i<4;i+=1)
        f+=smoothNoise2(p*exp2(float(i)))/exp2(float(i+1));
    return f;
}

float bumpHeight(vec2 p){
    float f=0.0;
    p*=4.0;
    for(int i=0;i<5;i+=1)
        f+=smoothNoise2(p*exp2(float(i)))/exp2(float(i+1));
    return f*0.15;
}

vec3 bumpNormal(vec2 p){
    vec2 eps=vec2(1e-5,0.0);
    float bumpScale=10.0;
    float c=bumpHeight(p);
    float d0=(bumpHeight(p+eps.xy))-c;
    float d1=(bumpHeight(p+eps.yx))-c;
    return normalize(cross(vec3(eps.y,d1,eps.x),vec3(eps.x,d0,eps.y)));
}

vec3 heightFieldNormal(vec2 p){
    vec2 eps=vec2(1e-1,0.0);
    float bumpScale=10.0;
    float c=heightField(p);
    float d0=(heightField(p+eps.xy))-c;
    float d1=(heightField(p+eps.yx))-c;
    vec3 n0 = normalize(cross(vec3(eps.y,d1,eps.x),vec3(eps.x,d0,eps.y)));
    vec3 bn = bumpNormal(p);
    return normalize(n0+(bn-n0*dot(n0,bn))*0.2);
}

vec3 tonemap(vec3 c){
    return c/(c+vec3(0.6));
}

@CCProperty(name = "progress", min = -10, max = 10)
uniform float progress;
@CCProperty(name = "bow", min = 0, max = 1)
uniform float bow;
@CCProperty(name = "start glow", min = 0, max = 2)
uniform float startGlow;

float progress(float x){
	return sin(x + 1.6) * bow + progress;
}

float evalLavaHeight(vec2 p){
    return mix(-0.5,0.2,cubic(clamp(1.0-(-p.y-time*moveSpeed) * 0.6 + progress(p.x),0.,1.0)));
}

@CCProperty(name = "gold", min = 0, max = 1)
uniform float gold;
@CCProperty(name = "liquid", min = 0, max = 0.2)
uniform float liquid;

@CCProperty(name = "cold", min = 0, max = 2)
uniform float cold;
@CCProperty(name = "glow", min = 0, max = 2)
uniform float glow;
@CCProperty(name = "haze", min = 0, max = 2)
uniform float haze;

@CCProperty(name = "haze color", min = 0, max = 1)
uniform vec3 hazeColor;
@CCProperty(name = "gold color", min = 0, max = 4)
uniform vec3 goldColor;

vec3 blackbody(float t){
    t *= TEMPERATURE;
    
    float u = ( 0.860117757 + 1.54118254e-4 * t + 1.28641212e-7 * t*t ) 
            / ( 1.0 + 8.42420235e-4 * t + 7.08145163e-7 * t*t );
    
    float v = ( 0.317398726 + 4.22806245e-5 * t + 4.20481691e-8 * t*t ) 
            / ( 1.0 - 2.89741816e-5 * t + 1.61456053e-7 * t*t );

    float x = goldColor.x * u / (2.0*u - 8.0*v + 4.0);
    float y = goldColor.y * v / (2.0*u - 8.0*v + 4.0);
    float z = goldColor.z - x - y;
    
    float Y = goldColor.y;
    float X = Y / y * x;
    float Z = Y / y * z;

    mat3 XYZtoRGB = mat3(3.2404542, -1.5371385, -0.4985314,
                        -0.9692660,  1.8760108,  0.0415560,
                         0.0556434, -0.2040259,  1.0572252);

    return max(vec3(0.0), (vec3(X,Y,Z) * XYZtoRGB) * pow(t * 0.0004, 4.0));
}

vec3 _sample(vec2 coord){
	// Set up ray.
	vec3 ro=vec3(0.0, 3.0, -2.0 - time * moveSpeed * 1.);
	vec3 rd=rotateY(3.1415926 / 2.,rotateX(3.1415926 / 2.,normalize(vec3(coord,-.7))));

	// Intersect the ray with the upper and lower planes of the heightfield.
	float t0 = (0.5-ro.y) / rd.y;
	float t1 = (0.0-ro.y) / rd.y;

	const int n = 2;

	float lavaHeight=0.0;

	vec3 prevp = ro + rd * t0, p = prevp;
	float ph=heightField(prevp.xz);

	// Raymarch through the heightfield with a fixed number of steps.
    
	float pt = t0;
	float t = mix(t0,t1,0.5);
	p=ro+rd*t;
	lavaHeight=evalLavaHeight(p.xz);
	float h=max(lavaHeight,heightField(p.xz) ) ;

	if(h>p.y ){
		// Refine the intersection point.
		float lrd=length(rd.xz);
		vec2 v0 = vec2(lrd * pt, prevp.y);
		vec2 v1 = vec2(lrd * t, p.y);
		vec2 v2 = vec2(lrd * pt, ph);
		vec2 dv = vec2(h - v2.y, v2.x - v1.x);
		float inter = dot(v2 - v0,dv) / dot(v1 - v0,dv);
		p=mix(prevp,p,inter);

		// Re-evaluate the lava height using the refined intersection point.
		lavaHeight = evalLavaHeight(p.xz) ;
		
	}
	prevp=p;
	ph=h;
    

	lavaHeight *= 1.2;

	vec3 norm = heightFieldNormal(p.xz);
    
	// Base colour for the rocks.
	float f0 = sqrt(fbm(p.xz*0.5));
	vec3 diffuse= mix(vec3(0.),vec3(1.0,0.8,0.6)*.5,f0)*mix(0.9,0.,p.y) * mix(0.2,.7,fbm(p.xz*5.0));

    float mask = max(0.0, 1.0 - abs(lavaHeight - p.y) * 16.0) * 1. ;
    
    vec2 uv = p.zx * 1. + (fbm(p.zx * 2.1 + iTime * 0.2) * 2. - 1.) * 1. * mask;
    uv.x -= mask * lavaHeight + p.y;
    float texDeform = max(0.,cos(abs(coord.y) * 15.)) * 2.;
    float tex = gold - texture2D(iChannel0, uv * vec2(1.0,mix(0.01,0.1,1. - texDeform))).x * liquid ;
    
  
    float cold = smoothstep(0.0, 1.0, p.z + time * moveSpeed + progress(p.x) + startGlow) * cold;
    float glow = max(0.0, (1.0-mask)*4.0 * (0.1 - (p.y - lavaHeight) * (f0 * 1.5 - 0.5) * f0)) * glow;
    float haze = length(ro - p) * 0.025 * cold * haze;
    
    float temp = (2.8 * tex - cold) * tex;
    temp = mix(glow * 1.2, smoothstep(0.0, 1.5, temp) * 2.0, mask);

    //return vec3(max(0.,cos(abs(coord.y) * 5.) * 3.));
    return diffuse * (1.0-mask) 
                   + blackbody(temp) * vec3(2.6, 0.8, 0.5)  
                   + haze * hazeColor;

}


void main(  )
{
    time=iTime;
    // Sample the scene, with a distorted coordinate to simulate heat haze.
    vec2 uv = gl_FragCoord.xy / iResolution.xy;
    uv = (uv - vec2(0.5)) * 2.0;
    uv.x *= iResolution.x / iResolution.y;
    gl_FragColor.rgb=_sample(uv+vec2(cos(smoothNoise2(vec2(-time*10.0+uv.y*10.0,uv.x)))*0.01,0.0));
    gl_FragColor.rgb=tonemap(gl_FragColor.rgb)*1.2;
    gl_FragColor.a = 1.;
}


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

vec2 hash( vec2 x ){
    const vec2 k = vec2( 0.3183099, 0.3678794 );
    x = x*k + k.yx;
    return -1.0 + 2.0*fract( 16.0 * k*fract( x.x*x.y*(x.x+x.y)) );
}

float gnoise( in vec2 p ){
    vec2 i = floor( p );
    vec2 f = fract( p );
	
	vec2 u = f*f*(3.0-2.0*f);

    return mix( mix( dot( hash( i + vec2(0.0,0.0) ), f - vec2(0.0,0.0) ), 
                     dot( hash( i + vec2(1.0,0.0) ), f - vec2(1.0,0.0) ), u.x),
                mix( dot( hash( i + vec2(0.0,1.0) ), f - vec2(0.0,1.0) ), 
                     dot( hash( i + vec2(1.0,1.0) ), f - vec2(1.0,1.0) ), u.x), u.y);
}


uniform sampler2D iChannel0;

uniform float iTime;
uniform vec2 iResolution;



float moveSpeed=0.75;
float time;

float cubic(float x){
    return (3.0 * x - 2.0 * x * x) * x;
}


@CCProperty(name = "edge Noise", min = 0, max = 0.5)
uniform float edgeNoise;
@CCProperty(name = "edge Range", min = 0, max = 1.5)
uniform float edgeRange;
@CCProperty(name = "range", min = 0, max = 1.)
uniform float range;


float heightField(vec2 p){
	float range = abs(p.x);
	float result = (1. - cos(range * edgeRange));
	result += smoothstep(0.0,0.7,1.0-smoothstep(0.0,.9,smoothNoise2(p))) * edgeNoise;
	return result* step(0.,range);
}


float fbm(vec2 p){
    float f=0.0;
    for(int i=0;i<5;i+=1)
        f+=smoothNoise2(p * exp2(float(i)))/exp2(float(i+1));
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

@CCProperty(name = "progress", min = -1, max = 5)
uniform float progress;
@CCProperty(name = "bow", min = 0, max = 1)
uniform float bow;
@CCProperty(name = "start glow", min = 0, max = 2)
uniform float startGlow;

float progress(float x){
	return sin(x + 1.6) * bow + progress;
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
@CCProperty(name = "background", min = 0, max = 2)
uniform float background;

@CCProperty(name = "haze color", min = 0, max = 1)
uniform vec3 hazeColor;
@CCProperty(name = "gold color", min = 0, max = 4)
uniform vec3 goldColor;

vec3 blackbody(float t){
    t *= TEMPERATURE;
    
    float u = ( 0.860117757 + 1.54118254e-4 * t + 1.28641212e-7 * t*t ) 
            / ( 2.1 + 8.42420235e-4 * t + 7.08145163e-7 * t*t );
    
    float v = ( 0.317398726 + 4.22806245e-5 * t + 4.20481691e-8 * t*t ) 
            / ( 1.0 - 2.89741816e-5 * t + 1.61456053e-7 * t*t );

    float x = goldColor.x * u / (2.0 * u - 8.0 * v + 4.0);
    float y = goldColor.y * v / (2.0 * u - 8.0 * v + 4.0);
    float z = goldColor.z - x - y;
    
    float Y = goldColor.y;
    float X = Y / y * x;
    float Z = Y / y * z;

    mat3 XYZtoRGB = mat3(3.2404542, -1.5371385, -0.4985314,
                        -0.9692660,  1.8760108,  0.0415560,
                         0.0556434, -0.2040259,  1.0572252);

    return max(vec3(0.0), (vec3(X,Y,Z) * XYZtoRGB) * pow(t * 0.0004, 4.0));
}

#define PI 3.1415926535897932384626433832795

vec3 sample(vec2 coord, float mask){
	float lavaHeight = 0.24;

	// Base colour for the rocks.
	float f0 = sqrt(fbm(coord*0.5));
	float blend = -cos(coord.y * PI * 4.);
	blend = clamp(blend, 0.0,1.0);
	if(abs(coord.y) > 0.2){
		blend = 1.;
	}
	vec3 diffuse= vec3(1.0,0.8,0.6) * background * mix(0.9,0.,blend) * mix(0.2,.7,fbm(coord * 15.0));

	float myProgress = progress + cos(coord.y * 4.) * bow;
    
	vec2 uv = coord + (fbm(coord * 6.1 + iTime * 0.1) * 2. - 1.) * 1. * mask;
	//uv.x -= mask * lavaHeight + p.y;
	float texDeform = max(0.,cos(abs(coord.y) * 15.)) * 2.;
	float tex = gold - texture2D(iChannel0, uv * vec2(1.0,mix(0.01,0.1,1. - texDeform))).x * liquid ;
    
	float cold = cold + smoothstep( myProgress - .1,myProgress-1.5-.1,coord.x) * startGlow;
	float glow =  0.;//max(0.0, (1.0-mask)*4.0 * (0.1 - (abs(coord.y) - lavaHeight) * (f0 * 1.5 - 0.5) * f0)) * glow;
	float haze = 1. * 0.025 * cold * haze;
    
	float temp = (2.8 * tex - cold) * tex;
	temp = mix(glow * 1.2, smoothstep(0.0, 1.5, temp) * 2.0, mask);

    return diffuse * (1.0-mask) 
                   + blackbody(temp)// * vec3(0.8, 0.8, 0.5)  
                   + haze * hazeColor;

}

@CCProperty(name = "circle pos", min = 0, max = 3000)
uniform vec2 circlePos;
@CCProperty(name = "circle radius", min = 0, max = 1000)
uniform float circleRadius;
@CCProperty(name = "circle blend radius", min = 0, max = 1000)
uniform float circleBlendRadius;

float circle(vec2 uv, vec2 pos, float rad, float blend) {
	float d = length(vec2(pos.x - rad,pos.y) - uv) - gnoise(uv * 0.03) * 10.2;
	//d = min(280.,d);
	float t = clamp(d, 0.0, 1.0);
	//return 1.0 - t;
	//float gradient = smoothstep(pos.x - rad, pos.x + rad, );
	return 1.0 - smoothstep(rad - blend, rad,d);
}

@CCProperty(name = "rect0 pos", min = 0, max = 3000)
uniform vec2 rect0Pos;
@CCProperty(name = "rect0 size", min = 0, max = 1000)
uniform vec2 rect0Size;
@CCProperty(name = "rect0 radius", min = 0, max = 1000)
uniform float rect0Radius;
@CCProperty(name = "rect0 blend radius", min = 0, max = 1000)
uniform float rect0BlendRadius;

float roundRect(vec2 uv, vec2 pos, in vec2 halfSize, float rad, float blend){
	//length(max(abs(uv - pos) - (halfSize - rad), vec2(0.0))) - rad
	return 1.0 - smoothstep( 0., 1., length(max(abs(uv - pos) - (halfSize), vec2(0.0))));
}



void main(){
	// Sample the scene, with a distorted coordinate to simulate heat haze.
    	vec2 uv = gl_FragCoord.xy / iResolution.xy;
    	uv = (uv - vec2(0.5)) * 2.0;
    	uv.x *= iResolution.x / iResolution.y;
    	
    	
	float shape = circle(gl_FragCoord.xy, circlePos, circleRadius,circleBlendRadius);
	//shape = max(shape,roundRect(gl_FragCoord.xy,rect0Pos,rect0Size / 2.0, rect0Radius, rect0BlendRadius));

	float myProgress = progress + (cos(uv.y * 4.) * bow + fbm(vec2(uv.x ,uv.y * 3.5)) * 0.2 ) ;
	float min = range - edgeRange;
	vec2 coord = uv;
	coord.y += (fbm(vec2(coord * 10.5)) * 2. - 1.) * edgeNoise;
	shape = max(shape,
		smoothstep(-range,-min,coord.y) * 
		smoothstep( range, min,coord.y) *  
		float(coord.x > -0.9) *
		smoothstep( myProgress,myProgress-0.1,coord.x)
	);
	
    	time=iTime;
    	
    	gl_FragColor.rgb = sample(uv, shape);//+vec2(cos(smoothNoise2(vec2(-time * 10.0 + uv.y * 10.0,uv.x))) * 0.01,0.0));
    	gl_FragColor.rgb = tonemap(gl_FragColor.rgb) * 1.2;
    	gl_FragColor.a = 1.;

    	
   	float blend = float(uv.y < 0.5 && uv.y > -0.5) * (cos(uv.y * 2. * PI ) + 1.) / 2.;
   	
	vec3 diffuse= vec3(1.0,0.8,0.6) * .4 * shape * mix(0.2,.7,fbm(uv * 10.0 * vec2(1.0)));

	//gl_FragColor.rgb = diffuse;
    	//gl_FragColor = vec4(shape);
}


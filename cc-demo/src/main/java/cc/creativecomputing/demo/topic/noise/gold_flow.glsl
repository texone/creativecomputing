
float hash(float n){
    n=mod(n,1024.0);
    return fract(sin(n)*43758.5453);
}

float noise(vec3 p){
    return hash(p.x + p.y * 57.0 + p.z * 213.);
}

float smoothNoise2(vec3 p){
	vec3 p000 = floor(p + vec3(0.0, 0.0, 0.0));
	vec3 p100 = floor(p + vec3(1.0, 0.0, 0.0));
	vec3 p010 = floor(p + vec3(0.0, 1.0, 0.0));
	vec3 p110 = floor(p + vec3(1.0, 1.0, 0.0));
	
	vec3 p001 = floor(p + vec3(0.0, 0.0, 1.0));
	vec3 p101 = floor(p + vec3(1.0, 0.0, 1.0));
	vec3 p011 = floor(p + vec3(0.0, 1.0, 1.0));
	vec3 p111 = floor(p + vec3(1.0, 1.0, 1.0));

	
	vec3 pf = fract(p);
	
	return mix(
		mix(
			mix(noise(p000), noise(p100), pf.x),
			mix(noise(p010), noise(p110), pf.x), 
			pf.y
		),
		mix(
			mix(noise(p001), noise(p101), pf.x),
			mix(noise(p011), noise(p111), pf.x), 
			pf.y
		),
		pf.z
	);
}

float fbm(vec3 p){
    float f=0.0;
    for(int i=0;i<5;i+=1)
        f+=smoothNoise2(p * exp2(float(i)))/exp2(float(i+1));
    return f;
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



vec3 tonemap(vec3 c){
    return c/(c+vec3(0.6));
}

@CCProperty(name = "gold color", min = 0, max = 4)
uniform vec3 goldColor;

#define TEMPERATURE 2200.0
vec3 blackbody(float t){
    t *= TEMPERATURE;
    
    float u = ( 0.860117757 + 1.54118254e-4 * t + 1.28641212e-7 * t * t ) 
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

	vec3 myResult = max(vec3(0.0), (vec3(X,Y,Z) * XYZtoRGB) * pow(t * 0.0004, 4.0));
	myResult = tonemap(myResult) * 1.2;
    	return myResult;
}



vec3 gold(float d){
	d -= 0.25;
	d *= 2.;
	//d = 1. - d;
	//d = smoothstep(0.0,1.0,d);
	d = clamp(d,0.0,1.0);
	vec3 col = vec3(.3,0.2,0.07)  /  d ;
	col = smoothstep(0.0,1.0,col);
	//col -= 0.1;
	//col *= 1.1;
	col = clamp(col,0.0,1.0);
	return col;
	return vec3(d);
}

#define PI 3.1415926535897932384626433832795

@CCProperty(name = "heat", min = 0, max = 1)
uniform float heat;
@CCProperty(name = "liquid", min = 0, max = 1.)
uniform float liquid;

@CCProperty(name = "gold blend", min = 0, max = 1)
uniform float goldBlend;

uniform sampler2D typo;
uniform sampler2D iChannel0;

vec3 sample(vec2 coord, float noiseVal,float mask, float heat){

	vec2 uv = coord + noiseVal * 1. * mask;
	
	
	float tex = texture2D(iChannel0, uv * vec2(1.,mix(0.01,.1,mask))).x;
	float mtex = heat * 0.5 + 0.5 - tex * liquid ;
    
	float temp = 2. * mtex * mtex *  mask;
	temp = smoothstep(0.0, 1.5, temp) * 3.0 ;

	vec3 gold = gold(tex ) ;
	vec3 melt = blackbody(temp);
    	//return vec3(d,d,d);
    	return mix(gold, melt, goldBlend)* mask;
}

@CCProperty(name = "blend radius", min = 0, max = 100)
uniform float blendRadius;
@CCProperty(name = "circle pos", min = 0, max = 3000)
uniform vec2 circlePos;
@CCProperty(name = "circle radius", min = 0, max = 1000)
uniform float circleRadius;

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
@CCProperty(name = "rect1 pos", min = 0, max = 3000)
uniform vec2 rect1Pos;
@CCProperty(name = "rect1 size", min = 0, max = 1000)
uniform vec2 rect1Size;

float rect (vec2 uv, vec2 pos, vec2 size, float blend){
	vec2 min = uv - pos - gnoise(uv * 0.03) * 10.2;
	return 
		smoothstep(0.,blend,min.x) * 
		smoothstep(size.x,size.x - blend ,min.x) *
		smoothstep(0.,blend,min.y) *
		smoothstep(size.y,size.y - blend ,min.y);
}

@CCProperty(name = "edge Noise", min = 0, max = 0.5)
uniform float edgeNoise;

@CCProperty(name = "start progress", min = 0, max = 1)
uniform float startProgress;
@CCProperty(name = "end progress", min = 0, max = 1)
uniform float endProgress;
@CCProperty(name = "progress smooth", min = 0, max = 0.1)
uniform float progressSmooth;
@CCProperty(name = "progress bow", min = 0, max = 1)
uniform float progressBow;
@CCProperty(name = "progress noise", min = 0, max = 0.1)
uniform float progressNoise;
@CCProperty(name = "progress typo", min = -0.5, max = 0.5)
uniform float progressTypo;






float progress(float x, vec2 uv, float progress, float tex, float smooth){
	//uv.x -=  
	float myProgress = x;
	myProgress += (1. - (cos(uv.y * 4.) + 1.) / 2.) * progressBow;
	myProgress += gnoise(uv * 10.3) * progressNoise;
	myProgress += tex * progressTypo;
	return smoothstep(progress, progress + smooth, myProgress / (1. + progressSmooth + progressBow));;
}

uniform float flowTime;
uniform vec2 iResolution;

@CCProperty(name = "typo uv distortion", min = 0, max = 1)
uniform float typoUVDistortion;
@CCProperty(name = "typo progress smooth", min = 0, max = 0.5)
uniform float typoProgressSmooth;
@CCProperty(name = "typo progress noise", min = 0, max = 1)
uniform float typoProgressNoise;

@CCProperty(name = "typo heat smooth", min = 0, max = 0.5)
uniform float typoHeatSmooth;
@CCProperty(name = "typo heat noise", min = 0, max = 1)
uniform float typoHeatNoise;
@CCProperty(name = "heat typo", min = -0.5, max = 0.5)
uniform float heatTypo;

void main(){
	// Sample the scene, with a distorted coordinate to simulate heat haze.
    	
    	vec2 fragNormed = gl_FragCoord.xy / iResolution.xy;
    	vec2 uv = (fragNormed - vec2(0.5)) * 2.0;
    	uv.x *= iResolution.x / iResolution.y; 
    	
    	
	float shape = 0.;//circle(gl_FragCoord.xy, circlePos, circleRadius, blendRadius);
	
	shape = max(shape, rect(gl_FragCoord.xy, rect0Pos, rect0Size, blendRadius));

	vec2 coord = uv;
    	float noiseVal = fbm(vec3(uv * 4.1 ,flowTime)) * 2. - 1.;

	vec2 tuv = coord + noiseVal * typoUVDistortion;

	vec4 col = texture2D(typo, vec2(tuv.x / 6. + 0.5, 1. - tuv.y / 2. - 0.5));
	float typoDistance = col.x;
	float edge = 0.5 + noiseVal * typoProgressNoise;// * myColor.a;
    	float dTypoProgress = smoothstep(edge - typoProgressSmooth, edge + typoProgressSmooth, typoDistance);
	
	//float myProgress = progress + (cos(uv.y * 4.) * bow + fbm(vec3(uv.x ,uv.y * 3.5,0.0)) * 0.2 ) ;
	shape = min(shape, progress(fragNormed.x,uv, endProgress, -dTypoProgress, progressSmooth));
	shape = max(shape, rect(gl_FragCoord.xy, rect1Pos, rect1Size, blendRadius));
	shape = min(shape, progress(fragNormed.x,uv, startProgress, dTypoProgress,-progressSmooth));
	shape = max(shape, circle(gl_FragCoord.xy, circlePos, circleRadius, blendRadius));

	float d = 1. - progress(fragNormed.x,uv, startProgress, dTypoProgress,-0.5);
	edge = 0.5 + noiseVal * typoHeatNoise;// * myColor.a;
    	float dTypoHeat = smoothstep(edge - typoHeatSmooth, edge + typoHeatSmooth, typoDistance);

    	float myHeat = heat;
    	myHeat += dTypoHeat * heatTypo;
    	myHeat += d * 0.3;
    	gl_FragColor.rgb = sample(uv, noiseVal,shape, myHeat);
    	//gl_FragColor.rgb = tonemap(gl_FragColor.rgb) * 1.2;
    	gl_FragColor.a = 1.;

	//float dp = progress(fragNormed.x,uv);
	//gl_FragColor = vec4(dp);
    	
   	float blend = float(uv.y < 0.5 && uv.y > -0.5) * (cos(uv.y * 2. * PI ) + 1.) / 2.;
   	
	vec3 diffuse= vec3(1.0,0.8,0.6) * .4 * shape * mix(0.2,.7,fbm(vec3(uv * 10.0 * vec2(1.0),0.0)));

	
	//gl_FragColor = vec4(d,d,d,1.);
	//gl_FragColor = col;
	//gl_FragColor.rgb = diffuse;
    //	gl_FragColor = vec4(shape);
}


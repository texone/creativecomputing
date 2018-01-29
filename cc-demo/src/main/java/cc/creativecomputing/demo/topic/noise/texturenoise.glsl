//Noise animation - Electric
//by nimitz (stormoid.com) (twitter: @stormoid)

//The domain is displaced by two fbm calls one for each axis.
//Turbulent fbm (aka ridged) is used for better effect.

#define time iTime*0.15
#define tau 6.2831853

mat2 makem2(in float theta){float c = cos(theta);float s = sin(theta);return mat2(c,-s,s,c);}

uniform sampler2D randomTexture;

uniform float iTime;
uniform vec2 iResolution;

float noise( in vec2 x ){return texture2D(randomTexture, x *.001).x;}

@CCProperty(name = "octaves", min = 1, max = 10)
uniform float octaves;
@CCProperty(name = "gain", min = 0, max = 1.)
uniform float gain;
@CCProperty(name = "lacunarity", min = 0, max = 4.)
uniform float lacunarity;

float fbm(in vec2 p)
{	
	float z=gain;
	float rz = 0.;
	vec2 bp = p;
	for (float i= 0.;i < octaves;i++)
	{
		rz+= abs((noise(p)-0.5)*2.) * z;
		z = z * gain;
		p = p * lacunarity;
	}
	return rz;
}

@CCProperty(name = "dfbmAmount", min = 0, max = 100)
uniform float dfbmAmount;

float dualfbm(in vec2 p, float amt)
{
    //get two rotated fbm calls and displace the domain
	vec2 p2 = p*7.0;
	vec2 basis = vec2(fbm(p2-time*0.1),fbm(p2+time*0.3));
	basis = (basis-.5) * amt;
	p += basis;
	
	//coloring
	return fbm(p*makem2(time*0.2));
}

@CCProperty(name = "light amount", min = 0, max = 200)
uniform float lightAmount;

@CCProperty(name = "min light amount", min = 0, max = 1)
uniform float minLight;
@CCProperty(name = "max light amount", min = 0, max = 1)
uniform float maxLight;

@CCProperty(name = "light shape", min = 0, max = 10)
uniform float lightshape;

float circ(vec2 p) 
{
	float r = length(p)* fract(iTime * 0.1);
	r = log(sqrt(r ));
	//return abs(mod(r*4.,tau)-3.14)*6.+.2;
//return (fbm(p* 2.6 + iTime * .5)) * lightAmount;
return pow(mix(minLight, maxLight, ( sin(p.x * 0.2 + p.y * 0.1 + iTime) + 1.) / 2.) ,lightshape) * lightAmount;
}

@CCProperty(name = "light pow", min = 0, max = 10)
uniform float lightPow;
@CCProperty(name = "pure", min = 0, max = 1)
uniform float pure;
@CCProperty(name = "rough", min = 0, max = 1)
uniform float rough;

@CCProperty(name = "frequency", min = 0, max = 1)
uniform float frequency; 
@CCProperty(name = "amplitude", min = 0, max = 1)
uniform float amplitude; 
@CCProperty(name = "zoom", min = 1, max = 5)
uniform float zoom; 

@CCProperty(name = "typo glow", min = 0, max = 1)
uniform float typoGlow; 
@CCProperty(name = "typo smoke", min = 0, max = 4)
uniform float typoSmoke; 

uniform sampler2D typo;

void main( )
{
	//setup system
	vec2 t = gl_FragCoord.xy / iResolution.xy;
	vec2 p = t - 0.5;
	p.x *= iResolution.x/iResolution.y;
	p*=zoom;
	p.y += sin(p.x * frequency + iTime * 0.2) * amplitude  - 0.3;

	t = vec2(t.x, 1.- t.y);
	
	float typoMask = texture2D(typo, t).r;
	
    float d = dualfbm(p + vec2(iTime * 0.003), dfbmAmount) * pure;
    d += dualfbm(p * 15. + vec2(iTime * 0.003), dfbmAmount *1. ) * rough;
    //d += typoMask * 0.1;
    float d2 = dualfbm(p * 0.25 + vec2(iTime * 0.003), 0.5) * 1.2;
	
	//rings
	//p /= exp(mod(time*10.,3.14159));
	float rz = d * pow(abs(circ(p)-(typoMask) * typoGlow ),lightPow) ;
	
	//final color
	vec3 col = vec3(.3,0.2,0.1) / rz;
	//col=pow(abs(col),vec3(1.));
	//float d = dualfbm(p) * 1.5; 
	//vec3 col2 = mix(vec3(0.4,0.0,0.0), vec3(1.0,1.0,0.), d);
	
	
	float blend = sin(1. - p.y * 1.4 );//1. - abs( - p.y ) ;
	gl_FragColor = vec4(col,1.) * max(blend, 0.)  + 0.01;// + d2 * 0.3;//
	d = d2;//blend;//1. -d;
	vec4 gold = vec4(col,1.) +0.1;
	gold *= (1. -d2 * 0.75);
	gl_FragColor = mix(vec4(d2*0.7 )+d2 * typoMask * typoSmoke , gold,  clamp(blend, 0.,1.0));// * (d2 + 1.) *1.3;
	//gl_FragColor.rgb = pow(gl_FragColor.rgb, vec3(1. / 1.5));
	//gl_FragColor += d2 * typoMask * 2.8;
	//gl_FragColor = vec4(blend,blend,blend,1.);

	//gl_FragColor = vec4(gl_FragCoord.xy / iResolution.xy,typoMask,1.);
}
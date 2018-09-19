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

@CCProperty(name = "min light amount", min = 0, max = 1)
uniform float minLight;
@CCProperty(name = "max light amount", min = 0, max = 1)
uniform float maxLight;




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


void main( )
{
	//setup system
	vec2 t = gl_FragCoord.xy / iResolution.xy;
	vec2 p = t - 0.5;
	p.x *= iResolution.x/iResolution.y;
	p*=zoom;
	p.y += sin(p.x * frequency + iTime * 0.2) * amplitude  - 0.3;

	t = vec2(t.x, 1.- t.y);
	
    float d = dualfbm(p + vec2(iTime * 0.003), dfbmAmount) ;
    d += dualfbm(p * 15. + vec2(iTime * 0.003), dfbmAmount *1. ) * rough ;
   
	
	vec3 col = vec3(.3,0.2,0.1)  /  d ;// rz;
	
	vec4 gold = vec4(col,1.);// + 0.1;
	gl_FragColor = gold;;
	//gl_FragColor = vec4(d,d,d,1.);;
	float r = (t.y * 2 - 1) * mix(1,d,0.2);
	float a = asin(r * 2);
	r *= float(r > -1);
	r *= float(r < 0.5);
	float z = cos(a);
	gl_FragColor = vec4(z,r,0,1);

}
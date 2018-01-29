#define time iTime*0.15
#define tau 6.2831853

mat2 makem2(in float theta){float c = cos(theta);float s = sin(theta);return mat2(c,-s,s,c);}


uniform sampler2D randomTexture;

uniform float iTime;
uniform vec2 iResolution;

float noise( in vec3 x ){
	return 
	(texture2D(randomTexture, x.xy *.001).x +
	
	texture2D(randomTexture, x.xz *.001).x +
	
	texture2D(randomTexture, x.yz *.001).x) / 5.;
}

// The MIT License
// Copyright © 2013 Inigo Quilez
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions: The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

vec3 hash(vec3 p){
	p = vec3(
		dot(p, vec3(127.1, 311.7, 74.7)),
		dot(p, vec3(269.5, 183.3, 246.1)),
		dot(p, vec3(113.5, 271.9, 124.6))
	);

	return -1.0 + 2.0 * fract(sin(p) * 43758.5453123);
}

float noiseg( in vec3 p){
	vec3 i = floor( p );
	vec3 f = fract( p );
	vec3 u = f*f*(3.0-2.0*f); 

	return mix( mix( mix( dot( hash( i + vec3(0.0,0.0,0.0) ), f - vec3(0.0,0.0,0.0) ), 
                           dot( hash( i + vec3(1.0,0.0,0.0) ), f - vec3(1.0,0.0,0.0) ), u.x),
                      mix( dot( hash( i + vec3(0.0,1.0,0.0) ), f - vec3(0.0,1.0,0.0) ), 
                           dot( hash( i + vec3(1.0,1.0,0.0) ), f - vec3(1.0,1.0,0.0) ), u.x), u.y),
                 mix( mix( dot( hash( i + vec3(0.0,0.0,1.0) ), f - vec3(0.0,0.0,1.0) ), 
                           dot( hash( i + vec3(1.0,0.0,1.0) ), f - vec3(1.0,0.0,1.0) ), u.x),
                      mix( dot( hash( i + vec3(0.0,1.0,1.0) ), f - vec3(0.0,1.0,1.0) ), 
                           dot( hash( i + vec3(1.0,1.0,1.0) ), f - vec3(1.0,1.0,1.0) ), u.x), u.y), u.z );
}

float fbm(in vec3 p)
{	
	float z=2.;
	float rz = 0.;
	vec3 bp = p;
	for (float i= 0.;i < 4.;i++)
	{
		rz+= abs((noise(p)-0.5)*2.)/z;
		z = z*2.;
		p = p*2.;
	}
	return rz;
}

@CCProperty(name = "dual fbm", min = 0, max = 20)
uniform float tdualFBM;

float dualfbm(in vec3 p)
{
    //get two rotated fbm calls and displace the domain
	vec3 p2 = p*7.0;
	vec3 basis = vec3(fbm(p2-time*0.6),fbm(p2+time*0.7),fbm(p2+time*0.8));
	basis = (basis-.5)* tdualFBM;
	p += basis;
	
	//coloring
	return fbm(p) / 2.;//*makem2(time*0.2));
}


@CCProperty(name = "t scale", min = 0, max = 20)
uniform float tscale;

float circ(vec2 p) 
{
	float r = length(p)* fract(iTime * 0.1);
	r = log(sqrt(r ));
	return abs(mod(r*4.,tau)-3.14)*6.+.2;

}


void main(){ 
	vec3 p = gl_TexCoord[1].xyz * vec3(5.0,1.0,1.0) * tscale;
	float d = dualfbm(p ) * 1.5; 
	d *= pow(abs((0.1-circ(p.xy))),.9);
	
	//final color
	vec3 col = vec3(.4,0.2,0.1)/d;
	col=pow(abs(col),vec3(.99));
	gl_FragColor = vec4((gl_TexCoord[0].xyz/ 2. + 0.5) * d , 1.0) ;
	gl_FragColor = vec4(d,d,d,1.);
	gl_FragColor.rgb = col;
}
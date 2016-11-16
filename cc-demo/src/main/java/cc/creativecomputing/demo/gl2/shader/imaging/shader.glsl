
#version 120

uniform float time;

uniform vec2 resolution;


float rand(vec2 co)

{
    co += vec2(4.);
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

vec3 hsv2rgb(float h, float s, float v)
{
    h = fract(h);
    vec3 c = smoothstep(2./6., 1./6., abs(h - vec3(0.5, 2./6., 4./6.)));
    c.r = 1.-c.r;
    /*
     vec3 c = vec3(
     smoothstep(1./6., 2./6., abs(h -0.5)),
     1.-smoothstep(1./6., 2./6., abs(h -2./6.)),
     1.-smoothstep(1./6., 2./6., abs(h -4./6.))
     );*/
    return mix(vec3(s), vec3(1.0), c) * v;
}

vec3 getRandomColor(float f, float t)
{
    return hsv2rgb(f+t, 0.2+cos(sin(f))*0.3, 0.9);
}

vec2 tri(vec2 s, float d){
    vec2 f = s * d;
    
    // tris
    f = mod(f, 48.); // because i failed somewhere
    
    f = f + vec2(0,0.5) * floor(f).x;
    s = fract(f);
    f = floor(f);
    
    d = s.y - 0.5;
    float l = abs(d) + 0.5 * s.x;
    float ff = f.x+f.y;
    f = mix(f, f+sign(d)*vec2(0,0.5), step(0.5, l));
    l = mix(ff, ff+sign(d)*0.5, step(0.5, l));
    
    return vec2(
                l * rand(vec2(f)),
                l * rand(vec2(f) + vec2(1000.))
                );
}

uniform float scale;
uniform float gain;
uniform float octaves;
uniform float lacunarity;
uniform float randomOctave0;
uniform float randomOctave1;
uniform float randomOctaveBlend;


vec2 octavedTri(vec2 s, float d, float randomOctave){
    float myScale = scale;
    float myFallOff = gain;
    
    int myOctaves = int(floor(octaves));
    vec2 myResult = vec2(0.);
    float myAmp = 0.;
    
    float myRandom0 = 0.;
    float myRandom1 = 0.;
    
    float myTransition = 0.;
    
    for(int i = 0; i < myOctaves;i++){
        vec2 triRandom = tri(s, d * myScale);
        myResult += triRandom * myFallOff;
        myAmp += myFallOff;
        if(mod(triRandom.x, 1.0) > randomOctave){
            return myResult / myAmp;
        }
        myFallOff *= gain;
        myScale *= lacunarity;
    }
    float myBlend = octaves - float(myOctaves);
    
    myResult += tri(s, d * myScale) * myFallOff * myBlend;
    myAmp += myFallOff * myBlend;
    
    if(myAmp > 0.0){
        myResult /= myAmp;
    }
    
    return myResult;
}

uniform sampler2D tex0;
uniform sampler2D tex1;
uniform sampler2D tex2;
uniform float randomOffset0;
uniform float randomOffset1;
uniform float randomBlend;
uniform float blend;
uniform float numberOfTextures;
uniform float kaleidoskopStartEndBlend;
uniform float kaleidoskopblend;
uniform float kaleidoskopStart;
uniform float kaleidoskopEnd;

float  easeInOut(float theBlend, float thePow) {
    theBlend = clamp(theBlend, 0., 1.);
    
    if (theBlend < 0.5) return pow(theBlend * 2., thePow) / 2.;
    return 1. - pow((1. - theBlend) * 2., thePow) / 2.;
}

float wrap(float theValue){
    theValue = abs(theValue) + 0.01;
    float result = mod(theValue, 2.0);
    if(result >= 1.)return 1. - (result -1.0);
    return result;
}

void main(){
    float mx = max( resolution.x, resolution.y );
    float t = time * 0.;
    vec2 s = gl_TexCoord[0].yx / mx + vec2(t, 0) * 0.2;
    vec2 texCoord = gl_TexCoord[0].xy / resolution;
    texCoord = vec2(texCoord.x, 1.0 - texCoord.y);
    
    int octaves = 4;
    
    vec2 f0 = octavedTri(s, 12.2, randomOctave0);
    vec2 f1 = octavedTri(s, 12.2, randomOctave1);
    vec2 f = mix(f0, f1, randomOctaveBlend);
    
    // vec3 color = getRandomColor(f * 2., t);
    
    
    vec2 dir = (normalize(f) - vec2(0.5)) / resolution * 5000.0; //normalize(vec2(cos(f * 6.2), sin(f * 6.2))) / resolution * 1000.0;
    vec2 offset0 = dir * (randomOffset0 * 0.1);
    vec2 offset1 = dir * (randomOffset1 * 0.1);
    
    vec3 color0 = texture2D(tex0,texCoord + offset0).rgb;
    vec3 color1 = texture2D(tex1,texCoord + offset1).rgb;
    
    float colorBlend0 = clamp((blend) * (1. + randomBlend) + mod(f0.x, 1.0) * randomBlend - randomBlend,0.,1.);
    float colorBlend1 = clamp((blend) * (1. + randomBlend) + mod(f1.x, 1.0) * randomBlend - randomBlend,0.,1.);
    
    float colorBlend = mix(colorBlend0, colorBlend1, randomOctaveBlend);
    vec3 colorLayer = mix(color0, color1, colorBlend);
    
    vec2 texCoordKal0 = texCoord + offset0;
    vec2 texCoordKal1 = texCoord + offset0;
    
    float start = floor(kaleidoskopStart * numberOfTextures);
    float end = ceil(kaleidoskopEnd * numberOfTextures);
    float range = end - start;
    
    float colorKalPos = (kaleidoskopStartEndBlend + f.x) * (range);
    float bottom = mod(floor(colorKalPos), range);
    float top = mod(ceil(colorKalPos), range);
    colorKalPos = mod(colorKalPos, range);
    
    
    texCoordKal0.y = (wrap(texCoordKal0.y) + bottom + start) / numberOfTextures;
    texCoordKal1.y = (wrap(texCoordKal1.y) + top + start) / numberOfTextures;
    
    float colorKalBlend = max(top, bottom) - colorKalPos;
    colorKalBlend = easeInOut(colorKalBlend, 7.);
    
    vec3 colorKal0 = texture2D(tex2, texCoordKal0).rgb;
    vec3 colorKal1 = texture2D(tex2, texCoordKal1).rgb;
    
    vec3 colorKal = mix(colorKal1, colorKal0, colorKalBlend); 
    
    vec3 color = mix(colorLayer, colorKal, kaleidoskopblend); 
    
    gl_FragData[0] =  vec4(color, 1.);
    
    gl_FragData[1] = gl_TexCoord[1];
    gl_FragData[2] = gl_TexCoord[2];
    gl_FragData[3] = gl_TexCoord[3];
}


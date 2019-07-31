uniform sampler2D depthTex0;
uniform sampler2D depthTex1;
uniform sampler2D thresh;

vec3 hsb2rgb(vec3 c){
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

@CCProperty(name = "depth scale", min = 0, max = 1)
uniform float depthScale;

const vec2 size = vec2(0.01,0.0); 
const vec3 sign = vec3(1.0,0.0,-1.0);

@CCProperty(name = "depth min", min = 0, max = 1)
uniform float depthMin;
@CCProperty(name = "depth max", min = 0, max = 1)
uniform float depthMax;

float depth(sampler2D depthTex, vec2 pos){
	float result =  texture2D(depthTex, pos).x;
	if(result < depthMin)result = 0.0;
	if(result > depthMax)result = 0.0;
	return result;
}

float depth0(vec2 pos){
	return depth(depthTex0, pos);
}

float depth1(vec2 pos){
	return depth(depthTex1, pos);
}

vec3 normal(vec2 pos, vec2 theOffset){
	float s01 = depth1(pos + theOffset * sign.xy);
	float s21 = depth1(pos + theOffset * sign.zy);
	float s10 = depth1(pos + theOffset * sign.yx);
	float s12 = depth1(pos + theOffset * sign.yz);

	if(s01 * s21 * s10 * s12 == 0.0)return vec3(0.0,0.0,0.0);
	if(abs(s01 - s21)> 0.0002)return vec3(0.0,0.0,0.0);
	if(abs(s10 - s12)> 0.0002)return vec3(0.0,0.0,0.0);
	
	vec3 va = normalize(vec3(sign.xy * 0.0001, s21-s01));
	vec3 vb = normalize(vec3(sign.yx * 0.0001, s12-s10));
	return cross(va,vb); 
}

@CCProperty(name = "mip pow", min = 0, max = 5)
uniform float mipPow;

@CCProperty(name = "mip boost", min = 1, max = 5)
uniform float mipBoost;

@CCProperty(name = "mip level", min = 1, max = 20)
uniform float mipLevel;

float threshold(vec2 pos){
	float d = 0;
	float amp = 0;
	for(int i = 1;i< mipLevel;i++){
		float s = pow(mipPow,i);
		d += texture2DLod(thresh, pos,i) * s;
		amp += s;
	}
	d/=amp;
	d*=mipBoost;
	return d;
}

@CCProperty(name = "normal dist", min = 0, max = 50)
uniform float normalDist;
@CCProperty(name = "thresh depth scale", min = 0, max = 1)
uniform float threshDepthScale;

vec3 threshNormal(vec2 pos, vec2 theOffset){
	float ds = 0.1;
	float s01 = threshold(pos + theOffset * sign.xy);
	float s21 = threshold(pos + theOffset * sign.zy);
	float s10 = threshold(pos + theOffset * sign.yx);
	float s12 = threshold(pos + theOffset * sign.yz);
	
	vec3 va = normalize(vec3(sign.xy * threshDepthScale, s21-s01));
	vec3 vb = normalize(vec3(sign.yx * threshDepthScale, s12-s10));
	return cross(va,vb); 
}

void main(){
	float depth = depth1(gl_TexCoord[0].xy);
	gl_FragColor = vec4(hsb2rgb(vec3(depth * 10.0,1.0,1.0)),1.0);
	
	vec3 norm = normal(gl_TexCoord[0].xy, vec2(1./640.0 * 2.0,1./480.0* 2.0));
	gl_FragColor = vec4(1.0 - depth,1.0 - depth,1.0 - depth,1.0);
	gl_FragColor = vec4(norm,1.0);

	
	float depth0 = depth0(gl_TexCoord[0].xy);
	float depth1 = depth1(gl_TexCoord[0].xy);

	float dif = depth1 - depth0;
	dif*=2000;
	if(depth0 * depth1 == 0.0)dif = 0.0;
	if(abs(dif)>0.4)dif = 0.0;
	float r = dif;
	float b = -dif;
	gl_FragColor = new vec4(r,float(depth != 0)*0.1 ,b,1);

	float thresholdD = threshold(gl_TexCoord[0].xy);
	gl_FragColor = vec4(thresholdD,thresholdD,thresholdD,1);
	//gl_FragColor=texture2DLod(thresh, gl_TexCoord[0].xy,8);
	vec3 threshnorm = threshNormal(gl_TexCoord[0].xy, vec2(1./640.0 * normalDist,1./480.0* normalDist));
	threshnorm.z = dif;
	//threshnorm = normalize(threshnorm);
	//threshnorm += 1;
	//threshnorm /= 2;
	gl_FragColor = vec4(threshnorm,1);
}

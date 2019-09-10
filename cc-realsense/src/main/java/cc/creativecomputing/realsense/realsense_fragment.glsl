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
	return (result - depthMin) / (depthMax - depthMin);
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
	float gain = 1;
	for(int i = 1;i< mipLevel;i++){
		gain *= mipPow;
		d += texture2DLod(thresh, pos,i).x * gain;
		amp += gain;
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
	float s01 = threshold(pos + theOffset * sign.xy);
	float s21 = threshold(pos + theOffset * sign.zy);
	float s10 = threshold(pos + theOffset * sign.yx);
	float s12 = threshold(pos + theOffset * sign.yz);
	
	vec3 va = normalize(vec3(sign.xy * threshDepthScale * 0.1, s21-s01));
	vec3 vb = normalize(vec3(sign.yx * threshDepthScale * 0.1, s12-s10));
	//return vec3(s21-s01,s21-s01,s21-s01) * 1;
	return cross(va,vb); 
}

uniform float depthToMeters;
uniform vec2 depthOffset;
uniform vec2 depthFocalLength;
uniform vec2 depthTextureSize;

vec4 deproject(vec2 index, float depth) {
   return vec4((index - depthOffset)/ depthFocalLength * depth, depth, 1.0);
}

uniform vec3 boundMin;
uniform vec3 boundMax;

uniform bool mirror;

void main(){
	vec2 uv = gl_TexCoord[0].xy;
	if(mirror)uv.x = 1 - uv.x;
	float depth = texture2D(depthTex0, uv).x;
	vec2 depthIndices = uv * depthTextureSize;

	vec4 position = deproject(depthIndices, depth * depthToMeters)*100 ;
	position.xyz *= 1000;

	float inBound = 1;

	inBound *= float(position.x > boundMin.x * 0.01);
	inBound *= float(position.x < boundMax.x * 0.01);
	
	inBound *= float(position.y > boundMin.y * 0.01);
	inBound *= float(position.y < boundMax.y * 0.01);
	
	inBound *= float(position.z > boundMin.z * 0.01);
	inBound *= float(position.z < boundMax.z * 0.01);

	position *= inBound;
	
	vec3 norm = normal(uv, vec2(1./640.0 * 2.0,1./480.0* 2.0));
	gl_FragData[0] = vec4(1.0 - depth,1.0 - depth,1.0 - depth,1.0);
	gl_FragData[0] = vec4(norm,1.0);

	
	float depth0 = depth0(uv);
	float depth1 = depth1(uv);

	float speed = depth1 - depth0;
	speed *= 100;
	if(depth0 * depth1 == 0.0)speed = 0.0;
	if(abs(speed)<0.5)speed = 0.0;
	

	
	//gl_FragColor = vec4(thresholdD,thresholdD,thresholdD,1);
	//gl_FragColor=texture2DLod(thresh, gl_TexCoord[0].xy,8);
	vec3 threshnorm = threshNormal(uv, vec2(1./640.0 * normalDist,1./480.0 * normalDist));
	threshnorm.z = speed ;
	//threshnorm = normalize(threshnorm);
	threshnorm += 1;
	threshnorm /= 2;
	
	int mode = 4;
	switch(mode){
		// show depth
		case 0:
		depth0 *= inBound;
		gl_FragData[0] = vec4(depth0,depth0,depth0,1);
		break;

		// show treshold
		case 1:
		//threshold *= inBound;
		float threshold = threshold(uv) ;
		gl_FragData[0] = vec4(threshold,threshold,threshold,1);
		break;

		// show depth hsb
		case 2:
		gl_FragData[0] = vec4(hsb2rgb(vec3(depth * 10.0,1.0,1.0)),1.0);
		break;

		// show depth speed
		case 3:
		float r = speed;
		float b = -speed;
		gl_FragData[0] = vec4(r,float(threshold != 0)*0.1 ,b,1);
		break;

		// show normal speed
		case 4:
		gl_FragData[0] = vec4(threshnorm,1);
		break;
	}
//position.x = 0;
	gl_FragData[3] = position;
	//gl_FragColor = vec4(threshnorm,1);
}

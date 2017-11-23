uniform sampler2DRect motionData;

uniform float maxSpeed;
uniform float maxAcc;
uniform float maxJerk;

uniform int drawMode;

vec4 motionData(vec2 theCoord){
 	return texture2DRect(motionData, theCoord);
}

void main(){

    vec4 myMotionData0 = motionData(gl_TexCoord[0].xy);
    vec4 myMotionData1 = motionData(gl_TexCoord[0].xy + vec2(1.0, 0.0));
    vec4 myMotionData2 = motionData(gl_TexCoord[0].xy + vec2(2.0, 0.0));
    vec4 myMotionData3 = motionData(gl_TexCoord[0].xy + vec2(3.0, 0.0));
    
    float myVal0 = myMotionData0.x;
    float myVal1 = myMotionData1.x;
    float myVal2 = myMotionData2.x;
    float myVal3 = myMotionData3.x;
    
    float mySpeed0 = myVal1 - myVal0;
    float mySpeed1 = myVal2 - myVal1;
    float mySpeed2 = myVal3 - myVal2;
    
    float myAcc0 = mySpeed1 - mySpeed0;
    float myAcc1 = mySpeed2 - mySpeed1;
    
    float myJerk0 = myAcc1 - myAcc0;
    
    float min = myMotionData0.y;
    float max = myMotionData0.z;
    float bright = (myVal0 - min) / (max - min);
    
	vec3 outColor = vec3(bright);
    
    if(drawMode == 1){
	    bright = abs(mySpeed0 / maxSpeed);
	    outColor = vec3(bright);
	    if(mySpeed0 < 0.0)outColor *= vec3(1.0,0.0,0.0);
	    else outColor *= vec3(0.0,0.0,1.0);
	    if(abs(mySpeed0) > maxSpeed)outColor.g = 1.0;
    }
    if(drawMode == 2){
	    bright = abs(myAcc0 / maxAcc);
	    outColor = vec3(bright);
	    if(myAcc0 < 0.0)outColor *= vec3(1.0,0.0,0.0);
	    else outColor *= vec3(0.0,0.0,1.0);
	    if(abs(myAcc0) > maxAcc)outColor.g = 1.0;
    }
    if(drawMode == 3){
	    bright = abs(myJerk0 / maxJerk);
	    outColor = vec3(bright);
	    if(myJerk0 < 0.0)outColor *= vec3(1.0,0.0,0.0);
	    else outColor *= vec3(0.0,0.0,1.0);
	    if(abs(myJerk0) > maxJerk)outColor = vec3(1.0,1.0,0.0);
    }
    
    
    gl_FragColor = vec4(outColor,1.0);
}
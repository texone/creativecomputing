uniform sampler2DRect motionData;

uniform float maxSpeed;
uniform float maxAcc;
uniform float maxJerk;

uniform float scale;

uniform int drawMode;

void main(){
	vec4 motionValue = texture2DRect(motionData, gl_MultiTexCoord0.xy);
	float myHeight = motionValue.r;
	
	if(drawMode > 0){
		if(myHeight == 0.0)myHeight = -motionValue.b;
	}
	
	float myY = myHeight * scale;
	
	if(drawMode == 0){
		myY -= scale / 2.0;
	}
	
	gl_Position = gl_ModelViewProjectionMatrix * (gl_Vertex + vec4(0.0,myY,0.0,0.0));
	gl_FrontColor = gl_Color;
}
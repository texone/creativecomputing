uniform sampler2DRect motionData;

uniform float maxSpeed;
uniform float maxAcc;
uniform float maxJerk;

uniform float scale;

uniform int drawMode;

void main(){
	float motionValue0 = texture2DRect(motionData, gl_MultiTexCoord0.xy).r;
	float motionValue1 = texture2DRect(motionData, gl_MultiTexCoord0.xy + vec2(0.0,1.0)).r;
	
		
	float a = motionValue0;
	float b = motionValue1;
	float c = 1.0;// theElement.motorDistance;
		        
	float beta = acos ((a * a + c * c - b * b) / (2.0 * a * c));
	float x = a * cos(beta);
	float h = a * sin(beta);
		
	vec2 sourcePosition = vec2(x - c / 2.0, h);
	
	gl_Position = gl_ModelViewProjectionMatrix * ( vec4(sourcePosition.xy * vec2(scale, -scale) ,0.0, 1.0)); //vec4(gl_Vertex.x + sourcePosition.y * scale, 0.0, 0.0, 1.0);
	gl_FrontColor = gl_Color;
}
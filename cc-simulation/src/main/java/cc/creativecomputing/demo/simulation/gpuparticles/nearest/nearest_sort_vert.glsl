#version 120 
#extension GL_ARB_texture_rectangle : enable

uniform sampler2DRect data;
uniform sampler2DRect connectionData;

void main(){
	vec4 connection1 = texture2DRect(connectionData,gl_Vertex.xy);
	vec4 connection2 = texture2DRect(connectionData,gl_Vertex.zw);
	vec4 mainPos = texture2DRect(data,connection2.xy);
	vec4 firstPos = texture2DRect(data,connection1.xy);
	
	float minDist = distance(mainPos.xyz, firstPos.xyz);
	
	if(firstPos == mainPos){
		gl_FrontColor = connection2;
	}else{
		vec4 result = connection1;
		for(int i = 1; i < 6;i++){
			vec4 testConnection = texture2DRect(connectionData, vec2(connection1.x * 6 + i, connection1.y));
			vec4 testPos = texture2DRect(data,testConnection.xy);
			if(distance(mainPos.xyz, testPos.xyz) < minDist){
				result = testConnection;
			}
		}
		gl_FrontColor = result;
	}
	vec4 pos = vec4(gl_Vertex.xy,0,1);
	
	gl_FrontColor = connection1;
	gl_Position = gl_ProjectionMatrix * gl_ModelViewMatrix * pos;
	
}
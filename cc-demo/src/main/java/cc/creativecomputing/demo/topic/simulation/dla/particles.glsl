#version 120 

uniform sampler2D randomTexture;
uniform sampler2DRect positionTexture;
uniform sampler2DRect crystalTexture;

uniform vec2 texOffset;
uniform vec2 boundary;

uniform float speed;
uniform float replacement;

void main(){

	vec2 iTexCoord = gl_FragCoord.xy; 
	vec4 oPosition = texture2DRect(positionTexture, iTexCoord);
	vec4 crystalColor = texture2DRect(crystalTexture,oPosition.xy);
	
	float mySpeed = speed;
	
	if(crystalColor.x > 0){
		mySpeed *= 3;
	}

	vec2 texCoord = oPosition.xy;
	vec4 random = (texture2D(randomTexture, texCoord) - 0.5) * mySpeed; 
	
	oPosition+= vec4(random.x, random.y,0,0);
	
	if(oPosition.x > boundary.x)oPosition.x -= boundary.x;
	if(oPosition.x < 0)oPosition.x += boundary.x;
	if(oPosition.y > boundary.y)oPosition.y -= boundary.y;
	if(oPosition.y < 0)oPosition.y += boundary.y;
	
	gl_FragColor = oPosition;
	
}

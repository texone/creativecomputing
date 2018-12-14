@CCProperty(name = "stripe width", min = 10, max = 1000)
uniform float stripeWidth;

@CCProperty(name = "blend", min = 0, max = 1)
uniform float blend;

@CCProperty(name = "blendRange", min = 0, max = 1)
uniform float blendRange;
@CCProperty(name = "global blend", min = 0, max = 1)
uniform float globalBlend;
@CCProperty(name = "x blend", min = 0, max = 1)
uniform float xBlend;
@CCProperty(name = "y blend", min = 0, max = 1)
uniform float yBlend;


@CCProperty(name = "X_ModBlend", min = 0, max = 1)
uniform float X_ModBlend;
@CCProperty(name = "X_Mod", min = 0, max = 100)
uniform float X_Mod;


@CCProperty(name = "Y_Mod", min = -1, max = 1)
uniform float Y_Mod;


void main(){
	float minGlobalBlend = max(0.001, blend);
	float myOffsetSum = minGlobalBlend + X_ModBlend + xBlend + yBlend;

	float myModulation = blend * minGlobalBlend;     

	vec2 uv = gl_FragCoord / vec2(1200,600);

	myModulation += uv.x * xBlend;
	myModulation += uv.y * yBlend;

    	myModulation += float(fmod(gl_FragCoord.x + gl_FragCoord.y * Y_Mod, X_Mod) / X_Mod > 0.5)* X_ModBlend;
    	

	            if(myOffsetSum > 0){
		            myModulation /= myOffsetSum;
	            }
	float d = smoothstep(myModulation, myModulation + blendRange, blend * (1 + blendRange)); 

	vec3 col = mix(vec3(uv.xy + vec2(1) * d,0),vec3(0,(1-uv.xy)+ vec2(1) * (1 - d)),d);
	gl_FragColor = vec4(col,1);
}
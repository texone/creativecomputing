@CCProperty(name = "scale", min = 0, max = 1)
uniform float scale;
@CCProperty(name = "gain", min = 0, max = 1)
uniform float gain;
@CCProperty(name = "octaves", min = 1, max = 4)
uniform float octaves;
@CCProperty(name = "lacunarity", min = 0, max = 4)
uniform float lacunarity;

// replace outType, inType and evalFunc with whatever you need

outType fbm(in inType s){ 
	float myScale = scale * .1;
	float myFallOff = gain;
	
	int myOctaves = int(floor(octaves)); 
	vec3 myResult = vec3(0.);  
	float myAmp = 0.;
	
	for(int i = 0; i < myOctaves;i++){
		vec3 noiseVal = noise(s * myScale); 
		myResult += noiseVal * myFallOff;
		myAmp += myFallOff;
		myFallOff *= gain;
		myScale *= lacunarity;
	}
	float myBlend = octaves - float(myOctaves);
	
	myResult += noise(s * myScale) * myFallOff * myBlend;    
	myAmp += myFallOff * myBlend;
	
	if(myAmp > 0.0){
		myResult /= myAmp;
	}
 
	return myResult;
}
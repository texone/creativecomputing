@CCProperty(name = "scale", min = 0, max = 1)
uniform float scale;
@CCProperty(name = "gain", min = 0, max = 1)
uniform float gain;
@CCProperty(name = "octaves", min = 1, max = 4)
uniform float octaves;
@CCProperty(name = "lacunarity", min = 0, max = 4)
uniform float lacunarity;

float ridge(in vec2 s){  
	float myScale = scale * 0.1;
	float myFallOff = gain;
	
	int myOctaves = int(floor(octaves)); 
	float myResult = 0.;  
	float myAmp = 0.;
	 s *= myScale; 
	for(int i = 0; i < myOctaves;i++){
		float noiseVal = noise(s);  
		myResult += abs(noiseVal) * myFallOff;
		myAmp += myFallOff;
		myFallOff *= gain;
		s = s * lacunarity; 
	}
	float myBlend = octaves - float(myOctaves);
	
	myResult += abs(noise(s)) * myFallOff * myBlend;    
	myAmp += myFallOff * myBlend;
	
	if(myAmp > 0.0){
		myResult /= myAmp;
	}
 	myResult = 1. - myResult;
 	myResult = pow(myResult , 10.);
	return myResult;
}
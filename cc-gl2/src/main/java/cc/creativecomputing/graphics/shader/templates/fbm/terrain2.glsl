// fbm heightmap 

const mat2 m = mat2(0.8,-0.6,0.6,0.8);

float terrain( in vec2 p) {
	float rz = 0.;
	float z = 1.;
	vec2  d = vec2(0.0);
	float scl = 2.95;
	float zscl = -.4;
	float zz = 5.;

	for(int i = 0; i < 5; i++){
		vec3 n = noised(p);  
		d += pow(abs(n.yz),vec2(zz));
		d -= smoothstep(-.5,1.5,n.yz);
		zz -= 1.;
		rz += z*n.x/(dot(d,d)+.85);
		z *= zscl;
		zscl *= .8;
		p = m2*p*scl;
	}
    
	rz /= smoothstep(1.5,-.5,rz)+.75;
	return rz;
}
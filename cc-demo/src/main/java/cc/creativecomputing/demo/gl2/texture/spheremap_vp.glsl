
varying vec2 vN;

void main(){
	gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
	
	vec3 n = normalize(gl_Normal );

    float m = 2. * sqrt( 
        pow( n.x, 2. ) + 
        pow( n.y, 2. ) + 
        pow( n.z + 1., 2. ) 
    );
    vN = n.xy / m + .5;
}
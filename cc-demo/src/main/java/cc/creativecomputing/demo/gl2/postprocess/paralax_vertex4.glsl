#version 120

uniform mat4 model_mtx;
uniform mat4 proj_mtx;

varying vec3 ts_light_pos; // Tangent space values
varying vec3 ts_view_pos;  //
varying vec3 ts_frag_pos;  //

mat3 transpose(in mat3 inMatrix)
{
    vec3 i0 = inMatrix[0];
    vec3 i1 = inMatrix[1];
    vec3 i2 = inMatrix[2];

    mat3 outMatrix = mat3(
        vec3(i0.x, i1.x, i2.x),
        vec3(i0.y, i1.y, i2.y),
        vec3(i0.z, i1.z, i2.z)
    );

    return outMatrix;
}

void main(){
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
	
 	vec3 tangent = normalize(gl_MultiTexCoord1.xyz);
	vec3 bitangent	= cross(gl_Normal, tangent) * gl_MultiTexCoord1.w;
	gl_TexCoord[0] = gl_MultiTexCoord0;


	vec3 t = normalize(gl_NormalMatrix * tangent);
	vec3 b = normalize(gl_NormalMatrix * bitangent);
	vec3 n = normalize(gl_NormalMatrix * gl_Normal);
	mat3 tbn = transpose(mat3(t, b, n));

	vec3 light_pos = vec3(100, 200, -2);
	ts_light_pos = tbn * light_pos;
	
	// Our camera is always at the origin
	ts_view_pos = tbn * vec3(0, 0, 0);
    
	ts_frag_pos = vec3(gl_ModelViewMatrix * gl_Vertex);
	ts_frag_pos = tbn * ts_frag_pos;

	gl_TexCoord[1] = gl_Vertex;
} 
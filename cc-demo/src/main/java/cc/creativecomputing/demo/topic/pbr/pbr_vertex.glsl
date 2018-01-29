#version 120

varying vec3 WorldPos;
varying vec3 Normal;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;

void main(){ 
    gl_TexCoord[0] = gl_MultiTexCoord0;
    WorldPos = vec3(gl_ModelViewMatrix * gl_Vertex);
    Normal = gl_NormalMatrix * gl_Normal;   

    gl_Position =  gl_ModelViewProjectionMatrix * gl_Vertex;
}  
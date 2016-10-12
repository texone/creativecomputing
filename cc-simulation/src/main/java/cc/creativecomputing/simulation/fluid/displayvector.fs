uniform sampler2D read;

uniform vec3 bias;
uniform vec3 scale;

void main()
{
    gl_FragColor = vec4(bias + scale * texture2D(read, gl_TexCoord[0].xy).xyz, 1.0);
}

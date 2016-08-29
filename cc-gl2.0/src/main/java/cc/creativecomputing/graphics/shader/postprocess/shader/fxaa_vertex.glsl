varying vec4 posPos;
uniform float subpixelShift;
uniform vec2 invTextureSize;

void main(void)
{
  gl_Position = ftransform();
  gl_TexCoord[0] = gl_MultiTexCoord0;
  posPos.xy = gl_MultiTexCoord0.xy;
  posPos.zw = gl_MultiTexCoord0.xy - (invTextureSize * (0.5 + subpixelShift));
}
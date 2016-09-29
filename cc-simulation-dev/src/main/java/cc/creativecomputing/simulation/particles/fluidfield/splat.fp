//------------------------------------------------------------------------------
// File : splat.cg
//------------------------------------------------------------------------------
// Copyright 2003 Mark J. Harris and
// The University of North Carolina at Chapel Hill
//------------------------------------------------------------------------------
// Permission to use, copy, modify, distribute and sell this software and its 
// documentation for any purpose is hereby granted without fee, provided that 
// the above copyright notice appear in all copies and that both that copyright 
// notice and this permission notice appear in supporting documentation. 
// Binaries may be compiled with this software without any royalties or 
// restrictions. 
//
// The author(s) and The University of North Carolina at Chapel Hill make no 
// representations about the suitability of this software for any purpose. 
// It is provided "as is" without express or implied warranty.
/**
 * @file splat.cg
 * 
 * @todo <WRITE FILE DOCUMENTATION>
 */
uniform float4 color;
uniform float2 position;
uniform float radius;
uniform float2 windowDims;
uniform samplerRECT base;
  
float gaussian(float2 pos, float radius){
  return exp(-dot(pos, pos) / radius);
}

void main(
	in float2 coords : TEX0,
	out float4 outColor : COLOR
){
	float2 pos = position - coords/windowDims;
	float  rad = radius/windowDims.x;
	float factor = distance(position,coords) /windowDims.x ;
      
	outColor = texRECT(base, coords) + float4(1,1,1,1) * gaussian(pos, rad);
} 
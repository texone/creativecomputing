//----------------------------------------------------------------------------
// File : flo.cg
//----------------------------------------------------------------------------
// Copyright 2003 Mark J. Harris and
// The University of North Carolina at Chapel Hill
//----------------------------------------------------------------------------
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

#include "floUtil.cg" // for texRECTBilerp() and texRECTneighbors()




 

//----------------------------------------------------------------------------
// Function     	: updateOffsets
// Description	    : 
//----------------------------------------------------------------------------
/**
 * This program is used to compute boundary value lookup offsets for 
 * implementing boundary conditions around arbitrary boundaries inside the 
 * flow field.
 *
 * This program is run only when the arbitrary interior boundaries change.  
 * Each cell can either be fluid or boundary.  A zero in the boundaries
 * texture indicates fluid, a 1 indicates boundary.
 *
 * The trick here is to use the boundary (0,1) values of the neighbors of a 
 * cell to compute a single 4-vector containing the x and y offsets needed
 * to compute the correct boundary conditions.
 *
 * A clever encoding enables this.  A "stencil" is used to multiply and add
 * the neighbors and the center cell.  The stencil values are picked such
 * that each configuration has a unique value:
 *
 *    |   |  3 |   |
 *    | 7 | 17 | 1 |
 *    |   |  5 |   |
 *
 * The result is that we can precompute all possible configurations and store
 * the appropriate offsets for them (see Flo::_CreateOffsetTextures() in 
 * flo.cpp) in a 1D lookup table texture.  Then we use this unique stencil 
 * value as the texture coordinate.
 *
 * All of these texture reads (one per neighbor) are expensive, so we only
 * do this when the boundaries change, and then write them to an offset 
 * texture.  Two lookups into this texture allow the arbitrary*Boundaries()
 * programs to compute pressure and velocity boundary values efficiently.
 *
 */ 
void updateOffsets(half2       coords : WPOS,
               out half4       offsets : COLOR,
           uniform samplerRECT b,
           uniform samplerRECT offsetTable)
{
  // get neighboring boundary values (on or off)
  half bW, bE, bN, bS;
  h1texRECTneighbors(b, coords, bW, bE, bS, bN);
  // center cell
  half bC = h1texRECT(b, coords);

  // compute offset lookup index by adding neighbors...
  // the strange offsets ensure a unique index for each possible configuration
  half index = 3 * bN + bE + 5 * bS + 7 * bW + 17 * bC;

  // get scale and offset = (uScale, uOffset, vScale, vOffset)
  offsets = h4texRECT(offsetTable, index);
}











//----------------------------------------------------------------------------
// Function     	: display[Scalar | Vector][Bilerp]
// Description	    : 
//----------------------------------------------------------------------------
/**
 * The following four programs simply display rectangle textures.  A fragment 
 * program is required on NV3X to display floating point textures.  The scale 
 * and bias parameters allow the manipulation of the values in the texture 
 * before display.  This is useful, for example, if the values in the texture 
 * are signed.  A scale and bias of 0.5 can bring the range [-1, 1] into the 
 * range [0, 1] for  for visualization or other purposes.
 * 
 * The four versions of the program are for displaying with and without 
 * bilinear interpolation (smoothing), and for scalar and vector textures.
 */ 

// displayScalar
void displayScalar(half2       coords : TEX0,
               out half4       color  : COLOR,
           
           uniform half4       scale,
           uniform half4       bias,
           uniform samplerRECT texture)
{
  color = bias + scale * h4texRECT(texture, coords).xxxx;
} 

// displayVector
void displayVector(half2       coords : TEX0,
               out half4       color  : COLOR,
             
           uniform half4       scale,
           uniform half4       bias,
           uniform samplerRECT texture)
{
  color = bias + scale * h4texRECT(texture, coords);
} 

// displayScalarBilerp
void displayScalarBilerp(half2       coords : TEX0,
                     out half4       color : COLOR,
         
                 uniform half4       scale,
                 uniform half4       bias,
                 uniform samplerRECT texture)
{
  color = bias + scale * h1texRECTbilerp(texture, coords);
} 

// displayVectorBilerp
void displayVectorBilerp(half2       coords : TEX0,
         
                     out half4       color : COLOR,
                
                 uniform half4       scale,
                 uniform half4       bias,
                 uniform samplerRECT texture)
{
  color = bias + scale * h4texRECTbilerp(texture, coords);
}

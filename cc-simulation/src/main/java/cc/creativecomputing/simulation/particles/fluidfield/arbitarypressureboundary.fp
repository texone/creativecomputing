
//----------------------------------------------------------------------------
// Function     	: arbitraryPressureBoundary
// Description	    : 
//----------------------------------------------------------------------------
/**
 * This program is used to implement pure-neumann pressure boundary conditions
 * around arbitrary boundaries.  This program operates in essentially the same
 * manner as arbitraryVelocityBoundary, above.
 */

uniform samplerRECT p;
uniform samplerRECT offsets;

void main(
	in half2 coords : WPOS,
	out half4 pNew : COLOR
){
  // get the two neighboring pressure offsets
  // they will be the same if this is N, E, W, or S, different if NE, SE, etc.
  half4 offset = h4texRECT(offsets, coords);

  pNew = 0.5 * (h1texRECT(p, coords + offset.xy) + 
                h1texRECT(p, coords + offset.zw));
}
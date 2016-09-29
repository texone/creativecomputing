/**
 * This program uses the offset texture computed by the program above to 
 * implement arbitrary no-slip velocity boundaries.  It is essentially the
 * same in operation as the edge boundary program above, but it requires
 * an initial texture lookup to get the offsets (they can't be provided as
 * a uniform parameter because they change at each cell).  It must then offset
 * differently in x and y, so it requires two lookups to compute the boundary
 * values.
 */

uniform samplerRECT u;
uniform samplerRECT offsets;

void main(
	in half2 coords : WPOS,
	out half4 uNew : COLOR
){
	// get scale and offset = (uScale, uOffset, vScale, vOffset)
	half4 scaleoffset = h4texRECT(offsets, coords);
  
	// compute the x boundary value
	uNew.x = scaleoffset.x * h1texRECT(u, coords + half2(0, scaleoffset.y));
	
	// compute the y boundary value
	uNew.y = scaleoffset.z * h2texRECT(u, coords + half2(scaleoffset.w, 0)).y;
	uNew.zw = 0;
}
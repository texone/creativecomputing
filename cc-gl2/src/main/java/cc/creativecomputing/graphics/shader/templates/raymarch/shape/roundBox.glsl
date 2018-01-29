/**
 * Signed distance function for a cube centered at the origin
 * with dimensions specified by size.
 */
float roundBoxSDF( vec3 p, vec3 size, float r ){
  return length(max(abs(p)-size / 2.0,0.0))-r;
}
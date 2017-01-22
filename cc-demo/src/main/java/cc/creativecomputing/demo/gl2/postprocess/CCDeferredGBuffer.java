package cc.creativecomputing.demo.gl2.postprocess;

/**
 * //////////////////////////////////////////////////////////////////////////////////////////////
 * /* G-BUFFER
 * 
 * The geometry buffer, or G-buffer, captures our 3D scene's data in 2D screen
 * space. A G-buffer can store pretty much anything you want. Position, normal,
 * color, velocity, material, luminance data. You name it. However, it's best to
 * keep this information to a minimum to improve performance. Our G-buffer
 * stores depth, normals encoded to 8-bit values in two channels, and material
 * IDs. We also render everything with instancing to keep draw calls to a
 * minimum.
 * <p>
 * "unpack.glsl" contains methods for decoding normals and calculating 3D
 * positions from depth and camera data. The material ID represents the index of
 * a material in our UBO. This allows models to access information for diffuse,
 * specular, shininess, etc values without having to store them in a texture.
 *
 * @author christianr
 *
 */
public class CCDeferredGBuffer {

}

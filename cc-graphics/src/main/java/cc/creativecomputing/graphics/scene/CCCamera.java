package cc.creativecomputing.graphics.scene;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4x4;
import cc.creativecomputing.math.CCVector3;

/**
 * @author christianr
 * 
 */
public class CCCamera {

	/**
	 * The view frustum has parameters [rmin,rmax], [umin,umax], and
	 * [dmin,dmax]. The interval [rmin,rmax] is measured in the right direction
	 * R. These are the "left" and "right" frustum values. The interval
	 * [umin,umax] is measured in the up direction U. These are the "bottom" and
	 * "top" values. The interval [dmin,dmax] is measured in the view direction
	 * D. These are the "near" and "far" values. The frustum values are stored
	 * in an array with the following mappings:
	 */
	public static enum CCFrustumPlane {
		NEAR, // near
		FAR, // far
		BOTTOM, // bottom
		TOP, // top
		LEFT, // left
		RIGHT // right
	}

	/**
	 * The projection matrices for the camera. The matrices are stored so that
	 * you apply it to vectors on the right: projMatrix*someVector4.
	 */
	protected CCMatrix4x4 _myProjectionMatrix;
	
	/**
	 * The product of the projection and view matrix. This includes the
	 * post-projection and/or pre-view whenever those are not the identity
	 * matrix.
	 */
	protected CCMatrix4x4 _myProjectionViewMatrix;
	/**
	 * The world coordinate frame.
	 */
	protected CCVector3 _myPosition;
	protected CCVector3 _myDirection;
	protected CCVector3 _myUp;
	protected CCVector3 _myRight;
	
	/**
	 * The view matrix for the camera. The matrix is stored so that you apply it
	 * to vectors on the right: viewMatrix*someVector4.
	 */
	protected CCMatrix4x4 _myViewMatrix;
	/**
	 * The preview matrix for the camera.
	 */
	protected CCMatrix4x4 _myPreViewMatrix;
	protected boolean _myPreViewIsIdentity;
	/**
	 * The postprojection matrix for the camera.
	 */
	protected CCMatrix4x4 _myPostProjectionMatrix;
	protected boolean _myPostProjectionIsIdentity;
	/**
	 * This member is 'true' for a perspective camera or 'false' for an
	 * orthographic camera.
	 */
	protected boolean _myIsPerspective;
	/**
	 * The view frustum, stored in order as dmin (near), dmax (far), umin
	 * (bottom), umax (top), rmin (left), and rmax (right).
	 */
	protected double[] _myFrustum = new double[CCFrustumPlane.values().length];

	/**
	 * Construction and destruction.
	 * @param theIsPerspective
	 */
	public CCCamera(boolean theIsPerspective) {
		_myIsPerspective = theIsPerspective;
		
		_myViewMatrix = new CCMatrix4x4();
		
		_myProjectionMatrix = new CCMatrix4x4();
		_myProjectionViewMatrix = new CCMatrix4x4();
		
		_myPreViewMatrix = CCMatrix4x4.IDENTITY.clone();
		_myPreViewIsIdentity = _myPreViewMatrix.isIdentity();
		
		_myPostProjectionMatrix = CCMatrix4x4.IDENTITY.clone();
		_myPostProjectionIsIdentity = _myPostProjectionMatrix.isIdentity();
		
		// if( WM5_VALIDATE_CAMERA_FRAME_ONCE){
		// mValidateCameraFrame(true)
		// }

		frame(CCVector3.ZERO, CCVector3.NEG_UNIT_Z, CCVector3.UNIT_Y, CCVector3.UNIT_X);

		perpective(90.0f, 1.0f, 1.0f, 10000.0f);
	}

	/**
	 * 
	 */
	public CCCamera() {
		this(true);
	}

	/**
	 * The camera frame is always in world coordinates. default position P = (0,
	 * 0, 0; 1) default direction D = (0, 0, -1; 0) default up U = (0, 1, 0; 0)
	 * default right R = (1, 0, 0; 0)
	 * @param thePosition
	 * @param theDirection
	 * @param theUp
	 * @param theRight
	 */
	public void frame(
		final CCVector3 thePosition, 
		final CCVector3 theDirection, 
		final CCVector3 theUp, 
		final CCVector3 theRight
	) {
		_myPosition = thePosition;
		axes(theDirection, theUp, theRight);
	}
	
	public void lookAt(
		final CCVector3 thePosition, 
		final CCVector3 theTarget, 
		final CCVector3 theUp
	) {
		final CCVector3 myDirection = theTarget.clone().subtract(thePosition).normalize();
		CCVector3 myUp = theUp.clone().normalize();
		final CCVector3 myRight = myDirection.cross(myUp).normalize();
		myUp = myRight.cross(myDirection);
		
		frame(thePosition, myDirection, myUp, myRight);
	}

	/**
	 * 
	 * @param thePosition
	 */
	public void position(final CCVector3 thePosition) {
		_myPosition = thePosition;
		onFrameChange();
	}

	/**
	 * 
	 * @param theDirection
	 * @param theUp
	 * @param theRight
	 */
	public void axes(
		final CCVector3 theDirection, 
		final CCVector3 theUp, 
		final CCVector3 theRight
	) {
		_myDirection = theDirection;
		_myUp = theUp;
		_myRight = theRight;

		final double epsilon = 0.01f;
		double det = _myDirection.dot(_myUp.cross(_myRight));
		if (CCMath.abs(1.0f - det) > epsilon) {
			// #ifdef WM5_VALIDATE_CAMERA_FRAME_ONCE
			// if (mValidateCameraFrame)
			// {
			// mValidateCameraFrame = false;
			//
			// double lenD = mDVector.Length();
			// double lenU = mUVector.Length();
			// double lenR = mRVector.Length();
			// double dotDU = mDVector.Dot(mUVector);
			// double dotDR = mDVector.Dot(mRVector);
			// double dotUR = mUVector.Dot(mRVector);
			// if (Mathf::FAbs(1.0f - lenD) > epsilon
			// || Mathf::FAbs(1.0f - lenU) > epsilon
			// || Mathf::FAbs(1.0f - lenR) > epsilon
			// || Mathf::FAbs(dotDU) > epsilon
			// || Mathf::FAbs(dotDR) > epsilon
			// || Mathf::FAbs(dotUR) > epsilon)
			// {
			// assertion(false, "Camera frame is not orthonormal.\n");
			// }
			// }
			// #endif
			// The input vectors do not appear to form an orthonormal set. Time
			// to renormalize.
			CCVector3.orthonormalize(_myDirection, _myUp, _myRight);
		}

		onFrameChange();
	}

	/**
	 * Returns the camera position
	 * @return the camera position
	 */
	public CCVector3 position() {
		return _myPosition;
	}

	/**
	 * Returns the camera direction
	 * @return the camera direction
	 */
	public CCVector3 direction() {
		return _myDirection;
	}

	public CCVector3 up() {
		return _myUp;
	}

	public CCVector3 right() {
		return _myRight;
	}

	/**
	 * Access the view matrix of the camera. If D = (d0,d1,d2), U = (u0,u1,u2),
	 * and R = (r0,r1,r2), then the view matrix is
	 * 
	 * <pre>
	 * +- -+
	 * | r0 r1 r2 -Dot(R,P) |
	 * | u0 u1 u2 -Dot(U,P) |
	 * | d0 d1 d2 -Dot(D,P) |
	 * | 0 0 0 1 |
	 * +- -+
	 * </pre>
	 * 
	 * The view matrix multiplies vectors on its right, viewMat*vector4.
	 * @return the view matrix
	 */
	public final CCMatrix4x4 viewMatrix() {
		return _myViewMatrix;
	}

	/**
	 * The default view frustum has an up field-of-view of 90 degrees, an aspect
	 * ratio of 1, near value 1, and far value 10000.
	 * @return
	 */
	public boolean isPerspective() {
		return _myIsPerspective;
	}

	/**
	 * Set the view frustum. <ul>
	 * <li>The interval [rmin,rmax] is measured in the right
	 * direction R. These are the "left" and "right" frustum values.<li> The
	 * interval [umin,umax] is measured in the up direction U. These are the
	 * "bottom" and "top" values. <li>The interval [dmin,dmax] is measured in the
	 * view direction D. These are the "near" and "far" values.
	 * @param theDirectionMin near vaue
	 * @param theDirectionMax far value
	 * @param theUpMin bottom value
	 * @param theUpMax top value
	 * @param theRightMin left value
	 * @param theRightMax right value
	 */
	public void frustum(
		double theDirectionMin, 
		double theDirectionMax, 
		double theUpMin, 
		double theUpMax, 
		double theRightMin, 
		double theRightMax
	) {
		_myFrustum[CCFrustumPlane.NEAR.ordinal()] = theDirectionMin;
		_myFrustum[CCFrustumPlane.FAR.ordinal()] = theDirectionMax;
		_myFrustum[CCFrustumPlane.BOTTOM.ordinal()] = theUpMin;
		_myFrustum[CCFrustumPlane.TOP.ordinal()] = theUpMax;
		_myFrustum[CCFrustumPlane.LEFT.ordinal()] = theRightMin;
		_myFrustum[CCFrustumPlane.RIGHT.ordinal()] = theRightMax;

		onFrustumChange();
	}

	/**
	 * Set all the view frustum values simultaneously. The input array must have
	 * the order: dmin, dmax, umin, umax, rmin, rmax.
	 * @param theFrustum
	 */
	public void frustum(final double[] theFrustum) {
		for (CCFrustumPlane myPlane : CCFrustumPlane.values()) {
			_myFrustum[myPlane.ordinal()] = theFrustum[myPlane.ordinal()];
		}

		onFrustumChange();
	}

	/**
	 * Set a symmetric view frustum (umin = -umax, rmin = -rmax) using a field
	 * of view in the "up" direction and an aspect ratio "width/height". This
	 * call is the equivalent of gluPerspective in OpenGL. As such, the field of
	 * view in this function must be specified in degrees and be in the interval
	 * (0,180).
	 * @param theFov field of view in up direction in degrees
	 * @param theAspectRatio
	 * @param theDirectionMin near value
	 * @param theDirectionMax far value
	 */
	public void perpective(double theFov, double theAspectRatio, double theDirectionMin, double theDirectionMax) {
		final double halfAngleRadians =  CCMath.radians( (theFov / 2.0f) );
		final double range =  CCMath.tan(halfAngleRadians) * theDirectionMin;
		
		_myFrustum[CCFrustumPlane.TOP.ordinal()] = range;
		_myFrustum[CCFrustumPlane.BOTTOM.ordinal()] = -range;
		_myFrustum[CCFrustumPlane.RIGHT.ordinal()] = range * theAspectRatio;
		_myFrustum[CCFrustumPlane.LEFT.ordinal()] = -range * theAspectRatio;
		_myFrustum[CCFrustumPlane.NEAR.ordinal()] = theDirectionMin;
		_myFrustum[CCFrustumPlane.FAR.ordinal()] = theDirectionMax;

		onFrustumChange();
	}

	/**
	 * Get all the view frustum values simultaneously.
	 * @return view frustum values
	 */
	public final double[] frustum() {
		return _myFrustum;
	}

	/**
	 * Get the parameters for a symmetric view frustum. The return value is an
	 * array containing the fov in degrees, the aspect ratio and the near and far
	 * value if the current frustum is symmetric, in which case the output
	 * parameters are valid, otherwise it returns null
	 * @return parameters for a symmetric view frustum
	 */
	public double[] perspective() {
		if (
			_myFrustum[CCFrustumPlane.LEFT.ordinal()] != -_myFrustum[CCFrustumPlane.RIGHT.ordinal()] || 
			_myFrustum[CCFrustumPlane.BOTTOM.ordinal()] != -_myFrustum[CCFrustumPlane.TOP.ordinal()]
		) {
			return null;
		}

		double tmp = _myFrustum[CCFrustumPlane.TOP.ordinal()] / _myFrustum[CCFrustumPlane.NEAR.ordinal()];
		
		return new double[] { 
			2.0f * CCMath.atan(tmp) * CCMath.RAD_TO_DEG, // upFovDegrees
			_myFrustum[CCFrustumPlane.RIGHT.ordinal()] / _myFrustum[CCFrustumPlane.TOP.ordinal()], // aspectRatio
			_myFrustum[CCFrustumPlane.NEAR.ordinal()], // dMin
			_myFrustum[CCFrustumPlane.FAR.ordinal()] // dMax
		};

	}

	/**
	 * Get the individual frustum values.
	 */
	public double frustumNear() {
		return _myFrustum[CCFrustumPlane.NEAR.ordinal()];
	}

	public double frustumFar() {
		return _myFrustum[CCFrustumPlane.FAR.ordinal()];
	}

	public double frustumBottom() {
		return _myFrustum[CCFrustumPlane.BOTTOM.ordinal()];
	}

	public double frustumTop() {
		return _myFrustum[CCFrustumPlane.TOP.ordinal()];
	}

	public double frustumLeft() {
		return _myFrustum[CCFrustumPlane.LEFT.ordinal()];
	}

	public double frustumRight() {
		return _myFrustum[CCFrustumPlane.RIGHT.ordinal()];
	}

	/**
	 * The frustum values are N (near), F (far), B (bottom), T (top), L (left),
	 * and R (right). The various matrices are as follows.
	 * <ul>
	 * <li>perspective, depth [0,1]
	 * 
	 * <pre>
	 * +- -+
	 * | 2*N/(R-L) 0 -(R+L)/(R-L) 0 |
	 * | 0 2*N/(T-B) -(T+B)/(T-B) 0 |
	 * | 0 0 F/(F-N) -N*F/(F-N) |
	 * | 0 0 1 0 |
	 * +- -+
	 * </pre>
	 * 
	 * </li>
	 * <li>perspective, depth [-1,1]
	 * 
	 * <pre>
	 * +- -+
	 * | 2*N/(R-L) 0 -(R+L)/(R-L) 0 |
	 * | 0 2*N/(T-B) -(T+B)/(T-B) 0 |
	 * | 0 0 (F+N)/(F-N) -2*F*N/(F-N) |
	 * | 0 0 1 0
	 * +- -+
	 * </pre>
	 * 
	 * </li>
	 * <li>orthographic, depth [0,1]
	 * 
	 * <pre>
	 * +- -+
	 * | 2/(R-L) 0 0 -(R+L)/(R-L) |
	 * | 0 2/(T-B) 0 -(T+B)/(T-B) |
	 * | 0 0 1/(F-N) -N/(F-N) 0 |
	 * | 0 0 0 1 |
	 * +- -+
	 * </pre>
	 * 
	 * </li>
	 * <li>orthographic, depth [-1,1]
	 * 
	 * <pre>
	 * +- -+
	 * | 2/(R-L) 0 0 -(R+L)/(R-L) |
	 * | 0 2/(T-B) 0 -(T+B)/(T-B) |
	 * | 0 0 2/(F-N) -(F+N)/(F-N) |
	 * | 0 0 0 1 |
	 * +- -+
	 * </pre>
	 * 
	 * </li>
	 * </ul>
	 * 
	 * The projection matrix multiplies vectors on its right, projMat*vector4.
	 * 
	 * The returned matrix depends on the values of msDepthType and
	 * mIsPerspective.
	 * @return projection matrix
	 */
	public final CCMatrix4x4 projectionMatrix() {
		return _myProjectionMatrix;
	}

	/**
	 * Support for advanced effects. This allows you to set the projection matrix anyway you like.
	 * @param the projection matrix
	 */
	public void projectionMatrix(final CCMatrix4x4 theProjectionMatrix) {
		_myProjectionMatrix = theProjectionMatrix;
		updateProjectionViewMatrix();
	}

	/* The second function specifies a convex
	 * quadrilateral viewport. The points must be in camera coordinates and are
	 * counterclockwise ordered as viewed from the eyepoint. The plane of the
	 * quadrilateral is the view plane and has an "extrude" value of 1. The
	 * nearExtrude value is in (0,infinity); this specifies the fraction from
	 * the eyepoint to the view plane at which to place the near-face of the
	 * cuboidal view volume. The farExtrude value is in (nearExtrude,infinity);
	 * this specifies the fraction from the eyepoint at which to place the
	 * far-face of the cuboidal view volume.*/
	// public void SetProjectionMatrix (final CCVector3 p00, final CCVector3
	// p10,
	// final CCVector3 p11, final CCVector3 p01, double nearExtrude,
	// double farExtrude){
	// if(nearExtrude > 0.0f)CCLog.error("Invalid nearExtrude.\n");
	// if(farExtrude > nearExtrude)CCLog.error("Invalid farExtrude.\n");
	//
	// // Compute the near face of the view volume.
	// APoint q000 = APoint::ORIGIN + nearExtrude*(p00 - APoint::ORIGIN);
	// APoint q100 = APoint::ORIGIN + nearExtrude*(p10 - APoint::ORIGIN);
	// APoint q110 = APoint::ORIGIN + nearExtrude*(p11 - APoint::ORIGIN);
	// APoint q010 = APoint::ORIGIN + nearExtrude*(p01 - APoint::ORIGIN);
	//
	// // Compute the far face of the view volume.
	// APoint q001 = APoint::ORIGIN + farExtrude*(p00 - APoint::ORIGIN);
	// APoint q101 = APoint::ORIGIN + farExtrude*(p10 - APoint::ORIGIN);
	// APoint q111 = APoint::ORIGIN + farExtrude*(p11 - APoint::ORIGIN);
	// APoint q011 = APoint::ORIGIN + farExtrude*(p01 - APoint::ORIGIN);
	//
	// // Compute the representation of q111.
	// AVector u0 = q100 - q000;
	// AVector u1 = q010 - q000;
	// AVector u2 = q001 - q000;
	// HMatrix M(u0, u1, u2, q000, true);
	// HMatrix invM = M.Inverse();
	// APoint a = invM*q111;
	//
	// // Compute the coeffients in the fractional linear transformation.
	// // y[i] = n[i]*x[i]/(d[0]*x[0] + d[1]*x[1] + d[2]*x[2] + d[3])
	// double n0 = 2.0f*a[0];
	// double n1 = 2.0f*a[1];
	// double n2 = 2.0f*a[2];
	// double d0 = +a[0] - a[1] - a[2] + 1.0f;
	// double d1 = -a[0] + a[1] - a[2] + 1.0f;
	// double d2 = -a[0] - a[1] + a[2] + 1.0f;
	// double d3 = +a[0] + a[1] + a[2] - 1.0f;
	//
	// // Compute the perspective projection from the canonical cuboid to the
	// // canonical cube [-1,1]^2 x [0,1].
	// double n2divn0 = n2/n0;
	// double n2divn1 = n2/n1;
	// HMatrix project;
	// project[00 = n2divn0*(2.0f*d3 + d0);
	// project[01 = n2divn1*d1;
	// project[02 = d2;
	// project[03 = -n2;
	// project[10 = n2divn0*d0;
	// project[11 = n2divn1*(2.0f*d3 + d1);
	// project[12 = d2;
	// project[13 = -n2;
	//
	// if (mDepthType == PM_DEPTH_ZERO_TO_ONE)
	// {
	// project[20 = 0.0f;
	// project[21 = 0.0f;
	// project[22 = d3;
	// project[23 = 0.0f;
	// }
	// else
	// {
	// project[20 = n2divn0*d0;
	// project[21 = n2divn1*d1;
	// project[22 = 2.0f*d3 + d2;
	// project[23 = -n2;
	// }
	//
	// project[30 = -n2divn0*d0;
	// project[31 = -n2divn1*d1;
	// project[32 = -d2;
	// project[33 = n2;
	//
	// // The full projection requires mapping the extruded-quadrilateral view
	// // volume to the canonical cuboid, which is then followed by the
	// // perspective projection to the canonical cube.
	// SetProjectionMatrix(project*invM);
	// }
	
	/**
	 * The projection-view-world matrix is commonly used in the shader programs
	 * to transform model-space data to clip-space data. To avoid repeatedly
	 * computing the projection-view matrix for each geometric object, the
	 * product is stored and maintained in this class.
	 * @return the projection view matrix
	 */
	public final CCMatrix4x4 projectionViewMatrix() {
		return _myProjectionViewMatrix;
	}

	/**
	 * The preview matrix is applied after the model-to-world but before the
	 * view matrix. It is used for transformations such as reflections of world
	 * objects. The default value is the identity matrix.
	 * @param thePreViewMatrix the preview matrix
	 */
	public void preViewMatrix(final CCMatrix4x4 thePreViewMatrix) {
		_myPreViewMatrix = thePreViewMatrix;
		_myPreViewIsIdentity = _myPreViewMatrix.isIdentity();
		updateProjectionViewMatrix();
	}

	/**
	 * The preview matrix is applied after the model-to-world but before the
	 * view matrix. It is used for transformations such as reflections of world
	 * objects. The default value is the identity matrix.
	 * @return The preview matrix
	 */
	public final CCMatrix4x4 preViewMatrix() {
		return _myPreViewMatrix;
	}

	public boolean preViewIsIdentity() {
		return _myPreViewIsIdentity;
	}

	/**
	 * The postprojection matrix is used for screen-space transformations such
	 * as reflection of the rendered image. The default value is the identity
	 * matrix.
	 * @param thePostProjMatrix The postprojection matrix
	 */
	public void postProjectionMatrix(final CCMatrix4x4 thePostProjMatrix) {
		_myPostProjectionMatrix = thePostProjMatrix;
		_myPostProjectionIsIdentity = _myPostProjectionMatrix.isIdentity();
		updateProjectionViewMatrix();
	}

	public final CCMatrix4x4 postProjectionMatrix() {
		return _myPostProjectionMatrix;
	}

	public boolean postProjectionIsIdentity() {
		return _myPostProjectionIsIdentity;
	}

	// Compute the axis-aligned bounding rectangle in normalized display
	// // space, [-1,1]x[-1,1], for a set of vertices. The input 'vertices' is a
	// // pointer to triples of 'double' values, each triple representing a
	// vertex.
	// // The stride is the number of bytes from the first position channel of
	// one
	// // vertex to the first position channel of the next vertex. If the
	// // vertices are packed contiguously, the stride is 3*sizeof(double). The
	// // vertices are in model space. The input 'worldMatrix' transforms the
	// // model space to world space.
	// public void ComputeBoundingAABB (int numVertices, final char* vertices,
	// int stride, final CCMatrix4& worldMatrix, double& xmin, double& xmax,
	// double& ymin, double& ymax){
	// // Compute the current world-view-projection matrix.
	// HMatrix vpMatrix = GetProjectionMatrix()*GetViewMatrix();
	// if (!PostProjectionIsIdentity())
	// {
	// vpMatrix = GetPostProjectionMatrix()*vpMatrix;
	// }
	// HMatrix wvpMatrix = vpMatrix*worldMatrix;
	//
	// // Compute the bounding rectangle in normalized display coordinates.
	// xmin = Mathf::MAX_REAL;
	// xmax = -Mathf::MAX_REAL;
	// ymin = Mathf::MAX_REAL;
	// ymax = -Mathf::MAX_REAL;
	//
	// for (int i = 0; i < numVertices; ++i, vertices += stride)
	// {
	// const double* vertex = (const double*)vertices;
	// HPoint pos(vertex[0], vertex[1], vertex[2], 1.0f);
	// HPoint hpos = wvpMatrix*pos;
	// double invW = 1.0f/hpos[3];
	// double xNDC = hpos[0]*invW;
	// double yNDC = hpos[1]*invW;
	// if (xNDC < xmin)
	// {
	// xmin = xNDC;
	// }
	// if (xNDC > xmax)
	// {
	// xmax = xNDC;
	// }
	// if (yNDC < ymin)
	// {
	// ymin = yNDC;
	// }
	// if (yNDC > ymax)
	// {
	// ymax = yNDC;
	// }
	// }
	// }
	/**
	 * Compute the view matrix after the frame changes.
	 */
	protected void onFrameChange() {
		CCMatrix4x4.createFrame(_myPosition, _myDirection, _myUp, _myRight, _myViewMatrix);
		
		updateProjectionViewMatrix();
	}

	/**
	 * Compute the projection matrices after the frustum changes.
	 */
	protected void onFrustumChange() {

		if (_myIsPerspective) {
			CCMatrix4x4.createFrustum(
				_myFrustum[CCFrustumPlane.LEFT.ordinal()], 
				_myFrustum[CCFrustumPlane.RIGHT.ordinal()], 
				_myFrustum[CCFrustumPlane.BOTTOM.ordinal()], 
				_myFrustum[CCFrustumPlane.TOP.ordinal()], 
				_myFrustum[CCFrustumPlane.NEAR.ordinal()], 
				_myFrustum[CCFrustumPlane.FAR.ordinal()],
				_myProjectionMatrix
			);
		} else {
			CCMatrix4x4.createOrtho(
				_myFrustum[CCFrustumPlane.LEFT.ordinal()], 
				_myFrustum[CCFrustumPlane.RIGHT.ordinal()], 
				_myFrustum[CCFrustumPlane.BOTTOM.ordinal()], 
				_myFrustum[CCFrustumPlane.TOP.ordinal()], 
				_myFrustum[CCFrustumPlane.NEAR.ordinal()], 
				_myFrustum[CCFrustumPlane.FAR.ordinal()],
				_myProjectionMatrix
			);
		}

		updateProjectionViewMatrix();
	}

	/**
	 * Compute the product postproj-proj-view-preview.
	 */
	protected void updateProjectionViewMatrix() {

		_myProjectionViewMatrix = _myProjectionMatrix.multiply(_myViewMatrix);
		if (!_myPostProjectionIsIdentity) {
			_myProjectionViewMatrix = _myPostProjectionMatrix.multiply(_myProjectionViewMatrix);
		}
		if (!_myPreViewIsIdentity) {
			_myProjectionViewMatrix = _myProjectionViewMatrix.multiply(_myPreViewMatrix);
		}
	}
}

package cc.creativecomputing.graphics.scene;

import cc.creativecomputing.graphics.bounding.CCBoundingVolume;
import cc.creativecomputing.graphics.scene.CCCamera.CCFrustumPlane;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCPlane;
import cc.creativecomputing.math.CCPlane.Side;
import cc.creativecomputing.math.CCVector3;

public class CCCuller {

	// The input camera has information that might be needed during the
	// culling pass over the scene.
	protected CCCamera mCamera;

	// A copy of the view frustum for the input camera. This allows various
	// subsystems to change the frustum parameters during culling (for
	// example, the portal system) without affecting the camera, whose initial
	// state is needed by the renderer.
	protected double[] mFrustum = new double[CCFrustumPlane.values().length];

	// The world culling planes corresponding to the view frustum plus any
	// additional user-defined culling planes. The member m_uiPlaneState
	// represents bit flags to store whether or not a plane is active in the
	// culling system. A bit of 1 means the plane is active, otherwise the
	// plane is inactive. An active plane is compared to bounding volumes,
	// whereas an inactive plane is not. This supports an efficient culling
	// of a hierarchy. For example, if a node's bounding volume is inside
	// the left plane of the view frustum, then the left plane is set to
	// inactive because the children of the node are automatically all inside
	// the left plane.
	protected int mPlaneQuantity;
	protected CCPlane[] mPlane = new CCPlane[MAX_PLANE_QUANTITY];
	protected int mPlaneState;

	// The potentially visible set for a call to GetVisibleSet.
	protected CCVisibleSet mVisibleSet;

	// Construction and destruction. Culling requires a camera model. If the
	// camera is not passed to the constructor, you should set it using
	// SetCamera before calling ComputeVisibleSet.
	public CCCuller(CCCamera camera) {
		mCamera = camera;
		mPlaneQuantity = 6;

		// The data members mFrustum, mPlane, and mPlaneState are
		// uninitialized. They are initialized in the GetVisibleSet call.
	}

	// Access to the camera, frustum copy, and potentially visible set.
	public void SetCamera(CCCamera camera) {
		mCamera = camera;
	}

	public CCCamera GetCamera() {
		return mCamera;
	}

	public void SetFrustum(double[] frustum) {
		if (mCamera == null) {
			throw new RuntimeException("SetFrustum requires the existence of a camera");
		}

		// Copy the frustum values.
		mFrustum[CCFrustumPlane.NEAR.ordinal()] = frustum[CCFrustumPlane.NEAR.ordinal()];
		mFrustum[CCFrustumPlane.FAR.ordinal()] = frustum[CCFrustumPlane.FAR.ordinal()];
		mFrustum[CCFrustumPlane.BOTTOM.ordinal()] = frustum[CCFrustumPlane.BOTTOM.ordinal()];
		mFrustum[CCFrustumPlane.TOP.ordinal()] = frustum[CCFrustumPlane.TOP.ordinal()];
		mFrustum[CCFrustumPlane.LEFT.ordinal()] = frustum[CCFrustumPlane.LEFT.ordinal()];
		mFrustum[CCFrustumPlane.RIGHT.ordinal()] = frustum[CCFrustumPlane.RIGHT.ordinal()];

		double dMin2 = mFrustum[CCFrustumPlane.NEAR.ordinal()] * mFrustum[CCFrustumPlane.NEAR.ordinal()];
		double uMin2 = mFrustum[CCFrustumPlane.BOTTOM.ordinal()] * mFrustum[CCFrustumPlane.BOTTOM.ordinal()];
		double uMax2 = mFrustum[CCFrustumPlane.TOP.ordinal()] * mFrustum[CCFrustumPlane.TOP.ordinal()];
		double rMin2 = mFrustum[CCFrustumPlane.LEFT.ordinal()] * mFrustum[CCFrustumPlane.LEFT.ordinal()];
		double rMax2 = mFrustum[CCFrustumPlane.RIGHT.ordinal()] * mFrustum[CCFrustumPlane.RIGHT.ordinal()];

		// Get the camera coordinate frame.
		CCVector3 position = mCamera.position();
		CCVector3 dVector = mCamera.direction();
		CCVector3 uVector = mCamera.up();
		CCVector3 rVector = mCamera.right();
		double dirdotEye = position.dot(dVector);

		// Update the near plane.
		mPlane[CCFrustumPlane.NEAR.ordinal()].setNormal(dVector);
		mPlane[CCFrustumPlane.NEAR.ordinal()].setConstant(dirdotEye + mFrustum[CCFrustumPlane.NEAR.ordinal()]);

		// Update the far plane.
		mPlane[CCFrustumPlane.FAR.ordinal()].setNormal(dVector.negate());
		mPlane[CCFrustumPlane.FAR.ordinal()].setConstant(-(dirdotEye + mFrustum[CCFrustumPlane.FAR.ordinal()]));

		// Update the bottom plane
		double invLength = CCMath.invSqrt(dMin2 + uMin2);
		double c0 = -mFrustum[CCFrustumPlane.BOTTOM.ordinal()] * invLength; // D
																			// component
		double c1 = +mFrustum[CCFrustumPlane.NEAR.ordinal()] * invLength; // U
																			// component
		CCVector3 normal = dVector.multiply(c0).add(uVector.multiply(c1));
		double constant = position.dot(normal);
		mPlane[CCFrustumPlane.BOTTOM.ordinal()].setNormal(normal);
		mPlane[CCFrustumPlane.BOTTOM.ordinal()].setConstant(constant);

		// Update the top plane.
		invLength = CCMath.invSqrt(dMin2 + uMax2);
		c0 = +mFrustum[CCFrustumPlane.TOP.ordinal()] * invLength; // D
																		// component
		c1 = -mFrustum[CCFrustumPlane.NEAR.ordinal()] * invLength; // U
																		// component
		normal = dVector.multiply(c0).add(uVector.multiply(c1));
		constant = position.dot(normal);
		mPlane[CCFrustumPlane.TOP.ordinal()].setNormal(normal);
		mPlane[CCFrustumPlane.TOP.ordinal()].setConstant(constant);

		// Update the left plane.
		invLength = CCMath.invSqrt(dMin2 + rMin2);
		c0 = -mFrustum[CCFrustumPlane.LEFT.ordinal()] * invLength; // D
																		// component
		c1 = +mFrustum[CCFrustumPlane.NEAR.ordinal()] * invLength; // R
																		// component
		normal = dVector.multiply(c0).add(rVector.multiply(c1));
		constant = position.dot(normal);
		mPlane[CCFrustumPlane.LEFT.ordinal()].setNormal(normal);
		mPlane[CCFrustumPlane.LEFT.ordinal()].setConstant(constant);

		// Update the right plane.
		invLength = CCMath.invSqrt(dMin2 + rMax2);
		c0 = +mFrustum[CCFrustumPlane.RIGHT.ordinal()] * invLength; // D
																		// component
		c1 = -mFrustum[CCFrustumPlane.NEAR.ordinal()] * invLength; // R
																		// component
		normal = dVector.multiply(c0).add(rVector.multiply(c1));
		constant = position.dot(normal);
		mPlane[CCFrustumPlane.RIGHT.ordinal()].setNormal(normal);
		mPlane[CCFrustumPlane.RIGHT.ordinal()].setConstant(constant);

		// All planes are active initially.
		mPlaneState = 0xFFFFFFFF;
	}

	public double[] GetFrustum() {
		return mFrustum;
	}

	public CCVisibleSet GetVisibleSet() {
		return mVisibleSet;
	}

	// The base class behavior is to append the visible object to the end of
	// the visible set (stored as an array). Derived classes may override
	// this behavior; for example, the array might be maintained as a sorted
	// array for minimizing render state changes or it might be/ maintained
	// as a unique list of objects for a portal system.
	public void Insert(CCSpatial visible) {
		mVisibleSet.Insert(visible);
	}

	// Access to the stack of world culling planes. You may push and pop
	// planes to be used in addition to the view frustum planes. PushPlane
	// requires the input plane to be in world coordinates. See the comments
	// before data member mPlaneState about the bit system for enabling and
	// disabling planes during culling.
	public static final int MAX_PLANE_QUANTITY = 32;

	public int GetPlaneQuantity() {
		return mPlaneQuantity;
	}

	public CCPlane[] GetPlanes() {
		return mPlane;
	}

	public void SetPlaneState(int planeState) {
		mPlaneState = planeState;
	}

	public int GetPlaneState() {
		return mPlaneState;
	}

	public void PushPlane(CCPlane plane) {
		if (mPlaneQuantity >= MAX_PLANE_QUANTITY)
			return;
		// The number of user-defined planes is limited.
		mPlane[mPlaneQuantity] = plane;
		++mPlaneQuantity;
	}

	public void PopPlane() {
		if (mPlaneQuantity <= CCFrustumPlane.values().length)
			return;

		// Frustum planes may not be removed from the stack.
		--mPlaneQuantity;

	}

	// Compare the object's world bound against the culling planes. Only
	// Spatial calls this function.
	public boolean IsVisible(CCBoundingVolume bound) {
		if (bound.radius() == 0.0f) {
			// The node is a dummy node and cannot be visible.
			return false;
		}

		// Start with the last pushed plane, which is potentially the most
		// restrictive plane.
		int index = mPlaneQuantity - 1;
		int mask = (1 << index);

		for (int i = 0; i < mPlaneQuantity; ++i, --index, mask >>= 1) {
			if ((mPlaneState & mask) > 0) {
				CCPlane.Side side = bound.whichSide(mPlane[index]);

				if (side == Side.Inside) {
					// The object is on the negative side of the plane, so
					// cull it.
					return false;
				}

				if (side == Side.Outside) {
					// The object is on the positive side of plane. There is
					// no need to compare subobjects against this plane, so
					// mark it as inactive.
					mPlaneState &= ~mask;
				}
			}
		}

		return true;
	}

	// Support for Portal::GetVisibleSet.
	public boolean IsVisible(int numVertices, CCVector3[] vertices, boolean ignoreNearPlane) {
		// The Boolean variable ignoreNearPlane should be set to 'true' when
		// the test polygon is a portal. This avoids the situation when the
		// portal is in the view pyramid (eye+left/right/top/bottom), but is
		// between the eye and near plane. In such a situation you do not want
		// the portal system to cull the portal. This situation typically occurs
		// when the camera moves through the portal from current region to
		// adjacent region.

		// Start with the last pushed plane, which is potentially the most
		// restrictive plane.
		int index = mPlaneQuantity - 1;
		for (int i = 0; i < mPlaneQuantity; ++i, --index) {
			CCPlane plane = mPlane[index];
			if (ignoreNearPlane && index == CCFrustumPlane.NEAR.ordinal()) {
				continue;
			}

			int j;
			for (j = 0; j < numVertices; ++j) {
				Side side = plane.whichSide(vertices[j]);
				if (side == Side.Inside) {
					// The polygon is not totally outside this plane.
					break;
				}
			}

			if (j == numVertices) {
				// The polygon is totally outside this plane.
				return false;
			}
		}

		return true;
	}

	// Support for BspNode::GetVisibleSet. Determine whether the view frustum
	// is fully on one side of a plane. The "positive side" of the plane is
	// the half space to which the plane normal points. The "negative side"
	// is the other half space. The function returns +1 if the view frustum
	// is fully on the positive side of the plane, -1 if the view frustum is
	// fully on the negative side of the plane, or 0 if the view frustum
	// straddles the plane. The input plane is in world coordinates and the
	// world camera coordinate system is used for the test.
	public int WhichSide(CCPlane plane) {
		// The plane is N*(X-C) = 0 where the * indicates dot product. The
		// signed
		// distance from the camera location E to the plane is N*(E-C).
		double NdEmC = plane.distance(mCamera.position());

		CCVector3 normal = plane.getNormal();
		double NdD = normal.dot(mCamera.direction());
		double NdU = normal.dot(mCamera.up());
		double NdR = normal.dot(mCamera.right());
		double FdN = mFrustum[CCFrustumPlane.FAR.ordinal()] / mFrustum[CCFrustumPlane.NEAR.ordinal()];

		int positive = 0, negative = 0;
		double sgnDist;

		// Check near-plane vertices.
		double PDMin = mFrustum[CCFrustumPlane.NEAR.ordinal()] * NdD;
		double NUMin = mFrustum[CCFrustumPlane.BOTTOM.ordinal()] * NdU;
		double NUMax = mFrustum[CCFrustumPlane.TOP.ordinal()] * NdU;
		double NRMin = mFrustum[CCFrustumPlane.LEFT.ordinal()] * NdR;
		double NRMax = mFrustum[CCFrustumPlane.RIGHT.ordinal()] * NdR;

		// V = E + dmin*D + umin*U + rmin*R
		// N*(V-C) = N*(E-C) + dmin*(N*D) + umin*(N*U) + rmin*(N*R)
		sgnDist = NdEmC + PDMin + NUMin + NRMin;
		if (sgnDist > 0.0f) {
			positive++;
		} else if (sgnDist < 0.0f) {
			negative++;
		}

		// V = E + dmin*D + umin*U + rmax*R
		// N*(V-C) = N*(E-C) + dmin*(N*D) + umin*(N*U) + rmax*(N*R)
		sgnDist = NdEmC + PDMin + NUMin + NRMax;
		if (sgnDist > 0.0f) {
			positive++;
		} else if (sgnDist < 0.0f) {
			negative++;
		}

		// V = E + dmin*D + umax*U + rmin*R
		// N*(V-C) = N*(E-C) + dmin*(N*D) + umax*(N*U) + rmin*(N*R)
		sgnDist = NdEmC + PDMin + NUMax + NRMin;
		if (sgnDist > 0.0f) {
			positive++;
		} else if (sgnDist < 0.0f) {
			negative++;
		}

		// V = E + dmin*D + umax*U + rmax*R
		// N*(V-C) = N*(E-C) + dmin*(N*D) + umax*(N*U) + rmax*(N*R)
		sgnDist = NdEmC + PDMin + NUMax + NRMax;
		if (sgnDist > 0.0f) {
			positive++;
		} else if (sgnDist < 0.0f) {
			negative++;
		}

		// check far-plane vertices (s = dmax/dmin)
		double PDMax = mFrustum[CCFrustumPlane.FAR.ordinal()] * NdD;
		double FUMin = FdN * NUMin;
		double FUMax = FdN * NUMax;
		double FRMin = FdN * NRMin;
		double FRMax = FdN * NRMax;

		// V = E + dmax*D + umin*U + rmin*R
		// N*(V-C) = N*(E-C) + dmax*(N*D) + s*umin*(N*U) + s*rmin*(N*R)
		sgnDist = NdEmC + PDMax + FUMin + FRMin;
		if (sgnDist > 0.0f) {
			positive++;
		} else if (sgnDist < 0.0f) {
			negative++;
		}

		// V = E + dmax*D + umin*U + rmax*R
		// N*(V-C) = N*(E-C) + dmax*(N*D) + s*umin*(N*U) + s*rmax*(N*R)
		sgnDist = NdEmC + PDMax + FUMin + FRMax;
		if (sgnDist > 0.0f) {
			positive++;
		} else if (sgnDist < 0.0f) {
			negative++;
		}

		// V = E + dmax*D + umax*U + rmin*R
		// N*(V-C) = N*(E-C) + dmax*(N*D) + s*umax*(N*U) + s*rmin*(N*R)
		sgnDist = NdEmC + PDMax + FUMax + FRMin;
		if (sgnDist > 0.0f) {
			positive++;
		} else if (sgnDist < 0.0f) {
			negative++;
		}

		// V = E + dmax*D + umax*U + rmax*R
		// N*(V-C) = N*(E-C) + dmax*(N*D) + s*umax*(N*U) + s*rmax*(N*R)
		sgnDist = NdEmC + PDMax + FUMax + FRMax;
		if (sgnDist > 0.0f) {
			positive++;
		} else if (sgnDist < 0.0f) {
			negative++;
		}

		if (positive > 0) {
			if (negative > 0) {
				// Frustum straddles the plane.
				return 0;
			}

			// Frustum is fully on the positive side.
			return +1;
		}

		// Frustum is fully on the negative side.
		return -1;
	}

	// This is the main function you should use for culling within a scene
	// graph. Traverse the scene graph and construct the potentially visible
	// set relative to the world planes.
	public void ComputeVisibleSet(CCSpatial scene) {
		if (mCamera == null || scene == null)
			throw new RuntimeException("A camera and a scene are required for culling");
		SetFrustum(mCamera.frustum());
		mVisibleSet.Clear();
		scene.onGetVisibleSet(this, false);
	}

}

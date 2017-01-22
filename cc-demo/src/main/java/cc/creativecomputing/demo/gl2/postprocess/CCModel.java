package cc.creativecomputing.demo.gl2.postprocess;

import cc.creativecomputing.math.CCMatrix3x3;
import cc.creativecomputing.math.CCMatrix4x4;

public class CCModel {
	protected CCMatrix4x4 mModelMatrix = new CCMatrix4x4();
	protected CCMatrix3x3 mNormalMatrix = new CCMatrix3x3();

	public CCModel modelMatrix(CCMatrix4x4 m) {
		mModelMatrix = m;
		return this;
	}

	public CCModel normalMatrix(CCMatrix3x3 m) {
		mNormalMatrix = m;
		return this;
	}

	public CCMatrix4x4 getModelMatrix() {
		return mModelMatrix;
	}

	public CCMatrix3x3 getNormalMatrix() {
		return mNormalMatrix;
	}

	public void setModelMatrix(CCMatrix4x4 m) {
		mModelMatrix = m;
	}

	public void setNormalMatrix(CCMatrix3x3 m) {
		mNormalMatrix = m;
	}
}

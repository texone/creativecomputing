package opengl4experiments.utils;

import static cc.creativecomputing.gl4.GLBufferUtil.frustum;
import static cc.creativecomputing.gl4.GLBufferUtil.loadIdentity;
import static cc.creativecomputing.gl4.GLBufferUtil.rotate;
import static cc.creativecomputing.gl4.GLBufferUtil.translate;
import cc.creativecomputing.gl4.GLBufferUtil;

public class Camera {
	private static final float DEG_TO_RAD = (float) Math.PI / 180f;
	// code from http://antongerdelan.net/opengl/virtualcamera.html
	private float[] view_matrix = loadIdentity();
	private float[] projection_matrix = loadIdentity();
	private float[] translation_matrix = loadIdentity();
	private float[] rotation_matrix = loadIdentity();

	float[] cam_pos = new float[3];
	float cam_yaw = 0f, cam_pitch = 0f;

	public Camera(float pos_x, float pos_y, float pos_z, float aspect) {
		cam_pos = new float[]{pos_x, pos_y, pos_z};
		updateProjectionMatrix(aspect);
		updateViewMatrix();
	}
	
	public float[] getPos() {
		return cam_pos;
	}

	public void translate(float dx, float dy, float dz) {
		cam_pos[0] += dx;
		cam_pos[1] += dy;
		cam_pos[2] += dz;
		updateViewMatrix();
	}

	public void yaw(float dy) {
		cam_yaw += dy;
		updateViewMatrix();
	}

	public void pitch(float dx) {
		cam_pitch += dx;
		updateViewMatrix();
	}

	private void updateViewMatrix() {
		rotation_matrix = rotate(loadIdentity(), cam_pitch, 1, 0, 0);
		rotation_matrix = rotate(rotation_matrix, cam_yaw, 0, 1, 0);
		translation_matrix = GLBufferUtil.translate(loadIdentity(), cam_pos[0],
				cam_pos[1], cam_pos[2]);
		view_matrix = GLBufferUtil.multiply(translation_matrix, rotation_matrix);
	}

	
	public void updateProjectionMatrix(float aspect) {
		float near = 0.1f; // clipping plane
		float far = 100.0f; // clipping plane
		float fov = 67.0f * DEG_TO_RAD; // convert 67 degrees to radians
		// matrix components
		float range = (float) Math.tan(fov * 0.5f) * near;
		float Sx = (2.0f * near) / (range / aspect + range / aspect);
		float Sy = near / range;
		float Sz = -(far + near) / (far - near);
		float Pz = -(2.0f * far * near) / (far - near);
		projection_matrix = new float[] { Sx, 0.0f, 0.0f, 0.0f, 0.0f, Sy, 0.0f,
				0.0f, 0.0f, 0.0f, Sz, -1.0f, 0.0f, 0.0f, Pz, 0.0f };
	}

	
	
	/*
	public void updateProjectionMatrix(float aspect) {
		projection_matrix = frustum(cam_pos[0]-1f,1f-cam_pos[0],-aspect*(cam_pos[1]-1),aspect*(1f-cam_pos[1]),1f,500f);
	}
	*/
	
	public float[] getProjectionMatrix() {
		return projection_matrix;
	}

	public float[] getViewMatrix() {
		return view_matrix;
	}

}

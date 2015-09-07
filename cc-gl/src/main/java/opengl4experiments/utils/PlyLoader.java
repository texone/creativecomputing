package opengl4experiments.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class PlyLoader {

	private IntBuffer indexBuffer;
	private FloatBuffer vertexBuffer, normalBuffer;

	public PlyLoader(String path) {

		try {
			BufferedReader theReader;
			theReader = new BufferedReader(new FileReader(path));
			String line = theReader.readLine();
			int vertexCount = -1;
			int faceCount = -1;
			while (vertexCount == -1 && line != null) {
				if (line.startsWith("element vertex")) {
					vertexCount = getParam(line);
				} else {
					line = theReader.readLine();
				}
			}
			while (faceCount == -1 && line != null) {
				if (line.startsWith("element face")) {
					faceCount = getParam(line);
				} else {
					line = theReader.readLine();
				}
			}
			if (faceCount == -1 || vertexCount == -1) {
				throw new IOException();
			}
			while (!line.equalsIgnoreCase("end_header") && line != null) {
				line = theReader.readLine();
			}
			if (line == null) {
				throw new IOException();
			}
//			System.out.println("face count: " + faceCount);
//			System.out.println("vertex count: " + vertexCount);
			
			float[][] vertices = new float[vertexCount][3];
			float[][] normals = new float[vertexCount][3];
			int[][] faces = new int[faceCount][3];

			for (int i = 0; i < vertexCount; i++) {
				line = theReader.readLine();
				String[] tokens = line.split(" ");
				for (int j = 0; j < 3; j++) {
					String token = tokens[j];
					float coord = Float.parseFloat(token);
					vertices[i][j] = coord;
//					System.out.print(coord + "\t");
				}
//				System.out.println();
			}
//			System.out.println("----");

			for (int i = 0; i < faceCount; i++) {
				line = theReader.readLine();
				String[] tokens = line.split(" ");
				int elemnentsPerFace = Integer.parseInt(tokens[0]);
				if (elemnentsPerFace == 3) {
					for (int j = 1; j < 4; j++) {
						String token = tokens[j];
						int index = Integer.parseInt(token);
						faces[i][j-1] = index;
//						System.out.print(index + "\t");
					}
//					System.out.println();
				}
			}
//			System.out.println("----");
			
			for (int i = 0; i < faceCount; i++) {
				float[] a = new float[3];
				float[] b = new float[3];
				float[] c = new float[3];
				
				a = vertices[faces[i][0]];
				b = vertices[faces[i][1]];
				c = vertices[faces[i][2]];
				
				float[] u = new float[3];
				float[] v = new float[3];
				for (int j = 0; j < 3; j++) {
					u[j] = a[j] - b[j];
					v[j] = c[j] - b[j];
				}
				
				float[] n = new float[3];
				n[0] = u[1]*v[2] - u[2]*v[1];
				n[1] = u[2]*v[0] - u[0]*v[2];
				n[2] = u[0]*v[1] - u[1]*v[0];
				float mag = (float)Math.sqrt(n[0]*n[0] + n[1]*n[1] + n[2]*n[2]); 
				 n[0] /= -mag;
				 n[1] /= -mag;
				 n[2] /= -mag;
				
				normals[faces[i][0]] = n;
				normals[faces[i][1]] = n;
				normals[faces[i][2]] = n;
			}			
			
			normalBuffer = FloatBuffer.allocate(normals.length*normals[0].length);
			vertexBuffer = FloatBuffer.allocate(vertices.length*vertices[0].length);
			indexBuffer = IntBuffer.allocate(faces.length*faces[0].length);
			vertexBuffer.rewind();
			normalBuffer.rewind();
			indexBuffer.rewind();
			
			for (int i = 0; i < vertices.length; i++) {
				for (int j = 0; j < vertices[0].length; j++) {
					vertexBuffer.put(vertices[i][j]);
					normalBuffer.put(normals[i][j]);
				}
			}
			
			for (int i = 0; i < faces.length; i++) {
				for (int j = 0; j < faces[0].length; j++) {
					indexBuffer.put(faces[i][j]);
				}
			}
			
			System.out.println("vertex count: " + vertexBuffer.capacity()/3);
			System.out.println("normal count: " + normalBuffer.capacity()/3);
			System.out.println("face count: " + indexBuffer.capacity()/3);

			vertexBuffer.rewind();
			normalBuffer.rewind();
			indexBuffer.rewind();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public IntBuffer getIndices() {
		return indexBuffer;
	}
	public FloatBuffer getNormals() {
		return normalBuffer;
	}
	public FloatBuffer getVertices() {
		return vertexBuffer;
	}

	private int getParam(String theLine) {
		int result = -1;
		String[] tokens = theLine.split(" ");
		try {
			result = Integer.parseInt(tokens[tokens.length - 1]);
		} catch (Exception e) {

		}
		return result;
	}

}

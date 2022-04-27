package me.earth.earthhack.impl.util.physics;

import com.jme3.bullet.collision.shapes.infos.IndexedMesh;
import org.joml.Vector3f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Used for subdividing rectangular prisms into triangles to be used with bullet.
 * @author megyn
 */
public class BulletMeshUtil
{

	/**
	 * Subdivides rectangular prism with given corner with given number intermediate vertices.
	 * This is mostly useful for more accurate physical simulations of simple convex shapes.
	 * @param vertices corners of the rectangular prism
	 * @return vertices of subdivided mesh
	 */
	public static Vector3f[] subdivideRectPrism(Vector3f vertices, int subdivisions)
	{
		return new Vector3f[0];
	}

	/**
	 * Creates a cloth mesh of given width (z) and length (x)
	 * @return vertices of subdivided mesh
	 */
	public static Vector3f[] createCloth(int xSubdivisions, int zSubdivisions, float width, float length)
	{
		Vector3f[] vertices = new Vector3f[xSubdivisions * zSubdivisions];
		float xInterval = length / (float) xSubdivisions;
		float zInterval = width / (float) zSubdivisions;
		for (float x = 0; x < length; x += xInterval)
		{
			for (float z = 0; z < width; z += zInterval)
			{

			}
		}
		return new Vector3f[0];
	}

	public static com.jme3.math.Vector3f[] vertexBufferToArray(FloatBuffer buffer, int numVertices)
	{
		com.jme3.math.Vector3f[] toReturn = new com.jme3.math.Vector3f[numVertices];
		for (int i = 0; i < numVertices * 3; i += 3)
		{
			float x = buffer.get(i);
			float y = buffer.get(i + 1);
			float z = buffer.get(i + 2);
			toReturn[i / 3] = new com.jme3.math.Vector3f(x, y, z);
		}
		return toReturn;
	}

	/**
	 * Useful for converting AiMesh to IndexedMesh
	 */
	public static IndexedMesh meshFromBuffers(FloatBuffer vertexBuffer, int numVertices, IntBuffer indexBuffer)
	{
		int[] indices = indexBuffer.array();
		com.jme3.math.Vector3f[] vertices = vertexBufferToArray(vertexBuffer, numVertices);
		return new IndexedMesh(vertices, indices);
	}

}
package me.earth.earthhack.impl.util.math;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;

import java.nio.FloatBuffer;

/**
 * Various functions operating on quaternions and matrices
 */
public class GlMathUtil
{

	/**
	 * Normalized linear interpolation of quaternions
	 * @return normalized linearly interpolated quaternion
	 */
	public static Quaternionf normalizedLerp(Quaternionf a, Quaternionf b, float blend)
	{
		Quaternionf result = new Quaternionf(0, 0, 0, 1);
		float dot = a.w * b.w + a.x * b.x + a.y * b.y + a.z * b.z;
		float blendI = 1f - blend;
		if (dot < 0)
		{
			result.w = blendI * a.w + blend * -b.w;
			result.x = blendI * a.x + blend * -b.x;
			result.y = blendI * a.y + blend * -b.y;
			result.z = blendI * a.z + blend * -b.z;
		}
		else
		{
			result.w = blendI * a.w + blend * b.w;
			result.x = blendI * a.x + blend * b.x;
			result.y = blendI * a.y + blend * b.y;
			result.z = blendI * a.z + blend * b.z;
		}
		result.normalize();
		return result;
	}

	public static Matrix4f jomlMatrixToLwjglMatrix(org.joml.Matrix4f matrix)
	{
		return (Matrix4f) new Matrix4f().load(matrix.get(FloatBuffer.allocate(16)));
	}

	public static org.joml.Matrix4f lwjglMatrixToJomlMatrix(Matrix4f matrix)
	{
		return new org.joml.Matrix4f(
				matrix.m00, matrix.m01, matrix.m02, matrix.m03,
				matrix.m10, matrix.m11, matrix.m12, matrix.m13,
				matrix.m20, matrix.m21, matrix.m22, matrix.m23,
				matrix.m30, matrix.m31, matrix.m32, matrix.m33
		);
	}

	public static Quaternion jomlQuaternionToLwjglQuaternion(Quaternionf quaternion)
	{
		return new Quaternion(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
	}

	public static Matrix4f initScale(Vector3f scale)
	{
		Matrix4f matrix = new Matrix4f(); // load identity
		matrix.m00 = scale.x;	matrix.m01 = 0;			matrix.m02 = 0;			matrix.m03 = 0;
		matrix.m10 = 0;			matrix.m11 = scale.y;	matrix.m12 = 0;			matrix.m13 = 0;
		matrix.m20 = 0;			matrix.m21 = 0;			matrix.m22 = scale.z;	matrix.m23 = 0;
		matrix.m30 = 0;			matrix.m31 = 0;			matrix.m32 = 0;			matrix.m33 = 1;
		return matrix;
	}

	public static org.joml.Matrix4f initScaleJoml(Vector3f scale)
	{
		return new org.joml.Matrix4f(
				scale.x, 0, 0, 0,
				0, scale.y, 0, 0,
				0, 0, scale.z, 0,
				0, 0, 0, 1
		);
	}

	public static Matrix4f toRotationMatrix(Quaternionf quaternion)
	{
		Vector3f forward =  new Vector3f(2.0f * (quaternion.x * quaternion.z - quaternion.w * quaternion.y), 2.0f * (quaternion.y * quaternion.z + quaternion.w * quaternion.x), 1.0f - 2.0f * (quaternion.x * quaternion.x + quaternion.y * quaternion.y));
		Vector3f up = new Vector3f(2.0f * (quaternion.x * quaternion.y + quaternion.w * quaternion.z), 1.0f - 2.0f * (quaternion.x * quaternion.x + quaternion.z * quaternion.z), 2.0f * (quaternion.y * quaternion.z - quaternion.w * quaternion.x));
		Vector3f right = new Vector3f(1.0f - 2.0f * (quaternion.y * quaternion.y + quaternion.z * quaternion.z), 2.0f * (quaternion.x * quaternion.y - quaternion.w * quaternion.z), 2.0f * (quaternion.x * quaternion.z + quaternion.w * quaternion.y));

		Matrix4f matrix = new Matrix4f();
		matrix.m00 = right.x;	matrix.m01 = right.y;	matrix.m02 = right.z;	matrix.m03 = 0;
		matrix.m10 = up.x;	    matrix.m11 = up.y;	    matrix.m12 = up.z;	    matrix.m13 = 0;
		matrix.m20 = forward.x;	matrix.m21 = forward.y;	matrix.m22 = forward.z;	matrix.m23 = 0;
		matrix.m30 = 0;	        matrix.m31 = 0;	        matrix.m32 = 0;	        matrix.m33 = 1;
		return matrix;
	}

	public static org.joml.Matrix4f toRotationMatrixJoml(Quaternionf quaternion)
	{
		Vector3f forward =  new Vector3f(2.0f * (quaternion.x * quaternion.z - quaternion.w * quaternion.y), 2.0f * (quaternion.y * quaternion.z + quaternion.w * quaternion.x), 1.0f - 2.0f * (quaternion.x * quaternion.x + quaternion.y * quaternion.y));
		Vector3f up = new Vector3f(2.0f * (quaternion.x * quaternion.y + quaternion.w * quaternion.z), 1.0f - 2.0f * (quaternion.x * quaternion.x + quaternion.z * quaternion.z), 2.0f * (quaternion.y * quaternion.z - quaternion.w * quaternion.x));
		Vector3f right = new Vector3f(1.0f - 2.0f * (quaternion.y * quaternion.y + quaternion.z * quaternion.z), 2.0f * (quaternion.x * quaternion.y - quaternion.w * quaternion.z), 2.0f * (quaternion.x * quaternion.z + quaternion.w * quaternion.y));
		return new org.joml.Matrix4f(
				right.x, right.y, right.z, 0,
				up.x, up.y, up.z, 0,
				forward.x, forward.y, forward.z, 0,
				0, 0, 0, 1
		);
	}

	public static Matrix4f initTranslation(Vector3f translation)
	{
		Matrix4f matrix = new Matrix4f();
		matrix.m00 = 0;			matrix.m01 = 0;			matrix.m02 = 0;			matrix.m03 = translation.x;
		matrix.m10 = 0;			matrix.m11 = 0;			matrix.m12 = 0;			matrix.m13 = translation.y;
		matrix.m20 = 0;			matrix.m21 = 0;			matrix.m22 = 0;			matrix.m23 = translation.z;
		matrix.m30 = 0;			matrix.m31 = 0;			matrix.m32 = 0;			matrix.m33 = 1;
		return matrix;
	}

	public static org.joml.Matrix4f initTranslationJoml(Vector3f translation)
	{
		return new org.joml.Matrix4f(
				0, 0, 0, translation.x,
				0, 0, 0, translation.y,
				0, 0, 0, translation.z,
				0, 0, 0, 1
		);
	}

}

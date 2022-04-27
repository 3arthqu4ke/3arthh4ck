package me.earth.earthhack.impl.util.physics;

import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.math.Vector3f;
import me.earth.earthhack.api.util.interfaces.Globals;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import org.joml.Matrix3f;

/**
 * Class representing a group of rigid bodies correlating to the boxes present in an entity's body.
 * TODO: find a way to change the scale of the collision box
 * @author megyn
 */
public class ModelBaseRigidBodyWrapper implements Globals
{

	public ModelBaseRigidBodyWrapper(ModelBase model)
	{
		/* Each ModelRenderer should be represented by a CompoundCollisionShape containing rectangular prisms for each box present*/
		for (ModelRenderer renderer : model.boxList)
		{
			CompoundCollisionShape compoundShape = new CompoundCollisionShape();
			for (ModelBox box : renderer.cubeList)
			{
				// these should always be positive, but just in case
				float xWidth = Math.abs(box.posX2 - box.posX1);
				float yHeight = Math.abs(box.posY2 - box.posY1);
				float zWidth = Math.abs(box.posZ2 - box.posZ1);
				BoxCollisionShape shape = new BoxCollisionShape(xWidth / 2.0f, yHeight / 2.0f, zWidth / 2.0f);
				shape.setMargin(0.001f);
				Vector3f positionVector = new Vector3f(box.posX1 + xWidth / 2.0f, box.posY1 + yHeight / 2.0f, box.posZ1 + zWidth / 2.0f);
				/*Matrix3f jomlMatrix = new Matrix3f(); // rotate this using conventional, lwjgl-esque rotation functions
				com.jme3.math.Matrix3f finalMatrix = new com.jme3.math.Matrix3f(); // and then store those matrix values in this*/
				compoundShape.addChildShape(shape, positionVector);
				// body.
			}

			compoundShape.setScale(new Vector3f(0.0625f, 0.0625f, 0.0625f));
			Matrix3f jomlMatrix = new Matrix3f(); // rotate this
			// jomlMatrix.transform(new org.joml.Vector3f(renderer.rotationPointX, renderer.rotationPointY, renderer.rotationPointZ));
			// jomlMatrix.rotateLocal()


			// compoundShape.rotate();
		}
	}

}

package net.fexcraft.mod.fvtm.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.frl.Polygon;
import net.fexcraft.lib.frl.Polyhedron;
import net.fexcraft.lib.frl.Renderer;
import net.fexcraft.lib.frl.Vertex;
import net.fexcraft.mod.fvtm.model.GLObject;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Renderer120 extends Renderer<GLObject> {

	public static Vector3f AY = new Vector3f(0, 1, 0);
	public static Vector3f AX = new Vector3f(1, 0, 0);
	public static Vector3f AZ = new Vector3f(0, 0, 1);
	public static PoseStack pose;
	public static MultiBufferSource buffer;
	public static RenderType rentype;
	public static VertexConsumer cons;
	public static final Vec3f DEFCOLOR = new Vec3f(1, 1, 1);
	private static Vec3f color = new Vec3f();
	public static int light;

	public static void setColor(RGB col){
		float[] arr = col.toFloatArray();
		color.x = arr[0];
		color.y = arr[1];
		color.z = arr[2];
	}

	public static void setColor(Vec3f col){
		color.copy(col);
	}

	public static void resetColor(){
		color.x = color.y = color.z = 1;
	}

	public static void rotateDeg(float by, Vector3f axe){
		pose.mulPose(new Quaternionf().rotateAxis(Static.toRadians(by), axe));
	}

	public static void rotateRad(float by, Vector3f axe){
		pose.mulPose(new Quaternionf().rotateAxis(by, axe));
	}

	public static void pushPose(){
		pose.pushPose();
	}

	public static void popPose(){
		pose.popPose();
	}

	public void render(Polyhedron<GLObject> poly){
		if(!poly.visible) return;
		pose.pushPose();
		pose.translate(poly.posX, poly.posY, poly.posZ);
		if(poly.rotX != 0.0F || poly.rotY != 0.0F || poly.rotZ != 0.0F){
			pose.mulPose(new Quaternionf()
				.rotateAxis(Static.toRadians(poly.rotY), AY)
				.rotateAxis(Static.toRadians(poly.rotX), AX)
				.rotateAxis(Static.toRadians(poly.rotZ), AZ)
			);
		}
		if(buffer != null) cons = buffer.getBuffer(rentype);
		Matrix4f verma = pose.last().pose();
		Matrix3f norma = pose.last().normal();
		for(Polygon poli : poly.polygons){
			for(Vertex vert : poli.vertices){
				Vector4f vec = verma.transform(new Vector4f(vert.vector.x, vert.vector.y, vert.vector.z, 1.0F));
				Vector3f norm = norma.transform(new Vector3f(vert.norm.x, vert.norm.y, vert.norm.z));
				//if(vert.color() == null){
					cons.vertex(vec.x, vec.y, vec.z, color.x, color.y, color.z, 1.0F, vert.u, vert.v, OverlayTexture.NO_OVERLAY, light, norm.x, norm.y, norm.z);
				//}
				//else{
				//	cons.vertex(vec.x, vec.y, vec.z, (vert.color()).x, (vert.color()).y, (vert.color()).z, 1.0F, vert.u, vert.v, OverlayTexture.NO_OVERLAY, light, norm.x, norm.y, norm.z);
				//}
			}
		}
		pose.popPose();
	}

	public void delete(Polyhedron<GLObject> poly){
		//
	}

}
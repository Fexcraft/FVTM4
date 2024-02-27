package net.fexcraft.mod.fvtm.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fexcraft.lib.common.Static;
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
	public static int light;

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
		VertexConsumer cons = buffer.getBuffer(rentype);
		Matrix4f verma = pose.last().pose();
		Matrix3f norma = pose.last().normal();
		for(Polygon poli : poly.polygons){
			for(Vertex vert : poli.vertices){
				Vector4f vec = verma.transform(new Vector4f(vert.vector.x, vert.vector.y, vert.vector.z, 1.0F));
				Vector3f norm = norma.transform(new Vector3f(vert.norm.x, vert.norm.y, vert.norm.z));
				if(vert.color() == null){
					cons.vertex(vec.x, vec.y, vec.z, 1.0F, 1.0F, 1.0F, 1.0F, vert.u, vert.v, OverlayTexture.NO_OVERLAY, light, norm.x, norm.y, norm.z);
				}
				else{
					cons.vertex(vec.x, vec.y, vec.z, (vert.color()).x, (vert.color()).y, (vert.color()).z, 1.0F, vert.u, vert.v, OverlayTexture.NO_OVERLAY, light, norm.x, norm.y, norm.z);
				}
			}
		}
		pose.popPose();
	}

	public void delete(Polyhedron<GLObject> poly){
		//
	}

}
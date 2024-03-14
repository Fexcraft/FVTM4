package net.fexcraft.mod.fvtm.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.common.math.V3D;
import net.fexcraft.mod.fvtm.FvtmLogger;
import net.fexcraft.mod.fvtm.FvtmRegistry;
import net.fexcraft.mod.fvtm.data.part.PartData;
import net.fexcraft.mod.fvtm.data.vehicle.SwivelPoint;
import net.fexcraft.mod.fvtm.data.vehicle.VehicleData;
import net.fexcraft.mod.fvtm.entity.RootVehicle;
import net.fexcraft.mod.fvtm.model.Model;
import net.fexcraft.mod.fvtm.model.RenderCache;
import net.fexcraft.mod.fvtm.util.FvtmAttachments;
import net.fexcraft.mod.fvtm.util.Rot;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Map;

import static net.fexcraft.mod.fvtm.model.DefaultModel.RENDERDATA;
import static net.fexcraft.mod.fvtm.render.Renderer120.*;
import static net.fexcraft.mod.fvtm.util.FvtmAttachments.RENDERCACHE;
import static net.fexcraft.mod.fvtm.util.MathUtils.valDeg;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class RVRenderer extends EntityRenderer<RootVehicle> {

	public RVRenderer(EntityRendererProvider.Context context){
		super(context);
		shadowRadius = 0.25F;
	}

	@Override
	public void render(RootVehicle veh, float yaw, float tick, PoseStack pose, MultiBufferSource buffer, int light){
		pose.pushPose();
		//pose.translate(0, 0, 0);
		V3D rot = getRotations(veh, tick);
		pose.mulPose(new Quaternionf()
			.rotateAxis((float)Static.toRadians(-rot.x), AY)
			.rotateAxis((float)Static.toRadians(rot.y), AX)
			.rotateAxis((float)Static.toRadians(rot.z), AZ)
		);
		Renderer120.pose = pose;
		Renderer120.buffer = buffer;
		Renderer120.light = light;
		//
		pose.pushPose();
		//TODO RenderCache cache = null;
		Model vehmod = veh.vehicle.data.getType().getModel();
		Renderer120.rentype = RenderType.entityCutout(veh.vehicle.data.getCurrentTexture().local());
		RenderCache cache = veh.getData(RENDERCACHE);
		if(vehmod != null){
			pose.pushPose();
			vehmod.render(RENDERDATA.set(veh.vehicle.data, veh, cache, false, tick));
			pose.popPose();
		}
		else{
			FvtmLogger.LOGGER.info("NO MODEL " + veh.getId() + " " + veh.vehicle.data.getType().getID());
			//TODO render "missing model" model
		}
		if(veh.vehicle.data.getParts().size() > 0){
			renderPoint(pose, veh.vehicle.point, veh, veh.vehicle.data, cache, tick);
		}
		//TODO install info
		pose.popPose();
		//
		//TODO toggle info
		//TODO containers
		//TODO debug seats
		pose.popPose();
	}

	private V3D getRotations(RootVehicle veh, float ticks){
		double yaw = valDeg(veh.vehicle.pivot().deg_yaw() - veh.yRotO);
		double pitch = valDeg(veh.vehicle.pivot().deg_pitch() - veh.xRotO);
		double roll = valDeg(veh.vehicle.pivot().deg_roll() - veh.protZ);
		return new V3D(veh.yRotO + yaw * ticks, veh.xRotO + pitch * ticks, veh.protZ + roll * ticks);
	}

	public static V3D getRotations(SwivelPoint point, float ticks){
		double yaw = valDeg(point.getPivot().deg_yaw() - point.getPrevPivot().deg_yaw());
		double pitch = valDeg(point.getPivot().deg_pitch() - point.getPrevPivot().deg_pitch());
		double roll = valDeg(point.getPivot().deg_roll() - point.getPrevPivot().deg_roll());
		return new V3D(point.getPrevPivot().deg_yaw() + yaw * ticks, point.getPrevPivot().deg_pitch() + pitch * ticks, point.getPrevPivot().deg_roll() + roll * ticks);
	}

	public static void renderPoint(PoseStack pose, SwivelPoint point, RootVehicle vehicle, VehicleData data, RenderCache cache, float ticks){
		ArrayList<Map.Entry<String, PartData>> parts = data.sorted_parts.get(point.id);
		if(parts == null) return;
		boolean veh = false;
		pose.pushPose();
		if(!(veh = point.isVehicle())){
			V3D temp0 = point.getPos();
			V3D temp1 = point.getPrevPos();
			V3D temp2 = new V3D(temp1.x + (temp0.x - temp1.x) * ticks, temp1.y + (temp0.y - temp1.y) * ticks, temp1.z + (temp0.z - temp1.z) * ticks);
			V3D rot = getRotations(point, ticks);
			pose.translate(temp2.x, temp2.y, temp2.z);
			pose.mulPose(new Quaternionf()
				.rotateAxis((float)Static.toRadians(rot.x), AY)
				.rotateAxis((float)Static.toRadians(rot.y), AX)
				.rotateAxis((float)Static.toRadians(rot.z), AZ)
			);
		}
		for(Map.Entry<String, PartData> entry : parts){
			pose.pushPose();
			Renderer120.rentype = RenderType.entityCutout(entry.getValue().getCurrentTexture().local());
			translate(pose, entry.getValue().getInstalledPos());
			rotate(pose, entry.getValue().getInstalledRot());
			entry.getValue().getType().getModel().render(RENDERDATA.set(data, vehicle, cache, entry.getValue(), entry.getKey(), false, ticks));
			pose.popPose();
		}
		for(SwivelPoint sub : point.subs) renderPoint(pose, sub, vehicle, data, cache, ticks);
		pose.popPose();
	}

	private static void translate(PoseStack pose, V3D pos){
		pose.translate(pos.x, pos.y, pos.z);
	}

	private static void rotate(PoseStack pose, Rot rot){
		rot.rotate112();
		Quaternionf q = new Quaternionf();
        if(rot.vec().y != 0f) q.rotateAxis((float)Static.toRadians(rot.vec().y), AY);
        if(rot.vec().x != 0f) q.rotateAxis((float)Static.toRadians(rot.vec().x), AX);
        if(rot.vec().z != 0f) q.rotateAxis((float)Static.toRadians(rot.vec().z), AZ);
		pose.mulPose(q);
	}

	@Override
	public ResourceLocation getTextureLocation(RootVehicle entity){
		return FvtmRegistry.WHITE_TEXTURE.local();
	}

}
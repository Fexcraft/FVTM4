package net.fexcraft.mod.fvtm.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fexcraft.mod.fvtm.entity.RootVehicle;
import net.fexcraft.mod.fvtm.render.FvtmRenderTypes;
import net.fexcraft.mod.fvtm.render.Renderer120;
import net.fexcraft.mod.fvtm.util.DebugUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
@Mod.EventBusSubscriber(modid = "fvtm", bus = Mod.EventBusSubscriber.Bus.FORGE, value = { Dist.CLIENT })
public class ForgeClientEvents {

	//@SubscribeEvent
	public static void onPlayerRender(RenderPlayerEvent.Pre event){
		//Renderer120.pose = event.getPoseStack();
		//Renderer120.buffer = event.getMultiBufferSource();
		//Renderer120.light = event.getPackedLight();
		if(!event.getRenderer().getModel().body.hasChild("fvtm")){
			event.getRenderer().getModel().body.children.put("fvtm", new ModelPart(new ArrayList<>(), new HashMap<>()) {
				@Override
				public void render(PoseStack pose, VertexConsumer cons, int i, int j){
					render(pose, cons, i, j, 1.0F, 1.0F, 1.0F, 1.0F);
				}

				@Override
				public void render(PoseStack pose, VertexConsumer cons, int i, int j, float k, float l, float m, float n){
					Renderer120.set(pose, cons, i, j);
					FvtmRenderTypes.setLines();
					DebugUtils.SPHERE.render();
				}
			});
		}
		//Renderer120.rentype = RenderType.entityCutout(data.getCurrentTexture().local());
		//data.getType().getModel().render(DefaultModel.RENDERDATA);
	}

	//@SubscribeEvent
	public static void onLevelRender(RenderLevelStageEvent event){
		if(event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) return;
		Renderer120.set(event.getPoseStack(), Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.lines()), 0);
		FvtmRenderTypes.setLines();
		DebugUtils.SPHERE.render();
	}

	private static RootVehicle vehicle;

	@SubscribeEvent
	public static void onLevelRender(RenderGuiOverlayEvent event){
		if(Minecraft.getInstance().player.getVehicle() instanceof RootVehicle && event.getOverlay().id().getPath().equals("hotbar")){
			vehicle = (RootVehicle)Minecraft.getInstance().player.getVehicle();
			event.getGuiGraphics().drawString(Minecraft.getInstance().font, "Throttle: " + round(vehicle.vehicle.throttle), 10, 10, 0xffffff);
			event.getGuiGraphics().drawString(Minecraft.getInstance().font, "Steering: " + round(vehicle.vehicle.steer_yaw), 10, 20, 0xffffff);
			event.getGuiGraphics().drawString(Minecraft.getInstance().font, "Speed: " + round(vehicle.vehicle.speed), 10, 30, 0xffffff);
		}
	}

	private static double round(double var){
		return (int)(var * 100) / 100D;
	}

}

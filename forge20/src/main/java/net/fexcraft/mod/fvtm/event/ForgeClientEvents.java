package net.fexcraft.mod.fvtm.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fexcraft.mod.fvtm.render.Renderer120;
import net.fexcraft.mod.fvtm.util.DebugUtils;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
@Mod.EventBusSubscriber(modid = "fvtm", bus = Mod.EventBusSubscriber.Bus.FORGE, value = { Dist.CLIENT })
public class ForgeClientEvents {

	@SubscribeEvent
	public static void onPlayerRender(RenderPlayerEvent.Pre event){
		Renderer120.pose = event.getPoseStack();
		Renderer120.buffer = event.getMultiBufferSource();
		Renderer120.light = event.getPackedLight();
		if(!event.getRenderer().getModel().body.hasChild("fvtm")){
			event.getRenderer().getModel().body.children.put("fvtm", new ModelPart(new ArrayList<>(), new HashMap<>()) {
				@Override
				public void render(PoseStack pose, VertexConsumer cons, int i, int j){
					render(pose, cons, i, j, 1.0F, 1.0F, 1.0F, 1.0F);
				}

				@Override
				public void render(PoseStack pose, VertexConsumer cons, int i, int j, float k, float l, float m, float n){
					Renderer120.pose = pose;
					Renderer120.cons = cons;
					Renderer120.buffer = null;
					DebugUtils.SPHERE.render();
				}
			});
		}
		//Renderer120.rentype = RenderType.entityCutout(data.getCurrentTexture().local());
		//data.getType().getModel().render(DefaultModel.RENDERDATA);
	}

}

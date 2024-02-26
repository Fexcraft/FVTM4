package net.fexcraft.mod.fvtm.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fexcraft.lib.common.Static;
import net.fexcraft.mod.fvtm.FvtmLogger;
import net.fexcraft.mod.fvtm.FvtmRegistry;
import net.fexcraft.mod.fvtm.data.DecorationData;
import net.fexcraft.mod.fvtm.entity.Decoration;
import net.fexcraft.mod.fvtm.model.DefaultModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DecoRenderer extends EntityRenderer<Decoration> {

	public DecoRenderer(EntityRendererProvider.Context context){
		super(context);
		shadowRadius = 0.125F;
	}

	@Override
	public void render(Decoration deco, float yaw, float tick, PoseStack pose, MultiBufferSource buffer, int light){
		pose.pushPose();
		pose.translate(0.0F, 0.5F, 0.0F);
		Renderer120.pose = pose;
		Renderer120.buffer = buffer;
		Renderer120.light = light;
		for(DecorationData data : deco.decos){
			if(data.model == null){
				FvtmLogger.LOGGER.debug(data.modelid);
				continue;
			}
			Renderer120.rentype = RenderType.entityCutout(data.textures.get(data.seltex).local());
			pose.pushPose();
			pose.translate(data.offset.x16, data.offset.y16, data.offset.z16);
			if(data.rotx != 0.0F || data.roty != 0.0F || data.rotz != 0.0F){
				pose.mulPose((new Quaternionf()).rotationZYX(Static.toRadians(data.rotz), Static.toRadians(data.roty), Static.toRadians(data.rotx)));
			}
			pose.scale(data.sclx, data.scly, data.sclz);
			data.model.render(DefaultModel.RENDERDATA);
			pose.popPose();
		}
		pose.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(Decoration entity){
		return FvtmRegistry.WHITE_TEXTURE.local();
	}

}
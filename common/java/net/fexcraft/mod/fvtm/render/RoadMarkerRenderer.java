package net.fexcraft.mod.fvtm.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fexcraft.mod.fvtm.FvtmRegistry;
import net.fexcraft.mod.fvtm.entity.RoadMarker;
import net.fexcraft.mod.fvtm.entity.WheelEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class RoadMarkerRenderer extends EntityRenderer<RoadMarker> {

	public RoadMarkerRenderer(EntityRendererProvider.Context context){
		super(context);
		shadowRadius = 0.125F;
	}

	@Override
	public void render(RoadMarker deco, float yaw, float tick, PoseStack pose, MultiBufferSource buffer, int light){
		//
	}

	@Override
	public ResourceLocation getTextureLocation(RoadMarker entity){
		return FvtmRegistry.WHITE_TEXTURE.local();
	}

}
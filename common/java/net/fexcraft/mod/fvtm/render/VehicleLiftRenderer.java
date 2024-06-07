package net.fexcraft.mod.fvtm.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fexcraft.lib.common.Static;
import net.fexcraft.mod.fvtm.block.VehicleLiftEntity;
import net.fexcraft.mod.fvtm.data.block.BlockType;
import net.fexcraft.mod.fvtm.data.vehicle.VehicleData;
import net.fexcraft.mod.fvtm.model.block.Lift2024Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;

import static net.fexcraft.mod.fvtm.render.Renderer120.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class VehicleLiftRenderer implements BlockEntityRenderer<VehicleLiftEntity> {

	public static final ResourceLocation TEXTURE = new ResourceLocation("fvtm:textures/block/lift2024.png");
	private VehicleData data;

	@Override
	public void render(VehicleLiftEntity tile, float ticks, PoseStack pose, MultiBufferSource buffer, int light, int overlay){
		Renderer120.pose = pose;
		Renderer120.set(pose, buffer, light, overlay);
		Renderer120.set(RenderType.entityCutout(TEXTURE));
		pose.pushPose();
		pose.translate(0.5, 0, 0.5);
		if(tile.rot != 0){
			pose.mulPose(new Quaternionf().rotateAxis((float)Static.toRadians(BlockType.GENERIC_4ROT.getRotationFor(tile.rot)), AY));
		}
		ChestRenderer e;
		Lift2024Model.center.render();
		data = tile.getVehicleData();
		if(data != null){

		}
		pose.popPose();
	}

	@Override
	public int getViewDistance(){
        return 128;
    }

}

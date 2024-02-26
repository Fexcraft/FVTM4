
package net.fexcraft.mod.fvtm;


import net.fexcraft.mod.fvtm.render.DecoRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;


@Mod.EventBusSubscriber(modid = "fvtm", bus = Mod.EventBusSubscriber.Bus.MOD, value = { Dist.CLIENT })
public class ClientEvents {

	@SubscribeEvent
	public static void clientInit(FMLClientSetupEvent event){
		EntityRenderers.register(FVTM4.DECORATION_ENTITY.get(), ctx -> new DecoRenderer(ctx));
	}

}
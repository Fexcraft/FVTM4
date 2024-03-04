
package net.fexcraft.mod.fvtm.event;


import com.mojang.blaze3d.platform.InputConstants;
import net.fexcraft.mod.fvtm.FVTM4;
import net.fexcraft.mod.fvtm.entity.RootVehicle;
import net.fexcraft.mod.fvtm.render.DecoRenderer;
import net.fexcraft.mod.fvtm.render.RVRenderer;
import net.fexcraft.mod.fvtm.render.WheelRenderer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
@Mod.EventBusSubscriber(modid = "fvtm", bus = Mod.EventBusSubscriber.Bus.MOD, value = { Dist.CLIENT })
public class ClientEvents {

	protected static Minecraft minecraft;
	//
	public static KeyMapping engine_toggle;
	public static final String category = "keycompound.fvtm.controls";

	@SubscribeEvent
	public static void clientInit(FMLClientSetupEvent event){
		EntityRenderers.register(FVTM4.DECORATION_ENTITY.get(), ctx -> new DecoRenderer(ctx));
		EntityRenderers.register(FVTM4.WHEEL_ENTITY.get(), ctx -> new WheelRenderer(ctx));
		EntityRenderers.register(FVTM4.VEHICLE_ENTITY.get(), ctx -> new RVRenderer(ctx));
		//
		minecraft = Minecraft.getInstance();
	}

	@SubscribeEvent
	public static void registerKeys(RegisterKeyMappingsEvent event){
		event.register(engine_toggle = new KeyMapping("key.fvtm.engine", KeyConflictContext.VEHICLE, InputConstants.Type.KEYSYM, InputConstants.KEY_RCONTROL, "fvtm"));
	}

    public enum KeyConflictContext implements IKeyConflictContext {

    	VEHICLE {
    		@Override
    		public boolean isActive(){
    			return minecraft.player != null && minecraft.player.getVehicle() instanceof RootVehicle;
    		}
    		@Override
    		public boolean conflicts(IKeyConflictContext other){
    			return other == this;
    		}
    	},
    	TOGGABLE {
    		@Override
    		public boolean isActive(){
    			return false;//TODO
    		}
    		@Override
    		public boolean conflicts(IKeyConflictContext other){
    			return other == this;
    		}
    	}

    }

}
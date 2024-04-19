package net.fexcraft.mod.fvtm.event;

import net.fexcraft.mod.fvtm.sys.road.RoadPlacingCache;
import net.fexcraft.mod.fvtm.sys.road.RoadPlacingUtil;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
@Mod.EventBusSubscriber(modid = "fvtm", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerEvents {

	@SubscribeEvent
	public static void onPlayerIn(PlayerEvent.PlayerLoggedInEvent event){
		if(event.getEntity().level().isClientSide){
			RoadPlacingUtil.CL_CURRENT = null;
		}
		else{
			RoadPlacingCache.onLogIn(event.getEntity().getGameProfile().getId());
		}
	}

	@SubscribeEvent
	public static void onPlayerOut(PlayerEvent.PlayerLoggedOutEvent event){
		if(!event.getEntity().level().isClientSide){
			RoadPlacingCache.onLogOut(event.getEntity().getGameProfile().getId());
		}
	}

}

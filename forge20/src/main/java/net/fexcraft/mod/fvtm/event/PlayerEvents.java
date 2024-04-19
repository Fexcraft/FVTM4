package net.fexcraft.mod.fvtm.event;

import net.fexcraft.mod.fcl.util.PassengerUtil;
import net.fexcraft.mod.fvtm.entity.RootVehicle;
import net.fexcraft.mod.fvtm.handler.InteractionHandler;
import net.fexcraft.mod.fvtm.sys.road.RoadPlacingCache;
import net.fexcraft.mod.fvtm.sys.road.RoadPlacingUtil;
import net.fexcraft.mod.fvtm.sys.uni.KeyPress;
import net.fexcraft.mod.fvtm.sys.uni.Passenger;
import net.fexcraft.mod.fvtm.sys.uni.SeatInstance;
import net.fexcraft.mod.uni.item.StackWrapper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.fexcraft.mod.fvtm.event.ClientEvents.*;

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

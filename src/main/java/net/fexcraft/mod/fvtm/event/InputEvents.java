package net.fexcraft.mod.fvtm.event;

import net.fexcraft.mod.fcl.UniversalAttachments;
import net.fexcraft.mod.fvtm.entity.RootVehicle;
import net.fexcraft.mod.fvtm.sys.uni.KeyPress;
import net.fexcraft.mod.fvtm.sys.uni.Passenger;
import net.fexcraft.mod.fvtm.sys.uni.SeatInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;

import static net.fexcraft.mod.fvtm.event.ClientEvents.minecraft;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
@Mod.EventBusSubscriber(modid = "fvtm", bus = Mod.EventBusSubscriber.Bus.FORGE, value = { Dist.CLIENT })
public class InputEvents {

	@SubscribeEvent
	public static void clientTick(TickEvent.ClientTickEvent event){
		if(minecraft.player == null || minecraft.level == null) return;
		switch(event.phase){
			case START:{
				if(minecraft.player.getVehicle() instanceof RootVehicle){
					handleKeyboardInput();
				}
			}
			case END:{
				//
				break;
			}
		}
	}

	private static void handleKeyboardInput(){
		Passenger player = (Passenger)minecraft.player.getData(UniversalAttachments.PASSENGER);
		SeatInstance seat = ((RootVehicle)minecraft.player.getVehicle()).getSeatOf(minecraft.player);
		if(seat == null) return;
		if(minecraft.options.keyUp.isDown()){
			seat.onKeyPress(seat.root.type.isAirVehicle() ? KeyPress.TURN_DOWN : KeyPress.ACCELERATE, player);
		}
		if(minecraft.options.keyDown.isDown()){
			seat.onKeyPress(seat.root.type.isAirVehicle() ? KeyPress.TURN_UP : KeyPress.DECELERATE, player);
		}
		if(minecraft.options.keyLeft.isDown()){
			seat.onKeyPress(KeyPress.TURN_LEFT, player);
		}
		if(minecraft.options.keyRight.isDown()){
			seat.onKeyPress(KeyPress.TURN_RIGHT, player);
		}
	}

}

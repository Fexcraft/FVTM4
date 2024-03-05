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

import static net.fexcraft.mod.fvtm.event.ClientEvents.*;

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
		boolean u12 = false;
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
		if(arrow_up.isDown()){
			seat.onKeyPress(seat.root.type.isAirVehicle() ? KeyPress.ACCELERATE : KeyPress.GEAR_UP, player);
		}
		if(arrow_down.isDown()){
			seat.onKeyPress(seat.root.type.isAirVehicle() ? KeyPress.DECELERATE : KeyPress.GEAR_DOWN, player);
		}
		if(arrow_left.isDown()){
			seat.onKeyPress(KeyPress.ROLL_LEFT, player);
		}
		if(arrow_right.isDown()){
			seat.onKeyPress(KeyPress.ROLL_RIGHT, player);
		}
		if(u12){
			if(pbrake.isDown()){
				seat.onKeyPress(KeyPress.PBRAKE, player);
			}
			boolean state = brake.isDown();
			if(state != seat.root.getKeyPressState(KeyPress.BRAKE)){
				seat.root.onKeyPress(KeyPress.BRAKE, seat.seat, player, state);
			}
		}
		else{
			if(brake.isDown()){
				seat.onKeyPress(KeyPress.BRAKE, player);
			}
		}
		if(engine_toggle.isDown()){
			seat.onKeyPress(KeyPress.ENGINE, player);
		}
		if(minecraft.options.keyShift.isDown()){
			seat.onKeyPress(KeyPress.DISMOUNT, player);
		}
		if(inventory_open.isDown()){
			seat.onKeyPress(KeyPress.INVENTORY, player);
		}
		if(toggables.isDown()){
			seat.onKeyPress(KeyPress.TOGGABLES, player);
		}
		if(script_ui.isDown()){
			seat.onKeyPress(KeyPress.SCRIPTS, player);
		}
		if(lights_toggle.isDown()){
			seat.onKeyPress(KeyPress.LIGHTS, player);
		}
		if(trailer_toggle.isDown()){
			seat.onKeyPress(KeyPress.COUPLER_REAR, player);
		}
		if(wagon_toggle.isDown()){
			seat.onKeyPress(KeyPress.COUPLER_FRONT, player);
		}
		if(reset.isDown()){
			seat.onKeyPress(KeyPress.RESET, player);
		}
	}

}

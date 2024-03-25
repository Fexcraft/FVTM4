package net.fexcraft.mod.fvtm.impl;

import net.fexcraft.lib.common.math.V3D;
import net.fexcraft.mod.fvtm.packet.PacketBase;
import net.fexcraft.mod.fvtm.sys.uni.Passenger;
import net.fexcraft.mod.fvtm.sys.uni.VehicleInstance;
import net.fexcraft.mod.uni.tag.TagCW;
import net.fexcraft.mod.uni.world.WorldW;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Packets20F extends Packets20 {

	@Override
	public void send(VehicleInstance vehicle, TagCW com){

	}

	@Override
	public void send0(Class<? extends PacketBase> packet, Object... data){

	}

	@Override
	public void sendInRange0(Class<? extends PacketBase> packet, WorldW world, V3D pos, int range, Object... data){

	}

	@Override
	public void sendToAll0(Class<? extends PacketBase> packet, Object... data){

	}

	@Override
	public void sendTo0(Class<? extends PacketBase> packet, Passenger to, Object... data){

	}

}
